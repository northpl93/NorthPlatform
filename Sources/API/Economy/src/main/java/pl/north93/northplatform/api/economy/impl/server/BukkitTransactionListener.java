package pl.north93.northplatform.api.economy.impl.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.economy.ITransactionListener;
import pl.north93.northplatform.api.economy.impl.server.event.PlayerCurrencyChangedEvent;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IPlayer;

public class BukkitTransactionListener implements ITransactionListener
{
    @Inject
    private BukkitApiCore apiCore;

    @Override
    public void amountUpdated(final IPlayer player, final ICurrency currency, final double newAmount)
    {
        final INorthPlayer northPlayer = INorthPlayer.getExact(player.getLatestNick());
        if (northPlayer == null)
        {
            return;
        }

        this.apiCore.callEvent(new PlayerCurrencyChangedEvent(northPlayer, currency, newAmount));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
