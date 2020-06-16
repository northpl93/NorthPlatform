package pl.north93.northplatform.api.bukkit.utils.chat;

import javax.annotation.Nonnull;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public final class ChatCentering
{
    private final static int CENTER_PX = 140;

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

            if (character != ChatUtils.COLOR_CHAR) // jak nie jest kodem koloru to ok
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
                TextComponent textComponent = (TextComponent) component;
                
                String text = textComponent.getText();
                int newLineIndex = text.indexOf('\n');
                
                if ( newLineIndex != -1 && newLineIndex + 1 < text.length() )
                {
                    String text1 = text.substring(0, newLineIndex + 1);
                    String text2 = text.substring(newLineIndex + 1);
                    
                    textComponent.setText(text1);
                    TextComponent textComponent2 = (TextComponent) textComponent.duplicate();
                    textComponent2.setText(text2);
                    
                    insertExtraAtBegin(textComponent, textComponent2);
                }

                components.add(textComponent);
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
        
        private void insertExtraAtBegin(BaseComponent component, BaseComponent extra)
        {
            if ( component.getExtra() == null )
            {
                component.addExtra(extra);
            }
            else
            {
                component.getExtra().add(0, extra);
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
            final String newText = text.substring(0, startIndex);
            startComponent.setText(newText);

            TextComponent spaces = new TextComponent(StringUtils.repeat(' ', amount));
            spaces.setBold(false);
            spaces.setItalic(false);
            spaces.setUnderlined(false);
            spaces.setStrikethrough(false);
            spaces.setColor(ChatColor.WHITE);

            TextComponent afterSpaces = new TextComponent(text.substring(startIndex));
            if ( startComponent.getExtra() == null )
            {
                startComponent.addExtra(spaces);
                startComponent.addExtra(afterSpaces);
            }
            else
            {
                startComponent.getExtra().add(0, afterSpaces);
                startComponent.getExtra().add(0, spaces);
            }

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
