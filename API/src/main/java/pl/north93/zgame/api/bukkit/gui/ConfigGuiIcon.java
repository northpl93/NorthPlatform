package pl.north93.zgame.api.bukkit.gui;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.north93.zgame.api.bukkit.gui.impl.xml.XmlVariable;
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

        List<String> lore = this.lore != null ? Arrays.asList(this.lore.getValue(player.getLocale(), parameters).toLegacyText().split("\n")) : Arrays.asList();
        String name = this.name.getValue(player.getLocale(), parameters).toLegacyText();

        final ItemMeta itemMeta = this.preCreatedItemStack.getItemMeta();
        if (itemMeta == null)
        {
            // air, nic wiecej nie ustawimy
            return this.preCreatedItemStack;
        }

        itemMeta.setDisplayName(!name.isEmpty() ? name : "§0");
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.values());

        final ItemStack newItemStack = this.preCreatedItemStack.clone();
        newItemStack.setItemMeta(itemMeta);

        return newItemStack;
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

        private List<XmlVariable> variables;

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
