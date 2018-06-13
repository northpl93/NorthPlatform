package pl.north93.zgame.features.controller.broadcaster.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.messages.MessageLayout;

@XmlAccessorType(XmlAccessType.NONE)
public class BroadcasterMessageCfg
{
    @XmlAttribute
    private String        locale;
    @XmlAttribute
    private MessageLayout layout = MessageLayout.DEFAULT;
    @XmlValue
    private String        message;

    public String getLocale()
    {
        return this.locale;
    }

    public MessageLayout getLayout()
    {
        return this.layout;
    }

    public String getMessage()
    {
        return this.message;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("locale", this.locale).append("layout", this.layout).append("message", this.message).toString();
    }
}
