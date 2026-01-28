package pt.supercrafting.adventure.tag.resolver;

import com.hypixel.hytale.protocol.FormattedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.TokenHelper;
import net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.adventure.HytaleComponentSerializer;
import pt.supercrafting.adventure.tag.MonospaceTag;
import pt.supercrafting.adventure.tag.HytaleTag;

import java.util.*;

final class HytaleTagResolverImpl implements HytaleTagResolver {

    static final TagResolver STANDARD = HytaleTagResolver.builder()
            .tags(
                    MonospaceTag.ENABLED,
                    MonospaceTag.DISABLED
            )
            .build();

    private final String name;
    private final List<HytaleTag> tags;
    private final TagResolver tagResolver;

    private HytaleTagResolverImpl(String name, List<HytaleTag> tags) {
        this.name = name;
        this.tags = List.copyOf(tags);
        this.tagResolver = create();
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        return tagResolver.resolve(name, arguments, ctx);
    }

    @Override
    public boolean has(@NotNull String name) {
        return tagResolver.has(name);
    }

    private TagResolver create() {
        TagResolver.Builder resolverBuilder = TagResolver.builder()
                .resolver(SerializableResolver.claimingComponent(name, (_, _) -> Tag.inserting(Component.empty()), this::claim));

        if(!tags.isEmpty()) {
            for (HytaleTag tag : tags) {
                for (String name : tag.names()) {
                    resolverBuilder.tag(name, new Applicator(tag));
                }
            }
        }

        return resolverBuilder.build();
    }

    private record Applicator(HytaleTag tag) implements Modifying {

        @Override
        public Component apply(@NotNull Component current, int depth) {
            current = current.children(List.of());

            if(!Component.IS_NOT_EMPTY.test(current))
                return current;

            FormattedMessage message = HytaleComponentSerializer.get().serialize(current);
            tag.apply(message);
            return Component.virtual(Void.class, HytaleComponentSerializer.box(message, current), current.style());
        }

    }

    @Nullable
    private Emitable claim(Component component) {

        component = component.children(List.of());

        FormattedMessage formattedMessage = HytaleComponentSerializer.unbox(component);
        if (formattedMessage == null)
            return null;

        String text = Objects.requireNonNull(formattedMessage.rawText, formattedMessage.messageId);
        if (text.isBlank())
            return null;

        return emitter -> {
            for (HytaleTag tag : tags)
                tag.claim(formattedMessage, emitter);
            if(!text.isBlank())
                TokenHelper.emitUnquoted(emitter, text);
        };

    }

    public static final class BuilderImpl implements HytaleTagResolver.Builder {

        private String name = "hytale";
        private List<HytaleTag> tags;

        @Override
        public HytaleTagResolver.Builder name(String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        @Override
        public HytaleTagResolver.Builder tag(HytaleTag tag) {
            tags().add(Objects.requireNonNull(tag, "tag"));
            return this;
        }

        @Override
        public HytaleTagResolver.Builder tags(Iterable<? extends HytaleTag> tags) {
            for (HytaleTag tag : tags)
                tag(tag);
            return this;
        }

        @Override
        public HytaleTagResolver.Builder tags(HytaleTag... tags) {
            return tags(Arrays.asList(tags));
        }

        @Override
        public HytaleTagResolver build() {
            return new HytaleTagResolverImpl(name, tags == null ? List.of() : tags);
        }

        private List<HytaleTag> tags() {
            if(tags == null)
                tags = new ArrayList<>();
            return tags;
        }

    }

}
