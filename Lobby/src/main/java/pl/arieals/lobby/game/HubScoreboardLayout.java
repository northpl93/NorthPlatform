package pl.arieals.lobby.game;

import java.util.Optional;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.unit.NumberUnit;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Klasa pomocnicza do tworzenia scoreboardow na hubach.
 */
public abstract class HubScoreboardLayout implements IScoreboardLayout
{
    @Inject
    protected IEconomyManager economyManager;

    /**
     * Formatuje wynik odpytywania baze o statystyke.
     *
     * @param optional Wynik z metody pobierajacej statystyke.
     * @return Sformatowana liczba.
     */
    protected final String parseNumber(final Optional<IRecord<NumberUnit>> optional)
    {
        return optional.map(numberUnitIRecord -> numberUnitIRecord.getValue().getValue().toString()).orElse("0");
    }

    protected final String getPlayerCurrency(final Player player)
    {
        final Identity identity = Identity.of(player);
        final ICurrency currency = this.economyManager.getCurrency("minigame");

        return String.valueOf(this.economyManager.getAmount(currency, identity));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
