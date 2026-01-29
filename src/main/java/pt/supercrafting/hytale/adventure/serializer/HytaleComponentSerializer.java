package pt.supercrafting.hytale.adventure.serializer;

import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.MaybeBool;
import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.VirtualComponentRenderer;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.ApiStatus;

public sealed interface HytaleComponentSerializer extends ComponentSerializer<Component, Component, FormattedMessage> permits HytaleComponentSerializerImpl{

    static ComponentFlattener flattener() {
        return HytaleComponentSerializerImpl.FLATTENER;
    }

    static MiniMessage miniMessage() {
        return HytaleComponentSerializerImpl.MINI_MESSAGE;
    }

    static PlainTextComponentSerializer plainText() {
        return HytaleComponentSerializerImpl.PLAIN_TEXT;
    }

    static HytaleComponentSerializer get() {
        return HytaleComponentSerializerImpl.INSTANCE;
    }

    default Message serializeToMessage(Component input) {
        return new Message(serialize(input));
    }

    default Component deserializeFromMessage(Message input) {
        return deserialize(input.getFormattedMessage());
    }

    @ApiStatus.Internal
    static FormattedMessage unbox(Component component) {
        if (!(component instanceof VirtualComponent vc))
            return null;
        if (!(vc.renderer() instanceof HytaleRenderer hytaleRenderer))
            return null;
        return hytaleRenderer.formatted();
    }

    @ApiStatus.Internal
    static VirtualComponentRenderer<Void> box(FormattedMessage message, Component component) {
        return new HytaleRenderer(message, component);
    }

    @ApiStatus.Internal
    static MaybeBool fromState(TextDecoration.State state) {
        return switch (state) {
            case TRUE -> MaybeBool.True;
            case FALSE -> MaybeBool.False;
            default -> MaybeBool.Null;
        };
    }

}
