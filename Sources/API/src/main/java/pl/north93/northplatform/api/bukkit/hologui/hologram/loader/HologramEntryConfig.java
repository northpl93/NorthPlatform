package pl.north93.northplatform.api.bukkit.hologui.hologram.loader;


import javax.xml.bind.annotation.XmlElement;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.bukkit.utils.xml.XmlLocation;
import pl.north93.northplatform.api.bukkit.hologui.hologram.HologramRenderContext;
import pl.north93.northplatform.api.bukkit.hologui.hologram.IHologramMessage;

public class HologramEntryConfig implements IHologramMessage
{
    @XmlElement
    private XmlLocation                 location;
    @XmlElement(name = "message")
    private List<HologramMessageConfig> messages;

    public XmlLocation getLocation()
    {
        return this.location;
    }

    public List<HologramMessageConfig> getMessages()
    {
        return this.messages;
    }

    public HologramMessageConfig getMessage(final Locale locale)
    {
        for (final HologramMessageConfig message : this.messages)
        {
            if (message.getLocale().equals(locale))
            {
                return message;
            }
        }

        return this.messages.get(0);
    }

    @Override
    public List<String> render(final HologramRenderContext renderContext)
    {
        final HologramMessageConfig message = this.getMessage(renderContext.getLocale());
        final String content = ChatUtils.translateAlternateColorCodes(message.getContent());

        final String[] lines = content.trim().split("\n");

        return Arrays.asList(lines);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("location", this.location).append("messages", this.messages).toString();
    }
}
