package pt.supercrafting.adventure;

import com.hypixel.hytale.protocol.FormattedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Objects;

public interface HytaleComponentSerializer<H> extends ComponentSerializer<Component, Component, H> {

    ComponentFlattener FLATTENER = ComponentFlattener.basic().toBuilder()
            .mapper(TextComponent.class, c -> {
                if(!(c instanceof VirtualComponent vc))
                    return c.content();

                if(!(vc.renderer() instanceof HytaleRenderer hytaleRenderer))
                    return vc.content();

                FormattedMessage formattedMessage = hytaleRenderer.formatted();
                return Objects.requireNonNullElse(formattedMessage.rawText, formattedMessage.messageId);
            })
            .build();

    MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(HytaleTagResolver.standard())
                    .resolver(TagResolver.standard())
                    .build())
            .build();

    PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.builder()
            .flattener(FLATTENER)
            .build();

    static HytaleComponentSerializer<FormattedMessage> formattedMessage() {
        return FormattedMessageComponentSerializer.INSTANCE;
    }

}
