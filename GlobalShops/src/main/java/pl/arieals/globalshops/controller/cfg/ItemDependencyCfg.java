package pl.arieals.globalshops.controller.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dependency")
@XmlAccessorType(XmlAccessType.NONE)
public class ItemDependencyCfg
{
	@XmlAttribute(name = "item")
	private String itemName;
	@XmlAttribute(name = "level")
	private Integer itemLevel = 1;
	
	public String getItemName()
	{
		return itemName;
	}
	
	public int getItemLevel()
	{
		return itemLevel;
	}
}
