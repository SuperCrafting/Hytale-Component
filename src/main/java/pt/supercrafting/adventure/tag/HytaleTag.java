package pt.supercrafting.adventure.tag;

import com.hypixel.hytale.protocol.FormattedMessage;
import net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;

public sealed interface HytaleTag permits MonospaceTag {

    void apply(FormattedMessage message);

    void claim(FormattedMessage message, TokenEmitter emitter);

}
