package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.zgame.api.bukkit.gui.DelegatedGuiIcon;
import pl.north93.zgame.api.bukkit.gui.element.ButtonElement;
import pl.north93.zgame.api.bukkit.gui.impl.XmlReaderContext;

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
    protected ButtonElement toGuiElement0(XmlReaderContext context)
    {
        if (! this.shouldShow(context))
        {
            return null;
        }
        else if (this.icon != null)
        {
            return new ButtonElement(icon.toGuiIcon(context, this.getVariables()));
        }
        else if (this.renderer != null)
        {
            return new ButtonElement(new DelegatedGuiIcon(this.renderer));
        }
        throw new IllegalStateException("Both icon and renderer are null");
    }
}
