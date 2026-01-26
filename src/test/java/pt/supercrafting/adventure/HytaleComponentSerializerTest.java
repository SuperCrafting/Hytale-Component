package pt.supercrafting.adventure;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

public class HytaleComponentSerializerTest {

    @Test
    void testComponentSerialization() {
        Component test = HytaleComponentSerializer.miniMessage().deserialize("<ms><red>Salut !!</red></ms><blue> Bleu</blue>");
        String testString = HytaleComponentSerializer.miniMessage().serialize(test);
        System.out.println("Test monospace: " + testString);
    }

}
