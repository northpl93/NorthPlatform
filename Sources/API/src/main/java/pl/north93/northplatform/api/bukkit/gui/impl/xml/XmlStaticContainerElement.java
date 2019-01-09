package pl.north93.northplatform.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.northplatform.api.bukkit.gui.element.ContainerElement;
import pl.north93.northplatform.api.bukkit.gui.element.StaticContainerElement;
import pl.north93.northplatform.api.bukkit.gui.impl.XmlReaderContext;

@XmlRootElement(name = "container")
public class XmlStaticContainerElement extends XmlGuiElement
{
    @XmlAttribute
    private String size = "1,1";
    
    private XmlGuiIcon background;
    private XmlGuiIcon border;

    public String getSize()
    {
        return this.size;
    }

    public XmlGuiIcon getBackground()
    {
        return background;
    }
    
    public void setBackground(XmlGuiIcon background)
    {
        this.background = background;
    }
    
    public XmlGuiIcon getBorder()
    {
        return border;
    }
    
    public void setBorder(XmlGuiIcon border)
    {
        this.border = border;
    }
    
    @Override
    protected ContainerElement toGuiElement0(XmlReaderContext renderContext)
    {
        String[] split = size.split(",");
        
        StaticContainerElement result = new StaticContainerElement(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        result.setBackground(background != null ? background.toGuiIcon(renderContext, this.getVariables()) : null);
        result.setBorder(border != null ? border.toGuiIcon(renderContext, this.getVariables()) : null);
        
        return result;
    }
}
