package pt.supercrafting.adventure.tag.resolver;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import pt.supercrafting.adventure.tag.HytaleTag;

public sealed interface HytaleTagResolver extends TagResolver permits HytaleTagResolverImpl {

    static Builder builder() {
        return new HytaleTagResolverImpl.BuilderImpl();
    }

    static TagResolver standard() {
        return HytaleTagResolverImpl.STANDARD;
    }

    sealed interface Builder extends AbstractBuilder<HytaleTagResolver> permits HytaleTagResolverImpl.BuilderImpl {

        Builder name(String name);

        Builder tag(HytaleTag tag);

        Builder tags(Iterable<? extends HytaleTag> tags);

        Builder tags(HytaleTag... tags);

        HytaleTagResolver build();

    }

}
