package pl.north93.zgame.api.bukkit.utils.chat;

import org.junit.Assert;
import org.junit.Test;

import net.md_5.bungee.api.chat.BaseComponent;

public class LegacyTextParserTest
{
    @Test
    public void emptyText()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("");

        Assert.assertEquals("§f", baseComponent.toLegacyText());
    }

    @Test
    public void oneColor()
    {
        //final BaseComponent baseComponent = new TextComponent(TextComponent.fromLegacyText(ChatUtils.translateAlternateColorCodes("&a")));
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&a");

        Assert.assertEquals("§a", baseComponent.toLegacyText());
    }

    @Test
    public void simpleTextWithoutColour()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("Test");

        Assert.assertEquals("§fTest", baseComponent.toLegacyText());
    }

    @Test
    public void simpleTextWithOneColor()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&aTest");

        Assert.assertEquals("§aTest", baseComponent.toLegacyText());
    }

    @Test
    public void twoParametersWithTextAndColorsInLegacyTextSection()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&aTest1 &b{0} &c{1}", "param1", "param2");

        Assert.assertEquals("§f§aTest1 §bparam1§b §cparam2", baseComponent.toLegacyText());
    }

    @Test
    public void colourAsParameter()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&aTest1 {0}Test2", "&b");

        Assert.assertEquals("§f§aTest1 §b§bTest2", baseComponent.toLegacyText());
        //System.out.println(baseComponent);
        //System.out.println(baseComponent.toLegacyText());
    }

    @Test
    public void colourAndTextAsParameter()
    {
        final BaseComponent baseComponent = LegacyTextParser.parseLegacyText("&aTest1 {0}Test3", "&bTest2 ");

        Assert.assertEquals("§f§aTest1 §bTest2 §bTest3", baseComponent.toLegacyText());
    }
}
