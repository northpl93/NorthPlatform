package pl.north93.northplatform.api.bukkit.utils.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

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

        final BaseComponent centered = ChatCentering.centerMessage(component);
        final String plain = centered.toPlainText();

        assertEquals("", plain);
    }

    @Test
    public void oneLetterCenter()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("a");

        final BaseComponent centered = ChatCentering.centerMessage(component);
        final String plain = centered.toPlainText();

        assertEquals("                                   a", plain);
    }

    @Test
    public void manyLettersCenter()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("test");

        final BaseComponent centered = ChatCentering.centerMessage(component);
        final String plain = centered.toPlainText();

        assertEquals("                                 test", plain);
    }

    @Test
    public void formattedText()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("&a&lABC &rdef &cghi &ljkl");

        final BaseComponent centered = ChatCentering.centerMessage(component);
        final String plain = centered.toPlainText();

        assertEquals("                         ABC def ghi jkl", plain);
    }

    @Test
    public void oneNewLine()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("line1\nline2");

        final BaseComponent centered = ChatCentering.centerMessage(component);
        final String plain = centered.toPlainText();

        assertEquals("                                 line1\n                                 line2", plain);
    }

    @Test
    public void manyNewLinesWithoutText()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("\n\n\n");

        final BaseComponent centered = ChatCentering.centerMessage(component);
        final String plain = centered.toPlainText();

        assertEquals("\n\n\n", plain);
    }

    @Test
    public void manyNewLinesWithFormatting()
    {
        final BaseComponent component = ChatUtils.parseLegacyText("\n&aA\n&bB\n");

        final BaseComponent centered = ChatCentering.centerMessage(component);
        final String plain = centered.toPlainText();

        assertEquals("\n                                   A\n                                   B\n", plain);
    }

    @Test
    public void bugTest1() // wyjatek przy obslugiwaniu tego konkretnego stringa
    {
        final BaseComponent component = ChatUtils.parseLegacyText("&f&e&lL &eNorthPL93");

        final BaseComponent centered = ChatCentering.centerMessage(component);
        final String plain = centered.toPlainText();

        assertEquals("                           L NorthPL93", plain);
    }

    // 1. niepoprawnie uzyty builder nie zamienia znaków formatowania, ale klient to ogarnia
    // 2. bug powodujacy zliczanie znaków koloru
    @Test
    public void bugTest2()
    {
        final String cmdClickMessage = "&aWpisz &e&l/grupa akceptuj &alub kliknij &e&lTUTAJ";
        final BaseComponent[] cmdClickComponents = ChatUtils.builderFromLegacyText(cmdClickMessage).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept")).create();

        final BaseComponent centered = ChatCentering.centerMessage(new TextComponent(cmdClickComponents));
        assertEquals("§a§f        §aWpisz §e§l/grupa akceptuj §alub kliknij §e§lTUTAJ", centered.toLegacyText());
    }
}
