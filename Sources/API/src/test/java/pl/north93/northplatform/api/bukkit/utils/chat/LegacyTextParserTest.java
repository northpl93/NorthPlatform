package pl.north93.northplatform.api.bukkit.utils.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

import net.md_5.bungee.api.chat.BaseComponent;

public class LegacyTextParserTest
{
    @Test
    public void emptyText()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("");

        assertEquals("", baseComponent.toLegacyText());
    }

    @Test
    public void oneColor()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&a");

        assertEquals("§a", baseComponent.toLegacyText());
    }

    @Test
    public void simpleTextWithoutColour()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("Test");

        assertEquals("Test", baseComponent.toLegacyText());
    }

    @Test
    public void simpleTextWithOneColor()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&aTest");

        assertEquals("§aTest", baseComponent.toLegacyText());
    }

    @Test
    public void twoParametersWithTextAndColorsInLegacyTextSection()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&aTest1 &b{0} &c{1}", "param1", "param2");

        assertEquals("§aTest1 §bparam1 §cparam2", baseComponent.toLegacyText());
    }

    @Test
    public void colourAsParameter()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&aTest1 {0}Test2", "&b");

        assertEquals("§aTest1 §bTest2", baseComponent.toLegacyText());
        //System.out.println(baseComponent);
        //System.out.println(baseComponent.toLegacyText());
    }

    @Test
    public void colourAndTextAsParameter()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&aTest1 {0}Test3", "&bTest2 ");

        assertEquals("§aTest1 §bTest2 Test3", baseComponent.toLegacyText());
    }
}
