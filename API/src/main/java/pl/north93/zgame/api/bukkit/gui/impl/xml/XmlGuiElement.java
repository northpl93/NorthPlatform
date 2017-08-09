package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

import pl.north93.zgame.api.bukkit.gui.GuiElement;
import pl.north93.zgame.api.bukkit.gui.impl.RenderContext;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class XmlGuiElement
{
    @XmlAttribute(name = "pos")
    private String position = "0,0";
    
    @XmlAttribute(name = "show")
    private boolean visible = true;

    @XmlElement(name = "variable")
    private List<XmlVariable> variables = new ArrayList<>();

    @XmlElement
    private List<String> onClick = new ArrayList<>();
    
    @XmlElement
    private List<XmlMetadataEntry> metadata = new ArrayList<>();
    
    @XmlElementWrapper(name = "content")
    @XmlAnyElement(lax = true)
    private List<XmlGuiElement> content = new ArrayList<>();
    
    public GuiElement toGuiElement(final RenderContext context)
    {
        GuiElement element = toGuiElement0(context);
        
        String[] split = position.split(",");
        element.setPosition(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        element.setVisible(visible);
        
        for ( String clickHandler : onClick )
        {
            element.getClickHandlers().add(clickHandler);
        }
        
        for ( XmlMetadataEntry entry : metadata )
        {
            element.getMetadata().put(entry.getKey(), entry.getValue());
        }

        for ( XmlGuiElement child : content )
        {
            final GuiElement convertedChild = child.toGuiElement(context);
            if ( convertedChild == null )
            {
                continue;
            }
            element.addChild(convertedChild);
        }
        
        return element;
    }

    public List<XmlVariable> getVariables()
    {
        return this.variables;
    }

    protected abstract GuiElement toGuiElement0(RenderContext context);
}
