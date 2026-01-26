package pt.supercrafting.adventure;

import com.hypixel.hytale.protocol.FormattedMessage;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public interface HytaleTag {

    void claim(FormattedMessage message, TokenEmitter emitter);

}
