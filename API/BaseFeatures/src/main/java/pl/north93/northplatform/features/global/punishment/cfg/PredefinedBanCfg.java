package pl.north93.northplatform.features.global.punishment.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.messages.TranslatableString;

@XmlAccessorType(XmlAccessType.NONE)
public class PredefinedBanCfg
{
    @XmlAttribute(required = true)
    private Integer             id;
    @XmlElement(required = true)
    private String              name;
    @XmlElement
    private Integer             duration;
    @XmlElement(name = "message")
    private List<BanMessageCfg> messages;

    public Integer getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public Integer getDuration()
    {
        return this.duration;
    }

    public TranslatableString getMessage()
    {
        final Collector<BanMessageCfg, ?, Map<Locale, String>> collector = Collectors.toMap(banMessageCfg -> Locale.forLanguageTag(banMessageCfg.getLocale()), BanMessageCfg::getMessage);
        return TranslatableString.custom(this.messages.stream().collect(collector));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("name", this.name).append("duration", this.duration).append("messages", this.messages).toString();
    }
}
