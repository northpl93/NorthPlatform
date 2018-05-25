package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.north93.zgame.api.bukkit.gui.IGuiIcon;
import pl.north93.zgame.api.bukkit.gui.impl.XmlReaderContext;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlHotbarEntry
{
    @XmlAttribute(name = "pos")
    private int position;
    
    @XmlAttribute(name = "show")
    private boolean visible = true;
    
    @XmlElement
    private XmlGuiIcon icon;
    
    @XmlElement(name = "variable")
    private List<XmlVariable> variables = new ArrayList<>();

    @XmlElement
    private List<String> onClick = new ArrayList<>();
    
    @XmlElement
    private List<XmlMetadataEntry> metadata = new ArrayList<>();
    
    public int getPosition()
    {
        return position;
    }
    
    public void setPosition(int position)
    {
        this.position = position;
    }
    
    public boolean isVisible()
    {
        return visible;
    }
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
    
    public XmlGuiIcon getIcon()
    {
        return icon;
    }
    
    public void setIcon(XmlGuiIcon icon)
    {
        this.icon = icon;
    }
    
    public List<XmlVariable> getVariables()
    {
        return variables;
    }
    
    public void setVariables(List<XmlVariable> variables)
    {
        this.variables = variables;
    }
    
    public List<String> getOnClick()
    {
        return onClick;
    }
    
    public void setOnClick(List<String> onClick)
    {
        this.onClick = onClick;
    }
    
    public Map<String, String> getMetadataAsMap()
    {
        Map<String, String> result = new HashMap<>();
        
        for ( XmlMetadataEntry entry : metadata )
        {
            result.put(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
    
    public IGuiIcon createGuiIcon(XmlReaderContext context)
    {
        if ( icon == null )
        {
            return null;
        }
        
        return icon.toGuiIcon(context, variables);
    }
}
