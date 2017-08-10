package pl.north93.zgame.api.global.messages;

import java.util.Locale;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.utils.Vars;

public abstract class TranslatableString
{
    public static final TranslatableString EMPTY = new ConstantTranslatableString("");

    TranslatableString()
    {
    }
    
    public TranslatableString concat(String string)
    {
        return concat(this, new ConstantTranslatableString(string));
    }
    
    public TranslatableString concat(TranslatableString other)
    {
        return concat(this, other);
    }
    
    public String getValue(Messageable messageable)
    {
        return getValue(messageable, Vars.empty());
    }
    
    public String getValue(Messageable messageable, Vars<Object> params)
    {
        return getValue(messageable.getLocale(), params);
    }
    
    public String getValue(Player player)
    {
        return getValue(player, Vars.empty());
    }
    
    public String getValue(Player player, Vars<Object> params)
    {
        return getValue(player.spigot().getLocale(), params);
    }
    
    public String getValue(String locale)
    {
        return getValue(locale, Vars.empty());
    }
    
    public String getValue(String locale, Vars<Object> params)
    {
        return getValue(Locale.forLanguageTag(locale), params);
    }
        
    public String getValue(Locale locale)
    {
        return getValue(locale, Vars.empty());
    }
    
    public void sendMessage(Messageable messageable)
    {
        sendMessage(messageable, MessageLayout.DEFAULT, Vars.empty());
    }
    
    public void sendMessage(Messageable messageable, Vars<Object> params)
    {
        sendMessage(messageable, MessageLayout.DEFAULT, params);
    }
    
    public void sendMessage(Messageable messageable, MessageLayout messageLayout)
    {
        sendMessage(messageable, messageLayout, Vars.empty());
    }
    
    public void sendMessage(Messageable messageable, MessageLayout messageLayout, Vars<Object> params)
    {
        String message = getValue(messageable, params);
        for ( String line : messageLayout.processMessage(message) )
        {
            messageable.sendRawMessage(line, true);
        }
    }
    
    public void sendMessage(Player player)
    {
        sendMessage(player, MessageLayout.DEFAULT, Vars.empty());
    }
    
    public void sendMessage(Player player, Vars<Object> params)
    {
        sendMessage(player, MessageLayout.DEFAULT, params);
    }
    
    public void sendMessage(Player player, MessageLayout messageLayout)
    {
        sendMessage(player, messageLayout, Vars.empty());
    }
    
    public void sendMessage(Player player, MessageLayout messageLayout, Vars<Object> params)
    {
        String message = getValue(player, params);
        player.sendMessage(messageLayout.processMessage(message));
    }
    
    public abstract String getValue(Locale locale, Vars<Object> params);
    
    public abstract boolean equals(Object object);
    
    public abstract int hashCode();
    
    public abstract String toString();
    
    public static TranslatableString constant(String string)
    {
        return new ConstantTranslatableString(string);
    }
    
    public static TranslatableString of(MessagesBox messagesBox, String string)
    {
        if ( string == null )
        {
            return null;
        }
        
        if ( string.length() < 4 || string.charAt(0) != '@' )
        {
            return new ConstantTranslatableString(string);
        }
        
        if ( string.charAt(1) == '@' )
        {
            return new ConstantTranslatableString(string.substring(1));
        }
        
        try
        {
            return MessagesBoxTranslatableString.parse(string, messagesBox);
        }
        catch ( IllegalArgumentException e )
        {
            return new ConstantTranslatableString(string);
        }
    }
    
    public static TranslatableString concat(TranslatableString string1, TranslatableString string2)
    {
        return new ComplexTranslatableString(string1, string2);
    }
}
