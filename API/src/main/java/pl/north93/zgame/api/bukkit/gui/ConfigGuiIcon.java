package pl.north93.zgame.api.bukkit.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.north93.zgame.api.bukkit.gui.impl.xml.XmlVariable;
import pl.north93.zgame.api.global.messages.LegacyMessage;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.Vars;

/**
 * Renderuje ikone na podstawie danych z xmlowego configu.
 */
public class ConfigGuiIcon implements IGuiIcon
{
    private ItemStack preCreatedItemStack;
    
    private final TranslatableString name;
    private final TranslatableString lore;

    private final List<XmlVariable> variables;

    private ConfigGuiIcon(ItemStack preCreatedItemStack, TranslatableString name, TranslatableString lore, final List<XmlVariable> variables)
    {
        this.preCreatedItemStack = preCreatedItemStack;
        this.name = name;
        this.lore = lore;
        this.variables = variables;
    }
    
    public TranslatableString getName()
    {
        return name;
    }
    
    public TranslatableString getLore()
    {
        return lore;
    }

    public List<XmlVariable> getVariables()
    {
        return this.variables;
    }
    
    @Override
    public ItemStack toItemStack(MessagesBox messages, Player player, Vars<Object> parameters)
    {
        // przetwarzamy dodane zmienne z xmla
        for (final XmlVariable xmlVariable : this.variables)
        {
            parameters = parameters.and(xmlVariable.process(messages, parameters));
        }

        final LegacyMessage name = this.name.getLegacy(player.getLocale(), parameters);
        final LegacyMessage lore = this.lore == null ? LegacyMessage.EMPTY : this.lore.getLegacy(player.getLocale(), parameters);

        final ItemMeta itemMeta = this.preCreatedItemStack.getItemMeta();
        if (itemMeta == null)
        {
            // air, nic wiecej nie ustawimy
            return this.preCreatedItemStack;
        }

        itemMeta.setDisplayName(name.asNonEmptyString());
        itemMeta.setLore(this.prepareLore(lore));
        itemMeta.addItemFlags(ItemFlag.values());

        final ItemStack newItemStack = this.preCreatedItemStack.clone();
        newItemStack.setItemMeta(itemMeta);

        return newItemStack;
    }

    private List<String> prepareLore(final LegacyMessage lore)
    {
        final List<String> list = lore.asList();
        String lastColors = "";

        final ListIterator<String> iterator = list.listIterator();
        while (iterator.hasNext())
        {
            final String line = lastColors + iterator.next();
            iterator.set(line);

            lastColors = ChatColor.getLastColors(line);
        }

        return list;
    }
    
    public static Builder builder()
    {
        return new Builder();
    }
    
    public static class Builder
    {
        private ItemStack preCreatedItemStack;
        
        private TranslatableString name = TranslatableString.empty();
        private TranslatableString lore;

        private List<XmlVariable> variables = new ArrayList<>();

        private Builder()
        {
        }
        
        public Builder itemStack(final ItemStack itemStack)
        {
            this.preCreatedItemStack = itemStack;
            return this;
        }
        
        public Builder name(TranslatableString name)
        {
            this.name = name != null ? name : TranslatableString.empty();
            return this;
        }
        
        public Builder lore(TranslatableString lore)
        {
            this.lore = lore;
            return this;
        }

        public Builder variables(final List<XmlVariable> variables)
        {
            this.variables = variables;
            return this;
        }

        public IGuiIcon build()
        {
            return new ConfigGuiIcon(preCreatedItemStack, name, lore, variables);
        }
    }
}
