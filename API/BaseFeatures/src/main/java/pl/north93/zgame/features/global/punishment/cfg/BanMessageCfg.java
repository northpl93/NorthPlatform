package pl.north93.zgame.features.global.punishment.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.NONE)
public class BanMessageCfg
{
    @XmlAttribute
    private String locale;
    @XmlAttribute
    private String message;

    public String getLocale()
    {
        return this.locale;
    }

    public String getMessage()
    {
        return this.message;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("locale", this.locale).append("message", this.message).toString();
    }
}
