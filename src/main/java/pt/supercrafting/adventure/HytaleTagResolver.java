package pt.supercrafting.adventure;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public interface HytaleTagResolver {

    TagResolver asTagResolver();

    static Builder builder() {
        return new HytaleTagResolverImpl.BuilderImpl();
    }

    static TagResolver standard() {
        return HytaleTagResolverImpl.STANDARD;
    }

    interface Builder extends AbstractBuilder<HytaleTagResolver> {

        Builder name(String name);

        Builder tag(HytaleTag tag);

        Builder tags(Iterable<? extends HytaleTag> tags);

        Builder tags(HytaleTag... tags);

        HytaleTagResolver build();

    }

}
