package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import pl.north93.zgame.api.bukkit.gui.ConfigGuiIcon;
import pl.north93.zgame.api.bukkit.gui.IGuiIcon;
import pl.north93.zgame.api.bukkit.gui.impl.RenderContext;
import pl.north93.zgame.api.bukkit.utils.xml.XmlItemStack;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;

@XmlRootElement(name = "icon")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlGuiIcon extends XmlItemStack
{
    public IGuiIcon toGuiIcon(RenderContext renderContext, List<XmlVariable> variables)
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
