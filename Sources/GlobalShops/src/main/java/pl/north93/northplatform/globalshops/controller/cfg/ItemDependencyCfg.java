package pl.north93.northplatform.globalshops.controller.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "dependency")
@XmlAccessorType(XmlAccessType.NONE)
public class ItemDependencyCfg
{
	@XmlAttribute(name = "item")
	private String  itemName;
	@XmlAttribute(name = "level")
	private Integer itemLevel = 1;
	
	public String getItemName()
	{
		return this.itemName;
	}
	
	public int getItemLevel()
	{
		return this.itemLevel;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("itemName", this.itemName).append("itemLevel", this.itemLevel).toString();
	}
}
