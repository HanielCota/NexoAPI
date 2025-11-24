package com.hanielcota.nexoapi.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NexoLegacyChatColors {

    private static final MiniMessage MINI_MESSAGE = buildMiniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = buildLegacySerializer();
    private static final PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = buildPlainTextSerializer();

    private NexoLegacyChatColors() {
        throw new UnsupportedOperationException("Utility class.");
    }

    public static Component componentFromLegacy(@NotNull LegacyText legacyText) {
        Objects.requireNonNull(legacyText, "Legacy text cannot be null.");

        boolean empty = legacyText.isEmpty();
        if (empty) {
            return Component.empty();
        }

        String textValue = legacyText.value();
        return LEGACY_SERIALIZER.deserialize(textValue);
    }

    public static MiniMessageText miniMessageFromLegacy(@NotNull LegacyText legacyText) {
        Objects.requireNonNull(legacyText, "Legacy text cannot be null.");

        Component component = componentFromLegacy(legacyText);
        String serialized = MINI_MESSAGE.serialize(component);

        return new MiniMessageText(serialized);
    }

    public static MiniMessageText miniMessageWithRole(
            @NotNull NexoColorRole role,
            @NotNull LegacyText legacyText
    ) {
        Objects.requireNonNull(role, "Color role cannot be null.");
        Objects.requireNonNull(legacyText, "Legacy text cannot be null.");

        MiniMessageText baseText = miniMessageFromLegacy(legacyText);
        return role.wrap(baseText);
    }

    public static Component componentWithRolePrefix(
            @NotNull NexoColorRole role,
            @NotNull LegacyText prefixText,
            @NotNull LegacyText legacyBodyText
    ) {
        Objects.requireNonNull(role, "Color role cannot be null.");
        Objects.requireNonNull(prefixText, "Prefix text cannot be null.");
        Objects.requireNonNull(legacyBodyText, "Body text cannot be null.");

        Component bodyComponent = componentFromLegacy(legacyBodyText);

        String prefixPlainText = stripLegacyToPlain(prefixText);
        TextColor prefixColor = role.asTextColor();

        Component prefixComponent = Component.text(prefixPlainText);
        Component coloredPrefixComponent = prefixComponent.color(prefixColor);

        Component spaceComponent = Component.text(" ");
        Component prefixWithSpace = coloredPrefixComponent.append(spaceComponent);

        return prefixWithSpace.append(bodyComponent);
    }

    public static String stripLegacyToPlain(@NotNull LegacyText legacyText) {
        Objects.requireNonNull(legacyText, "Legacy text cannot be null.");

        String textValue = legacyText.value();
        Component component = LEGACY_SERIALIZER.deserialize(textValue);

        return PLAIN_TEXT_SERIALIZER.serialize(component);
    }

    private static MiniMessage buildMiniMessage() {
        return MiniMessage.miniMessage();
    }

    private static LegacyComponentSerializer buildLegacySerializer() {
        LegacyComponentSerializer.Builder builder = LegacyComponentSerializer.builder();
        LegacyComponentSerializer.Builder withCharacter = builder.character('&');
        LegacyComponentSerializer.Builder withHexCharacter = withCharacter.hexCharacter('#');

        return withHexCharacter.build();
    }

    private static PlainTextComponentSerializer buildPlainTextSerializer() {
        return PlainTextComponentSerializer.plainText();
    }
}
