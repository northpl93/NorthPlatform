package pl.north93.zgame.api.global.messages;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.global.utils.Vars;

class MessagesBoxTranslatableString extends TranslatableString
{
    private final MessagesBox messagesBox;
    private final String messageKey;
    private final String[] messageArgs;
    
    MessagesBoxTranslatableString(MessagesBox messagesBox, String messageKey, String[] messageArgs)
    {
        this.messagesBox = messagesBox;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }
    
    @Override
    public BaseComponent getValue(Locale locale, Vars<Object> params)
    {
        final Object[] args = new Object[messageArgs.length];
        IntStream.range(0, args.length).forEach(i ->
        {
            final Object value = params.getValue(messageArgs[i]);
            if (value instanceof TranslatableString)
            {
                final TranslatableString translatableString = (TranslatableString) value;
                args[i] = translatableString.getValue(locale, params);
            }
            else if (value instanceof BaseComponent)
            {
                args[i] = value;
            }
            else
            {
                final String possibleLegacyText = String.valueOf(value);
                args[i] = ChatUtils.parseLegacyText(possibleLegacyText);
            }
        });
        return messagesBox.getMessage(locale, messageKey, args);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(messagesBox, messageKey, Arrays.hashCode(messageArgs));
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null || obj.getClass() != this.getClass() )
        {
            return false;
        }
        
        MessagesBoxTranslatableString other = (MessagesBoxTranslatableString) obj;
        return Objects.equals(this.messagesBox, other.messagesBox) 
                && Objects.equals(this.messageKey, other.messageKey)
                && Arrays.equals(this.messageArgs, other.messageArgs);
    }
    
    @Override
    public String toString()
    {
        return "MessagesBoxTranslatableString [messagesBox=" + messagesBox + ", messageKey=" + messageKey + ", messageArgs="
                + Arrays.toString(messageArgs) + "]";
    }

    static MessagesBoxTranslatableString parse(String string, MessagesBox messagesBox)
    {
        Preconditions.checkArgument(string != null && string.length() > 1);
        Preconditions.checkNotNull(messagesBox);
        
        String[] split = string.split("\\$");
        String[] args = split.length > 1 ? split[1].split(",") : new String[0];
        final String messageKey = split[0].substring(1);

        return new MessagesBoxTranslatableString(messagesBox, messageKey, args);
    }
}
