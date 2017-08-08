package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.zgame.api.bukkit.gui.DelegatedGuiIcon;
import pl.north93.zgame.api.bukkit.gui.GuiButtonElement;
import pl.north93.zgame.api.bukkit.gui.impl.RenderContext;

@XmlRootElement(name = "button")
public class XmlButtonElement extends XmlConditionalGuiElement
{
    private XmlGuiIcon icon;
    private String     renderer;
    
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
        else if (this.icon != null)
        {
            return new GuiButtonElement(icon.toGuiIcon(context, this.getVariables()));
        }
        else if (this.renderer != null)
        {
            return new GuiButtonElement(new DelegatedGuiIcon(this.renderer));
        }
        throw new IllegalStateException("Both icon and renderer are null");
    }
}
