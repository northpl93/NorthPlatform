package pl.north93.zgame.api.bukkit.utils.chat;

import javax.annotation.Nonnull;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public final class ChatUtils
{
    public  static final char    COLOR_CHAR          = '§';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)(" + COLOR_CHAR + "|&)[0-9A-FK-OR]");
    private final static int     CENTER_PX           = 140;

    /**
     * Konwertuje tekst legacy na BaseComponent, bez systemu parametrów.
     * Kody kolorów są tłumaczone w tej metodzie, nie ma potrzeby robienia tego ręcznie.
     *
     * @param legacyText Tekst do skonwertowania.
     * @return Wynikowy komponent.
     */
    public static BaseComponent fromLegacyText(final String legacyText)
    {
        final BaseComponent[] components = TextComponent.fromLegacyText(translateAlternateColorCodes(legacyText));
        if (components.length == 1)
        {
            return components[0];
        }

        return new TextComponent(components);
    }

    /**
     * Tworzy nową instancję {@link ComponentBuilder} z podanym tekstem legacy.
     *
     * @param legacyText Tekst legacy do dodania na początek buildera.
     * @return Builder z poprawnie wczytanym tekstem legacy.
     */
    public static ComponentBuilder builderFromLegacyText(final String legacyText)
    {
        final BaseComponent[] components = TextComponent.fromLegacyText(translateAlternateColorCodes(legacyText));
        return new ComponentBuilder("").append(components);
    }

    /**
     * Parsuje dany legacy tekst wraz z parametrami do BaseComponent.
     * Kody kolorów są tłumaczone w tej metodzie, nie ma potrzeby robienia tego ręcznie.
     * Więcej w dokumentacji klasy {@link LegacyTextParser}.
     *
     * @param legacyText Tekst legacy do sparsowania.
     * @return Wynikowy komponent parsowania.
     */
    public static BaseComponent parseLegacyText(final String legacyText, final Object... params)
    {
        return LegacyTextParser.parseLegacyText(legacyText, params);
    }

    public static String stripColor(final String input)
    {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String translateAlternateColorCodes(final String textToTranslate)
    {
        final char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++ i)
        {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > - 1)
            {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    // wyśrodkowuje wiadomość zapisaną w Stringu
    public static String centerMessage(String message)
    {
        if (message == null || message.equals(""))
        {
            return "";
        }
        message = ChatUtils.translateAlternateColorCodes(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (final char c : message.toCharArray())
        {
            if (c == '§')
            {
                previousCode = true;
            }
            else if (previousCode)
            {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }
            else
            {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        final int halvedMessageSize = messagePxSize / 2;
        final int toCompensate = CENTER_PX - halvedMessageSize;
        final int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        final StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate)
        {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

    public static BaseComponent centerMessage(final BaseComponent component)
    {
        final BaseComponent newComponent = component.duplicate();

        final TextIterator textIterator = new TextIterator(newComponent);
        while (textIterator.hasNext())
        {
            final TextComponent startComponent = textIterator.getCurrentComponent();
            final int startIndex = textIterator.textIterator.getIndex() - 1;

            final Pair<Integer, Integer> lineData = countLineSize(textIterator);
            final int lineSize = lineData.getLeft();
            if (lineSize == 0)
            {
                continue;
            }

            final int halvedLineSize = lineSize / 2;
            final double toCompensate = CENTER_PX - halvedLineSize;
            final double spaceLength = DefaultFontInfo.SPACE.getLength() + 1;

            final int spaces = (int) Math.ceil(toCompensate / spaceLength);

            textIterator.addSpaces(startComponent, startIndex, lineData.getRight(), spaces);
        }

        return newComponent;
    }

    private static Pair<Integer, Integer> countLineSize(final TextIterator iterator)
    {
        int messagePxSize = 0;
        int chars = 0;

        while (iterator.hasNext())
        {
            final Character character = iterator.next();
            if (iterator.isNextLineChar(character))
            {
                break;
            }

            if (character != COLOR_CHAR) // jak nie jest kodem koloru to ok
            {
                final boolean bold = iterator.getCurrentComponent().isBold();

                final DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(character);
                messagePxSize += bold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
            else if (iterator.hasNext()) // jak jest kodem koloru to omijamy nastepny znak, jesli jest
            {
                iterator.next();
                chars++;
            }

            chars++;
        }

        return Pair.of(messagePxSize, chars);
    }

    private static class TextIterator implements Iterator<Character>, Iterable<Character>
    {
        private Queue<TextComponent>    components;
        private TextComponent           currentComponent;
        private StringCharacterIterator textIterator;
        private Character               next;

        public TextIterator(final BaseComponent component)
        {
            this.components = new LinkedList<>();
            this.textIterator = new StringCharacterIterator("");

            this.buildQueue(component, this.components);
        }

        private void buildQueue(final BaseComponent component, final Queue<TextComponent> components)
        {
            if (component instanceof TextComponent)
            {
                components.add((TextComponent) component);
            }

            final List<BaseComponent> extra = component.getExtra();
            if (extra == null)
            {
                return;
            }

            for (final BaseComponent child : extra)
            {
                this.buildQueue(child, components);
            }
        }

        @Override
        public boolean hasNext()
        {
            if (this.next != null)
            {
                return this.next != CharacterIterator.DONE;
            }

            this.next = this.doNext();
            return this.next != CharacterIterator.DONE;
        }

        @Override
        public Character next()
        {
            final Character next = this.next;
            this.next = null;
            return next;
        }

        private Character doNext()
        {
            final Character character = this.textIterator.current();
            if (character != CharacterIterator.DONE)
            {
                this.textIterator.next();
                return character;
            }


            while ((this.currentComponent = this.components.poll()) != null)
            {
                final String text = this.currentComponent.getText();
                if (StringUtils.isEmpty(text))
                {
                    continue;
                }

                this.textIterator.setText(text);

                final char current = this.textIterator.current();
                this.textIterator.next();

                return current; // na pewno zwróci sensowny znak
            }

            return CharacterIterator.DONE;
        }

        public TextComponent getCurrentComponent()
        {
            return this.currentComponent;
        }

        public boolean isNextLineChar(final Character character)
        {
            return character == '\n';
        }

        public void addSpaces(final TextComponent startComponent, final int startIndex, final int lineChars, final int amount)
        {
            final String text = startComponent.getText();
            final String newText = text.substring(0, startIndex) + StringUtils.repeat(' ', amount) + text.substring(startIndex, text.length());
            startComponent.setText(newText);

            final int newIndex = startIndex + amount + lineChars;
            this.textIterator.setText(newText);
            this.textIterator.setIndex(Math.min(newIndex, this.textIterator.getEndIndex()));
        }

        @Nonnull
        @Override
        public Iterator<Character> iterator()
        {
            return this;
        }
    }
}
