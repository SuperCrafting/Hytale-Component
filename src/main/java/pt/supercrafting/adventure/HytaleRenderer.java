package pt.supercrafting.adventure;

import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.VirtualComponentRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

record HytaleRenderer(FormattedMessage formatted, Component component) implements VirtualComponentRenderer<Void> {

    HytaleRenderer(FormattedMessage formatted, Component component) {
        this.formatted = formatted.clone();
        this.component = component;
    }

    public static FormattedMessage unbox(Component component) {
        if (!(component instanceof VirtualComponent vc))
            return null;
        if (!(vc.renderer() instanceof HytaleRenderer hytaleRenderer))
            return null;
        return hytaleRenderer.formatted();
    }

    @Override
    public FormattedMessage formatted() {
        return this.formatted.clone();
    }

    @Override
    public @UnknownNullability ComponentLike apply(@NotNull Void context) {
        return Component.empty();
    }

    @Override
    public @NotNull String fallbackString() {
        return "";
        //return "hytale_virtual_component";
        //return Message.CODEC.encode(message(), new ExtraInfo()).toString();
    }

}
