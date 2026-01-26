package net.kyori.adventure.text.minimessage;

import net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

@ApiStatus.Internal
public final class TokenHelper {

    private static final VarHandle CONSUMER;
    private static final char[] EMPTY_CHAR_ARRAY = new char[0];

    static {

        VarHandle consumer;
        try {
            MethodHandles.Lookup lookup =
                    MethodHandles.privateLookupIn(
                            MiniMessageSerializer.Collector.class,
                            MethodHandles.lookup()
                    );
            consumer = lookup.findVarHandle(
                    MiniMessageSerializer.Collector.class,
                    "consumer", StringBuilder.class);
        } catch (Exception e){
            consumer = null;
        }
        CONSUMER = consumer;

    }

    public static void emitUnquoted(TokenEmitter emitter, String text) {
        if(CONSUMER == null) {
            emitter.text(text);
        } else {
            MiniMessageSerializer.Collector collector = (MiniMessageSerializer.Collector) emitter;
            collector.completeTag();
            try {
                StringBuilder sb = (StringBuilder) CONSUMER.get(collector);
                //sb.append(text);
                MiniMessageSerializer.Collector.appendEscaping(sb, text, EMPTY_CHAR_ARRAY, true);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

}
