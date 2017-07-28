package pl.arieals.globalshops.controller.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "itemData")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemDataCfg
{
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String value;

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("value", this.value).toString();
    }
}
