package pl.north93.northplatform.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;

import pl.north93.northplatform.api.bukkit.gui.element.ButtonElement;
import pl.north93.northplatform.api.bukkit.gui.DelegatedGuiIcon;
import pl.north93.northplatform.api.bukkit.gui.impl.XmlReaderContext;

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
        ButtonElement button = null;
        if (this.icon != null)
        {
            button = new ButtonElement(icon.toGuiIcon(context, this.getVariables()));
        }
        else if (this.renderer != null)
        {
            button = new ButtonElement(new DelegatedGuiIcon(this.renderer));
        }
        
        Preconditions.checkState(button != null, "Both icon and renderer are null");
        
        button.setIfVar(getIfVar());
        button.setNegated(isNegated());
        return button;
    }
}
