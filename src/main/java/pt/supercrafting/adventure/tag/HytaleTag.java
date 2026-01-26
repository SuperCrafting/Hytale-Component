package pt.supercrafting.adventure.tag;

import com.hypixel.hytale.protocol.FormattedMessage;
import net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;

import java.util.Collection;
import java.util.Set;

public sealed interface HytaleTag permits MonospaceTag {

    Set<String> names();

    void apply(FormattedMessage message);

    void claim(FormattedMessage message, TokenEmitter emitter);

}
