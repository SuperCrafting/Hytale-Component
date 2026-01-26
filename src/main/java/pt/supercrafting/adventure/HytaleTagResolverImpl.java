package pt.supercrafting.adventure;

import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.minimessage.TokenHelper;
import net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Nullable;

import java.util.*;

final class HytaleTagResolverImpl implements HytaleTagResolver {

    static final TagResolver STANDARD = TagResolver.builder()
            .resolver(HytaleTagResolver.builder()
                    .tags(
                            MonospaceTag.INSTANCE
                    )
                    .build()
                    .asTagResolver())
            .resolver(MonospaceTag.RESOLVER)
            .build();

    private final String name;
    private final List<HytaleTag> tags;
    private TagResolver tagResolver;

    private HytaleTagResolverImpl(String name, List<HytaleTag> tags) {
        this.name = name;
        this.tags = List.copyOf(tags);
    }

    @Override
    public TagResolver asTagResolver() {
        if(tagResolver == null) {
            tagResolver = TagResolver.builder()
                    .resolver(SerializableResolver.claimingComponent(name, (_, _) -> Tag.inserting(Component.empty()), this::claim))
                    .build();
        }
        return tagResolver;
    }

    @Nullable
    private Emitable claim(Component component) {

        component = component.children(List.of());

        FormattedMessage formattedMessage = HytaleRenderer.unbox(component);
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

    public static class BuilderImpl implements HytaleTagResolver.Builder {

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
