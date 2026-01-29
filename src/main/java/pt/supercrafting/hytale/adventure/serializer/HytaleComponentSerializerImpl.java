package pt.supercrafting.hytale.adventure.serializer;

import com.hypixel.hytale.protocol.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.hytale.adventure.tag.resolver.HytaleTagResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class HytaleComponentSerializerImpl implements HytaleComponentSerializer {

    public static final HytaleComponentSerializerImpl INSTANCE = new HytaleComponentSerializerImpl();

    public static final ComponentFlattener FLATTENER = ComponentFlattener.basic().toBuilder()
            .mapper(TextComponent.class, c -> {
                if(!(c instanceof VirtualComponent vc))
                    return c.content();

                if(!(vc.renderer() instanceof HytaleRenderer hytaleRenderer))
                    return vc.content();

                FormattedMessage formattedMessage = hytaleRenderer.formatted();
                return Objects.requireNonNullElse(formattedMessage.rawText, formattedMessage.messageId);
            })
            .build();

    public static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .emitVirtuals(true)
            .tags(TagResolver.builder()
                    .resolver(TagResolver.standard())
                    .resolver(HytaleTagResolver.standard())
                    .build())
            .build();

    public static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.builder()
            .flattener(FLATTENER)
            .build();

    @Override
    public @NotNull Component deserialize(@NotNull FormattedMessage input) {

        Component component;

        if (input.messageId != null) {
            List<Component> args = new ArrayList<>();

            if (input.params != null) {
                for (Map.Entry<String, ParamValue> entry : input.params.entrySet()) {
                    args.add(asComponent(entry.getValue()));
                }
            }

            if (input.messageParams != null) {
                for (Map.Entry<String, FormattedMessage> entry : input.messageParams.entrySet()) {
                    args.add(deserialize(entry.getValue()));
                }
            }

            component = Component.translatable(input.messageId, args);
        } else {
            String text = Objects.requireNonNull(input.rawText, "");
            component = Component.text(text);
        }

        Style.Builder styleBuilder = Style.style();

        if (input.color != null) {
            TextColor color = TextColor.fromHexString(input.color);
            if (color != null)
                styleBuilder.color(color);
        }

        styleBuilder.decoration(TextDecoration.BOLD, toState(input.bold));
        styleBuilder.decoration(TextDecoration.ITALIC, toState(input.italic));
        styleBuilder.decoration(TextDecoration.UNDERLINED, toState(input.underlined));

        if (input.link != null) {
            styleBuilder.clickEvent(ClickEvent.openUrl(input.link));
        }

        component = component.style(styleBuilder);

        if (input.children != null) {
            for (FormattedMessage child : input.children)
                component = component.append(deserialize(child));
        }

        Style style = styleBuilder.build();
        return Component.virtual(Void.class, HytaleComponentSerializer.box(input, component), style).children(component.children());
    }

    @Override
    public @NotNull FormattedMessage serialize(@NotNull Component component) {

        FormattedMessage unboxed = HytaleComponentSerializer.unbox(component);
        if (unboxed != null)
            return unboxed;

        FormattedMessage formatted = new FormattedMessage();

        if(component instanceof TextComponent textComponent)
            formatted.rawText = textComponent.content();
        else if(component instanceof TranslatableComponent translatableComponent)
            formatted.messageId = translatableComponent.key();

        Style style = component.style();
        if (style.color() != null) {
            formatted.color = style.color().asHexString();
        }

        formatted.bold = HytaleComponentSerializer.fromState(style.decoration(TextDecoration.BOLD));
        formatted.italic = HytaleComponentSerializer.fromState(style.decoration(TextDecoration.ITALIC));
        formatted.underlined = HytaleComponentSerializer.fromState(style.decoration(TextDecoration.UNDERLINED));

        ClickEvent clickEvent = style.clickEvent();
        if (clickEvent != null) {
            ClickEvent.Payload payload = clickEvent.payload();
            if (payload instanceof ClickEvent.Payload.Text url) {
                formatted.link = url.value();
            }
        }

        List<FormattedMessage> children = new ArrayList<>();
        for (Component child : component.children()) {
            children.add(serialize(child));
        }

        if (!children.isEmpty()) {
            formatted.children = children.toArray(new FormattedMessage[0]);
        }

        return formatted;
    }

    public static TextDecoration.State toState(MaybeBool maybeBool) {
        return switch (maybeBool) {
            case True -> TextDecoration.State.TRUE;
            case False -> TextDecoration.State.FALSE;
            default -> TextDecoration.State.NOT_SET;
        };
    }

    private static Component asComponent(ParamValue value) {
        switch (value) {
            case StringParamValue stringValue -> {
                return stringValue.value != null ? Component.text(stringValue.value) : Component.empty();
            }
            case BoolParamValue boolValue -> {
                return Component.text(boolValue.value);
            }
            case IntParamValue intParamValue -> {
                return Component.text(intParamValue.value);
            }
            case LongParamValue longParamValue -> {
                return Component.text(longParamValue.value);
            }
            case DoubleParamValue doubleParamValue -> {
                return Component.text(doubleParamValue.value);
            }
            default -> throw new IllegalStateException("Unexpected value: " + value);
        }
    }

}
