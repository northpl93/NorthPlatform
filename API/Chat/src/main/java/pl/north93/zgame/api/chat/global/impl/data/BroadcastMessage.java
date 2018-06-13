package pl.north93.zgame.api.chat.global.impl.data;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BroadcastMessage extends AbstractChatData
{
    private String locale;

    public BroadcastMessage()
    {
    }

    public BroadcastMessage(final String roomId, final String message)
    {
        super(roomId, message);
    }

    public BroadcastMessage(final String roomId, final String message, final String locale)
    {
        super(roomId, message);
        this.locale = locale;
    }

    @Nullable
    public String getLocale()
    {
        return this.locale;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("locale", this.locale).toString();
    }
}
