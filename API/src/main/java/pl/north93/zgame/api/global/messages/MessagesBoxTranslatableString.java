package pl.north93.zgame.api.global.messages;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;

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
    public String getValue(Locale locale, Vars<String> params)
    {
        String[] args = new String[messageArgs.length];
        IntStream.range(0, args.length).forEach( (i) -> args[i] = params.getValue(messageArgs[i]) );
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

    static MessagesBoxTranslatableString parse(String string, Class<?> callerClass)
    {
        Preconditions.checkArgument(string != null && string.length() > 1);
        
        String[] split = string.split("\\$");
        String[] keys = split[0].split("\\.");
        String[] args = split.length > 1 ? split[1].split(",") : new String[0];
        
        MessagesBox messagesBox = new MessagesBox(callerClass.getClassLoader(), keys[0].substring(1));
        String messageKey = IntStream.range(1, keys.length).mapToObj(i -> keys[i]).collect(Collectors.joining("."));
        
        return new MessagesBoxTranslatableString(messagesBox, messageKey, args);
    }
}
