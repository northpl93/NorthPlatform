package pl.north93.zgame.api.bukkit.hologui;

import org.bukkit.inventory.ItemStack;

import pl.north93.zgame.api.global.messages.TranslatableString;

public interface IIcon
{
    /**
     * Zwraca kontekst do ktorego nalezy ta ikona.
     *
     * @see IHoloContext
     * @return Kontekst do ktorego nalezy ikona.
     */
    IHoloContext getHoloContext();

    ItemStack getItem();

    void setType(ItemStack stack);

    IconPosition getPosition();

    void setPosition(IconPosition position);

    void setNameLocation(IconNameLocation location);

    TranslatableString[] getDisplayName();

    void setDisplayName(TranslatableString... name);
}
