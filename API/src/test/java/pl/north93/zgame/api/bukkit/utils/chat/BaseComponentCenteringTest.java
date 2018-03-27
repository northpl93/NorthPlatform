package pl.north93.zgame.api.bukkit.utils.chat;

import org.junit.Assert;
import org.junit.Test;

import net.md_5.bungee.api.chat.BaseComponent;

// testy algorytmu wyśrodkowującego BaseComponent
public class BaseComponentCenteringTest
{
    @Test
    public void emptyCenter()
    {
        final BaseComponent component = ChatUtils.fromLegacyText("");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("", plain);
    }

    @Test
    public void oneLetterCenter()
    {
        final BaseComponent component = ChatUtils.fromLegacyText("a");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                                   a", plain);
    }

    @Test
    public void manyLettersCenter()
    {
        final BaseComponent component = ChatUtils.fromLegacyText("test");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                                 test", plain);
    }

    @Test
    public void formattedText()
    {
        final BaseComponent component = ChatUtils.fromLegacyText("&a&lABC &rdef &cghi &ljkl");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                         ABC def ghi jkl", plain);
    }

    @Test
    public void oneNewLine()
    {
        final BaseComponent component = ChatUtils.fromLegacyText("line1\nline2");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                                 line1\n                                 line2", plain);
    }

    @Test
    public void manyNewLinesWithoutText()
    {
        final BaseComponent component = ChatUtils.fromLegacyText("\n\n\n");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("\n\n\n", plain);
    }

    @Test
    public void manyNewLinesWithFormatting()
    {
        final BaseComponent component = ChatUtils.fromLegacyText("\n&aA\n&bB\n");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("\n                                   A\n                                   B\n", plain);
    }

    @Test
    public void bugTest2()
    {
        final BaseComponent component = ChatUtils.fromLegacyText("&aWpisz &f&lGrupa&e&l/grupa akceptuj &alub kliknij &e&lTUTAJ");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        System.out.println(centered.toLegacyText());
        System.out.println(ChatUtils.centerMessage("&aWpisz &f&lGrupa&e&l/grupa akceptuj &alub kliknij &e&lTUTAJ"));
    }

    @Test
    public void bugTest1() // wyjatek przy obslugiwaniu tego konkretnego stringa
    {
        final BaseComponent component = ChatUtils.fromLegacyText("&f&e&lL &eNorthPL93");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                           L NorthPL93", plain);
    }
}
