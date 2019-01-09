package pl.north93.northplatform.api.bukkit.hologui.hologram.loader;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class HologramMessageConfig
{
    @XmlAttribute
    private String locale;
    @XmlValue
    private String content;

    public Locale getLocale()
    {
        return Locale.forLanguageTag(this.locale);
    }

    public String getContent()
    {
        return this.content.trim();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("locale", this.locale).append("content", this.content).toString();
    }
}
