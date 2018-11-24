package pl.north93.northplatform.api.bukkit.gui.impl.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import pl.north93.northplatform.api.bukkit.utils.xml.itemstack.XmlItemStack;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.bukkit.gui.ConfigGuiIcon;
import pl.north93.northplatform.api.bukkit.gui.IGuiIcon;
import pl.north93.northplatform.api.bukkit.gui.impl.XmlReaderContext;

@XmlRootElement(name = "icon")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlGuiIcon extends XmlItemStack
{
    public XmlGuiIcon()
    {
        super(Material.AIR.name(), 0);
    }
    
    public IGuiIcon toGuiIcon(XmlReaderContext renderContext, List<XmlVariable> variables)
    {
        final MessagesBox messages = renderContext.getMessagesBox();

        final ItemStack itemStack = this.createItemStack();

        final TranslatableString name = TranslatableString.of(messages, this.getName());
        final TranslatableString lore = TranslatableString.of(messages, this.getLore());

        return ConfigGuiIcon.builder()
                            .itemStack(itemStack)
                            .name(name)
                            .lore(lore)
                            .variables(variables)
                            .build();
    }
}
