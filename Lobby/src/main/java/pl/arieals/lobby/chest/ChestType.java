package pl.arieals.lobby.chest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ChestType
{
    @XmlAttribute
    private String       name;
    @XmlElement(name = "category")
    private List<String> categories;

    public String getName()
    {
        return this.name;
    }

    public List<String> getCategories()
    {
        return this.categories;
    }
}

@XmlRootElement(name = "chestTypes")
class ChestTypeConfig
{
    @XmlElement(name = "chestType")
    private List<ChestType> chestTypes;

    public List<ChestType> getChestTypes()
    {
        return this.chestTypes;
    }
}