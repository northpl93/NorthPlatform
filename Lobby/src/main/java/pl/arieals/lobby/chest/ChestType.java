package pl.arieals.lobby.chest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Klasa reprezentuje rodzaj skrzynki mozliwej do otwierania.
 */
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("categories", this.categories).toString();
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("chestTypes", this.chestTypes).toString();
    }
}