package pt.supercrafting.adventure.util;

import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.adventure.HytaleComponentSerializer;

public final class HytaleDirectConverter {

    private static final MiniMessage MINI_MESSAGE = HytaleComponentSerializer.miniMessage();
    private static final HytaleComponentSerializer SERIALIZER = HytaleComponentSerializer.get();

    private HytaleDirectConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Message fromMiniMessage(@NotNull String text) {
        Component component = MINI_MESSAGE.deserialize(text);
        FormattedMessage formattedMessage = SERIALIZER.serialize(component);
        return SERIALIZER.deserializeToMessage(formattedMessage);
    }

    public static String toMiniMessage(@NotNull Message message) {
        Component component = SERIALIZER.deserializeFromMessage(message);
        return MINI_MESSAGE.serialize(component);
    }

}