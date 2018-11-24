package pl.north93.northplatform.api.global.messages;

import java.util.Locale;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.global.utils.Vars;

@ToString
@EqualsAndHashCode(callSuper = false)
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
    protected BaseComponent generateComponent(final Locale locale, final Vars<Object> params)
    {
        final Object[] args = Stream.of(this.messageArgs).map(params::getValue).toArray(Object[]::new);

        ParametersEvaluator.evalComponentParameters(locale, params, args);
        return messagesBox.getComponent(locale, messageKey, args);
    }

    @Override
    protected String generateString(final Locale locale, final Vars<Object> params)
    {
        final Object[] args = Stream.of(this.messageArgs).map(params::getValue).toArray(Object[]::new);

        ParametersEvaluator.evalStringParameters(locale, params, args);
        return messagesBox.getString(locale, messageKey, args);
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
