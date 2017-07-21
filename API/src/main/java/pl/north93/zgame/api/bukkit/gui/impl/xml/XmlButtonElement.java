package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.zgame.api.bukkit.gui.GuiButtonElement;
import pl.north93.zgame.api.bukkit.gui.impl.RenderContext;

@XmlRootElement(name = "button")
public class XmlButtonElement extends XmlConditionalGuiElement
{
    private XmlGuiIcon icon;
    
    public XmlGuiIcon getIcon()
    {
        return icon;
    }
    
    public void setIcon(XmlGuiIcon icon)
    {
        this.icon = icon;
    }

    @Override
    protected GuiButtonElement toGuiElement0(RenderContext context)
    {
        if (! this.shouldShow(context))
        {
            return null;
        }
        return new GuiButtonElement(icon.toGuiIcon(context, this.getVariables()));
    }
}
