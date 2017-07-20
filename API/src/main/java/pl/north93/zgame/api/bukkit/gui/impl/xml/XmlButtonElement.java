package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.zgame.api.bukkit.gui.GuiButtonElement;
import pl.north93.zgame.api.global.messages.MessagesBox;

@XmlRootElement(name = "button")
public class XmlButtonElement extends XmlGuiElement
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
    protected GuiButtonElement toGuiElement0(MessagesBox messagesBox)
    {
        return new GuiButtonElement(icon.toGuiIcon(messagesBox));
    }
}
