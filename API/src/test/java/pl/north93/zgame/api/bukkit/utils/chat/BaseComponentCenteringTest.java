package pl.north93.zgame.api.bukkit.utils.chat;

import org.junit.Assert;
import org.junit.Test;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

// testy algorytmu wyśrodkowującego BaseComponent
public class BaseComponentCenteringTest
{
    @Test
    public void emptyCenter()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("", plain);
    }

    @Test
    public void oneLetterCenter()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("a");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                                   a", plain);
    }

    @Test
    public void manyLettersCenter()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("test");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                                 test", plain);
    }

    @Test
    public void formattedText()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("&a&lABC &rdef &cghi &ljkl");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                         ABC def ghi jkl", plain);
    }

    @Test
    public void oneNewLine()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("line1\nline2");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                                 line1\n                                 line2", plain);
    }

    @Test
    public void manyNewLinesWithoutText()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("\n\n\n");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("\n\n\n", plain);
    }

    @Test
    public void manyNewLinesWithFormatting()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("\n&aA\n&bB\n");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("\n                                   A\n                                   B\n", plain);
    }

    @Test
    public void bugTest1() // wyjatek przy obslugiwaniu tego konkretnego stringa
    {
        final BaseComponent component = ChatUtils.parseLegacyText("&f&e&lL &eNorthPL93");

        final BaseComponent centered = ChatUtils.centerMessage(component);
        final String plain = centered.toPlainText();

        Assert.assertEquals("                           L NorthPL93", plain);
    }

    // 1. niepoprawnie uzyty builder nie zamienia znaków formatowania, ale klient to ogarnia
    // 2. bug powodujacy zliczanie znaków koloru
    @Test
    public void bugTest2()
    {
        final String cmdClickMessage = "&aWpisz &e&l/grupa akceptuj &alub kliknij &e&lTUTAJ";
        final BaseComponent[] cmdClickComponents = ChatUtils.builderFromLegacyText(cmdClickMessage).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept")).create();

        final BaseComponent centered = ChatUtils.centerMessage(new TextComponent(cmdClickComponents));
        Assert.assertEquals(centered.toLegacyText(), "§f§f§a        Wpisz §e§l/grupa akceptuj §alub kliknij §e§lTUTAJ");
    }
}
