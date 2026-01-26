package pt.supercrafting.adventure;

import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.MaybeBool;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MonospaceTag implements Modifying, HytaleTag {

    static final MonospaceTag INSTANCE = new MonospaceTag(TextDecoration.State.NOT_SET);

    private static final MonospaceTag ENABLED = new MonospaceTag(TextDecoration.State.TRUE);
    private static final MonospaceTag DISABLED = new MonospaceTag(TextDecoration.State.FALSE);

    private static final String TAG = "ms";

    private static final String REVERT = "!";

    public static final TagResolver RESOLVER;

    static {
        TagResolver.Builder resolverBuilder = TagResolver.builder();
        for (String alias : List.of(TAG, "monospace")) {
            resolverBuilder.resolver(TagResolver.resolver(alias, ENABLED));
            resolverBuilder.resolver(TagResolver.resolver(REVERT + alias, DISABLED));
        }
        RESOLVER = resolverBuilder.build();
    }

    private final TextDecoration.State state;

    private MonospaceTag(TextDecoration.State state) {
        this.state = state;
    }

    @Override
    public void claim(FormattedMessage message, TokenEmitter emitter) {
        MaybeBool ms = message.monospace;
        if(ms == MaybeBool.Null)
            return;
        emitter.tag((ms == MaybeBool.True ? "" : REVERT) + TAG);
    }

    @Override
    public Component apply(@NotNull Component current, int depth) {

        current = current.children(List.of());
        if(!Component.IS_NOT_EMPTY.test(current))
            return current;

        FormattedMessage message = HytaleComponentSerializer.formattedMessage().serialize(current);
        message.monospace = FormattedMessageComponentSerializer.fromState(state);
        return Component.virtual(Void.class, new HytaleRenderer(message, current)).style(current.style());
    }

}
