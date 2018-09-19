package pl.north93.zgame.api.global.messages;

import java.util.Locale;
import java.util.Map;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.global.utils.Vars;

/**
 * Klasa {@code TranslatableString} reprezentuje tekst ktory moze
 * zostac przetlumaczony. Zrodlo tlumaczenia zalezy od wybranej
 * metody fabrykujacej.
 *
 * @see #empty()
 * @see #constant(String)
 * @see #of(MessagesBox, String)
 * @see #constant(String)
 * @see #custom(Map)
 */
public abstract class TranslatableString
{
    TranslatableString()
    {
    }

    // Pobieranie wartości jako BaseComponent //

    public BaseComponent getValue(Messageable messageable)
    {
        return getValue(messageable, Vars.empty());
    }
    
    public BaseComponent getValue(Messageable messageable, Vars<Object> params)
    {
        return generateComponent(messageable.getMyLocale(), params);
    }
    
    public BaseComponent getValue(Player player)
    {
        return getValue(player, Vars.empty());
    }
    
    public BaseComponent getValue(Player player, Vars<Object> params)
    {
        return getValue(player.getLocale(), params);
    }
    
    public BaseComponent getValue(String locale)
    {
        return getValue(locale, Vars.empty());
    }
    
    public BaseComponent getValue(String locale, Vars<Object> params)
    {
        return generateComponent(Locale.forLanguageTag(locale), params);
    }
        
    public BaseComponent getValue(Locale locale)
    {
        return generateComponent(locale, Vars.empty());
    }

    public BaseComponent getValue(Locale locale, Vars<Object> params)
    {
        return generateComponent(locale, params);
    }

    // Pobieranie wartości jako wiadomość legacy //

    public LegacyMessage getLegacy(Messageable messageable)
    {
        final String legacyString = generateString(messageable.getMyLocale(), Vars.empty());
        return new LegacyMessage(legacyString);
    }

    public LegacyMessage getLegacy(String locale)
    {
        return getLegacy(Locale.forLanguageTag(locale));
    }

    public LegacyMessage getLegacy(Locale locale)
    {
        final String legacyString = generateString(locale, Vars.empty());
        return new LegacyMessage(legacyString);
    }

    public LegacyMessage getLegacy(String locale, Vars<Object> params)
    {
        return getLegacy(Locale.forLanguageTag(locale), params);
    }

    public LegacyMessage getLegacy(Locale locale, Vars<Object> params)
    {
        final String legacyString = generateString(locale, params);
        return new LegacyMessage(legacyString);
    }

    // Wysyłanie wiadomości //

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
        final BaseComponent message = getValue(messageable, params);
        messageable.sendMessage(message);
    }
    
    public void sendMessage(Player player)
    {
        this.sendMessage(player, MessageLayout.DEFAULT, Vars.empty());
    }
    
    public void sendMessage(Player player, Vars<Object> params)
    {
        this.sendMessage(player, MessageLayout.DEFAULT, params);
    }
    
    public void sendMessage(Player player, MessageLayout messageLayout)
    {
        this.sendMessage(player, messageLayout, Vars.empty());
    }
    
    public void sendMessage(Player player, MessageLayout messageLayout, Vars<Object> params)
    {
        final Locale locale = Locale.forLanguageTag(player.getLocale());
        final BaseComponent message = this.generateComponent(locale, params);

        player.sendMessage(messageLayout.processMessage(message));
    }

    // pozostałe metody //

    protected abstract BaseComponent generateComponent(Locale locale, Vars<Object> params);

    protected abstract String generateString(Locale locale, Vars<Object> params);

    public TranslatableString concat(final String string)
    {
        return concat(this, new ConstantTranslatableString(string));
    }

    public TranslatableString concat(final TranslatableString other)
    {
        return concat(this, other);
    }

    public TranslatableString withVars(final Vars<Object> vars)
    {
        return withVars(this, vars);
    }

    public abstract boolean equals(Object object);

    public abstract int hashCode();

    public abstract String toString();

    /**
     * Zwraca predefiniowana instancje {@link TranslatableString} ktora zawsze
     * zwroci pusty String (dlugosc 0).
     *
     * @return TranslatableString zawsze zwracajacy pusty string.
     */
    public static TranslatableString empty()
    {
        return ConstantTranslatableString.EMPTY;
    }

    public static TranslatableString constant(final BaseComponent component)
    {
        return new ConstantTranslatableString(component);
    }

    /**
     * Tworzy obiekt {@link TranslatableString} ktory zawsze zwraca stala wartosc.
     *
     * @param string Wartosc ktora bedzie zawsze zwracana przez tego TranslatableString.
     * @return TranslatableString o stalej wartosci.
     */
    public static TranslatableString constant(final String string)
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
    
    public static TranslatableString concat(final TranslatableString string1, final TranslatableString string2)
    {
        return new ComplexTranslatableString(string1, string2);
    }

    /**
     * Umozliwia utworzenie instancji {@link TranslatableString} na podstawie mapy
     * tlumaczen. Mapa nie jest kopiowana.
     *
     * @param translations Mapa z tlumaczeniami ktora zostanie uzyta w tym TranslatableString.
     * @return TranslatableString z podana mapa tlumaczen.
     */
    public static TranslatableString custom(final Map<Locale, String> translations)
    {
        return new CustomTranslatableString(translations);
    }

    /**
     * Zwraca obiekt {@link TranslatableString} ktory zawiera predefiniowane
     * podane zmienne.
     *
     * @param vars Zmienne do predefiniowania.
     * @return TranslatableString z predefiniowanymi wartosciami
     */
    public static TranslatableString withVars(final TranslatableString translatableString, final Vars<Object> vars)
    {
        return new PredefinedVarsTranslatableString(translatableString, vars);
    }
}
