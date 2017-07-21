package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.zgame.api.bukkit.gui.GuiContainerElement;
import pl.north93.zgame.api.bukkit.gui.impl.RenderContext;

@XmlRootElement(name = "container")
public class XmlContainerElement extends XmlGuiElement
{
    @XmlAttribute
    private String size = "1,1";
    
    private XmlGuiIcon background;
    private XmlGuiIcon border;
    
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
    protected GuiContainerElement toGuiElement0(RenderContext renderContext)
    {
        String[] split = size.split(",");
        
        GuiContainerElement result = new GuiContainerElement(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        result.setBackground(background.toGuiIcon(renderContext, this.getVariables()));
        result.setBorder(border.toGuiIcon(renderContext, this.getVariables()));
        
        return result;
    }
}
