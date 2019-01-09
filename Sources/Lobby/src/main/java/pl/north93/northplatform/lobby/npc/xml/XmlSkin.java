package pl.north93.northplatform.lobby.npc.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlSkin
{
    @XmlElement(required = true)
    private String data;
    @XmlElement(required = true)
    private String sign;

    public String getData()
    {
        return this.data;
    }

    public String getSign()
    {
        return this.sign;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("data", this.data).append("sign", this.sign).toString();
    }
}
