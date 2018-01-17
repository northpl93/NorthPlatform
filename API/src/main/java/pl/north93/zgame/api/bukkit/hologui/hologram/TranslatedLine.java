package pl.north93.zgame.api.bukkit.hologui.hologram;

import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;


import java.util.Arrays;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.messages.MessagesBox;

public class TranslatedLine implements IHologramLine
{
    private final MessagesBox messagesBox;
    private final String      msgKey;
    private final Object[]    args;

    public TranslatedLine(final MessagesBox messagesBox, final String msgKey, final Object... args)
    {
        this.messagesBox = messagesBox;
        this.msgKey = msgKey;
        this.args = args;
    }

    @Override
    public String render(final IHologram hologram, final Player player)
    {
        final String locale = player.getLocale();
        return translateAlternateColorCodes(this.messagesBox.getMessage(locale, this.msgKey, this.args));
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final TranslatedLine that = (TranslatedLine) o;

        if (! this.messagesBox.equals(that.messagesBox))
        {
            return false;
        }
        if (! this.msgKey.equals(that.msgKey))
        {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(this.args, that.args);
    }

    @Override
    public int hashCode()
    {
        int result = this.messagesBox.hashCode();
        result = 31 * result + this.msgKey.hashCode();
        result = 31 * result + Arrays.hashCode(this.args);
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("msgKey", this.msgKey).append("args", this.args).toString();
    }
}
