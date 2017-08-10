package pl.arieals.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "bedwarsShop")
@XmlAccessorType(XmlAccessType.FIELD)
public class BwShopConfig
{
    @XmlElementWrapper(name = "shopEntries")
    @XmlElement(name = "shopEntry")
    private List<BwShopEntry>    shopEntries;
    @XmlElement
    private Map<String, Integer> upgrades;

    public List<BwShopEntry> getShopEntries()
    {
        return this.shopEntries;
    }

    public Map<String, Integer> getUpgrades()
    {
        return this.upgrades;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("shopEntries", this.shopEntries).append("upgrades", this.upgrades).toString();
    }
}
