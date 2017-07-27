package pl.arieals.globalshops.controller.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ItemCfg
{
    @XmlElement(required = true)
    private String id;


    public String getId()
    {
        return this.id;
    }
}
