package pt.supercrafting.hytale.adventure.tag;

import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.MaybeBool;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import pt.supercrafting.hytale.adventure.serializer.HytaleComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class MonospaceTag implements HytaleTag {

    private static final String TAG = "ms";

    private static final String REVERT = "!";

    public static final MonospaceTag ENABLED = new MonospaceTag(Set.of(TAG, "mono", "monospace"), TextDecoration.State.TRUE);
    public static final MonospaceTag DISABLED;

    static {
        List<String> revertNames = new ArrayList<>();
        for (String name : ENABLED.names)
            revertNames.add(REVERT + name);
        DISABLED = new MonospaceTag(Set.copyOf(revertNames), TextDecoration.State.FALSE);
    }

    private final Set<String> names;
    private final TextDecoration.State state;

    private MonospaceTag(Set<String> names, TextDecoration.State state) {
        this.names = names;
        this.state = state;
    }

    @Override
    public Set<String> names() {
        return names;
    }

    @Override
    public void claim(FormattedMessage message, TokenEmitter emitter) {
        MaybeBool ms = message.monospace;
        if(ms == MaybeBool.Null)
            return;
        if (state == TextDecoration.State.TRUE && ms == MaybeBool.True) {
            emitter.tag(TAG);
        } else if (state == TextDecoration.State.FALSE && ms == MaybeBool.False) {
            emitter.tag(REVERT + TAG);
        }
    }

    @Override
    public void apply(FormattedMessage message) {
        message.monospace = HytaleComponentSerializer.fromState(state);
    }

}
