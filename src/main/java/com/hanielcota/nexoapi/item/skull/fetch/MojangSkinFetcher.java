package com.hanielcota.nexoapi.item.skull.fetch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for fetching player skin textures from Mojang's API.
 * Fetches the base64 texture value for a given player UUID.
 *
 * @since 1.0.0
 */
public final class MojangSkinFetcher {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String PROPERTIES_KEY = "properties";
    private static final String VALUE_KEY = "value";

    private MojangSkinFetcher() {
    }

    /**
     * Fetches the base64 texture string for a player UUID from Mojang's API.
     *
     * @param uuid the player UUID (with or without dashes)
     * @return a CompletableFuture that completes with the base64 texture string
     */
    public static CompletableFuture<String> fetchBase64(@NotNull String uuid) {
        if (uuid.isBlank()) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("UUID cannot be blank")
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
                .thenApply(HttpResponse::body)
                .thenApply(MojangSkinFetcher::extractTexture);
    }

    private static String extractTexture(@NotNull String json) {
        var root = parseJson(json);
        var properties = getProperties(root);
        var texture = getFirstTexture(properties);
        return extractValue(texture);
    }

    private static JsonObject parseJson(@NotNull String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    private static JsonArray getProperties(@NotNull JsonObject root) {
        return root.getAsJsonArray(PROPERTIES_KEY);
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
