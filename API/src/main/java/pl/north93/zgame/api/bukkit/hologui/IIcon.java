package pl.north93.zgame.api.bukkit.hologui;

import org.bukkit.inventory.ItemStack;

import pl.north93.zgame.api.global.messages.TranslatableString;

public interface IIcon
{
    ItemStack getItem();

    void setType(ItemStack stack);

    IconPosition getPosition();

    void setPosition(IconPosition position);

    TranslatableString getDisplayName();

    void setDisplayName(TranslatableString name);
}
