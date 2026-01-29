package pt.supercrafting.hytale.adventure.util;

import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.hytale.adventure.serializer.HytaleComponentSerializer;

public final class HytaleDirectConverter {

    private static final MiniMessage MINI_MESSAGE = HytaleComponentSerializer.miniMessage();
    private static final HytaleComponentSerializer SERIALIZER = HytaleComponentSerializer.get();

    private HytaleDirectConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Message fromMiniMessage(@NotNull String text) {
        Component component = MINI_MESSAGE.deserialize(text);
        return SERIALIZER.serializeToMessage(component);
    }

    public static String toMiniMessage(@NotNull Message message) {
        Component component = SERIALIZER.deserializeFromMessage(message);
        return MINI_MESSAGE.serialize(component);
    }

}