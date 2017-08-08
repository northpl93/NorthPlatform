package pl.north93.zgame.api.bukkit.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.gui.impl.NorthUriUtils;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.Vars;

/**
 * Deleguje renderowanie do zewnetrznej metody.
 */
public class DelegatedGuiIcon implements IGuiIcon
{
    private final String northUri;

    public DelegatedGuiIcon(final String northUri)
    {
        this.northUri = northUri;
    }

    @Override
    public ItemStack toItemStack(final MessagesBox messages, final Player player, final Vars<Object> parameters)
    {
        final Vars<Object> vars = parameters.and("$playerId", player.getUniqueId());
        return (ItemStack) NorthUriUtils.getInstance().call(this.northUri, vars);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("northUri", this.northUri).toString();
    }
}
