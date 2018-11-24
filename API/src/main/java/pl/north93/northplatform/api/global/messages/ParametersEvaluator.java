package pl.north93.northplatform.api.global.messages;

import java.util.Locale;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.global.utils.Vars;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
/*default*/ final class ParametersEvaluator
{
    public static void evalComponentParameters(final Locale locale, final Vars<Object> parameters, final Object[] args)
    {
        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[i] instanceof TranslatableString )
            {
                final TranslatableString translatableString = (TranslatableString) args[i];
                args[i] = translatableString.generateComponent(locale, parameters);
            }
            else if (args[i] instanceof LegacyMessage)
            {
                final LegacyMessage legacyMessage = (LegacyMessage) args[i];
                args[i] = ChatUtils.fromLegacyText(legacyMessage.asString());
            }
            else if (! (args[i] instanceof BaseComponent))
            {
                final String possibleLegacyText = String.valueOf(args[i]);
                args[i] = ChatUtils.fromLegacyText(possibleLegacyText);
            }
        }
    }

    public static void evalStringParameters(final Locale locale, final Vars<Object> parameters, final Object[] args)
    {
        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[i] instanceof TranslatableString )
            {
                final TranslatableString translatableString = (TranslatableString) args[i];
                args[i] = translatableString.generateString(locale, parameters);
            }
            else if ( args[i] instanceof BaseComponent)
            {
                final BaseComponent component = (BaseComponent) args[i];
                args[i] = component.toLegacyText();
            }
            else if (args[i] instanceof LegacyMessage)
            {
                final LegacyMessage legacyMessage = (LegacyMessage) args[i];
                args[i] = legacyMessage.asString();
            }
        }
    }
}
