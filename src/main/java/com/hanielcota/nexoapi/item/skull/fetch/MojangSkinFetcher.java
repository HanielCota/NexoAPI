package com.hanielcota.nexoapi.item.skull.fetch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for fetching player skin textures from Mojang's API.
 * Fetches the base64 texture value for a given player UUID.
 * <p>
 * Includes proper error handling for network issues, timeouts, and invalid responses.
 * </p>
 *
 * @since 1.0.0
 */
public final class MojangSkinFetcher {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);
    private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String PROPERTIES_KEY = "properties";
    private static final String VALUE_KEY = "value";

    private MojangSkinFetcher() {
    }

    /**
     * Fetches the base64 texture string for a player UUID from Mojang's API.
     * <p>
     * The CompletableFuture may complete exceptionally with:
     * <ul>
     *   <li>{@link IllegalArgumentException} - if UUID is invalid</li>
     *   <li>{@link HttpTimeoutException} - if the request times out</li>
     *   <li>{@link java.net.ConnectException} - if connection fails</li>
     *   <li>{@link IllegalStateException} - if the API response is invalid or missing data</li>
     *   <li>{@link JsonSyntaxException} - if the response is not valid JSON</li>
     * </ul>
     * </p>
     *
     * @param uuid the player UUID (with or without dashes)
     * @return a CompletableFuture that completes with the base64 texture string
     */
    public static CompletableFuture<String> fetchBase64(@NotNull String uuid) {
        if (uuid == null || uuid.isBlank()) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("UUID cannot be null or blank")
            );
        }

        var request = buildRequest(uuid);
        return sendRequest(request);
    }

    private static HttpRequest buildRequest(@NotNull String uuid) {
        var cleanUuid = removeDashes(uuid);
        var uri = createUri(cleanUuid);
        return HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(REQUEST_TIMEOUT)
                .build();
    }

    private static String removeDashes(@NotNull String uuid) {
        return uuid.replace("-", "");
    }

    private static URI createUri(@NotNull String uuid) {
        return URI.create(BASE_URL + uuid);
    }

    private static CompletableFuture<String> sendRequest(@NotNull HttpRequest request) {
        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(MojangSkinFetcher::validateResponse)
                .thenApply(HttpResponse::body)
                .thenApply(MojangSkinFetcher::extractTexture)
                .exceptionally(throwable -> {
                    Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
                    
                    if (cause instanceof HttpTimeoutException) {
                        throw new RuntimeException("Request to Mojang API timed out after " + 
                                REQUEST_TIMEOUT.toSeconds() + " seconds", cause);
                    }
                    
                    if (cause instanceof java.net.ConnectException || 
                        cause instanceof java.net.UnknownHostException) {
                        throw new RuntimeException("Failed to connect to Mojang API: " + 
                                cause.getMessage(), cause);
                    }
                    
                    if (cause instanceof java.io.IOException) {
                        throw new RuntimeException("Network error while fetching skin from Mojang API: " + 
                                cause.getMessage(), cause);
                    }
                    
                    // Re-throw other exceptions as-is
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    }
                    
                    throw new RuntimeException("Unexpected error while fetching skin from Mojang API", cause);
                });
    }

    private static HttpResponse<String> validateResponse(@NotNull HttpResponse<String> response) {
        int statusCode = response.statusCode();
        
        if (statusCode < 200 || statusCode >= 300) {
            String errorMessage = switch (statusCode) {
                case 404 -> "Player profile not found (UUID may be invalid)";
                case 429 -> "Rate limit exceeded for Mojang API";
                case 500, 502, 503, 504 -> "Mojang API server error (status: " + statusCode + ")";
                default -> "Unexpected HTTP status from Mojang API: " + statusCode;
            };
            throw new IllegalStateException(errorMessage);
        }
        
        String body = response.body();
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("Empty response body from Mojang API");
        }
        
        return response;
    }

    private static String extractTexture(@NotNull String json) {
        try {
            var root = parseJson(json);
            var properties = getProperties(root);
            var texture = getFirstTexture(properties);
            return extractValue(texture);
        } catch (JsonSyntaxException e) {
            throw new IllegalStateException("Invalid JSON response from Mojang API: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            // Re-throw IllegalStateException as-is (already has proper message)
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse Mojang API response: " + e.getMessage(), e);
        }
    }

    private static JsonObject parseJson(@NotNull String json) {
        try {
            return JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new IllegalStateException("Invalid JSON format in Mojang API response", e);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("JSON response is not an object", e);
        }
    }

    private static JsonArray getProperties(@NotNull JsonObject root) {
        if (!root.has(PROPERTIES_KEY)) {
            throw new IllegalStateException("Missing 'properties' field in Mojang API response");
        }
        
        try {
            return root.getAsJsonArray(PROPERTIES_KEY);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("'properties' field is not an array in Mojang API response", e);
        }
    }

    private static JsonObject getFirstTexture(@NotNull JsonArray properties) {
        if (properties.isEmpty()) {
            throw new IllegalStateException("No properties found in Mojang API response");
        }
        return properties.get(0).getAsJsonObject();
    }

    private static String extractValue(@NotNull JsonObject texture) {
        var valueElement = texture.get(VALUE_KEY);
        if (valueElement == null) {
            throw new IllegalStateException("No value found in texture property");
        }
        return valueElement.getAsString();
    }
}
