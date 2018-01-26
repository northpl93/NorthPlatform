package pl.arieals.lobby.chest;

import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableCollection;

import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


import javax.xml.bind.JAXB;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Klasa zarzadzajaca konfiguracja skrzynek i iloscia skrzynek
 * posiadanych przez graczy.
 */
public class ChestService
{
    @Inject
    private Logger          logger;
    @Inject
    private INetworkManager networkManager;
    private ChestTypeConfig typeConfig;

    @Bean
    private ChestService()
    {
        final URL chestTypesFile = ChestService.class.getResource("/ChestTypes.xml");
        this.typeConfig = JAXB.unmarshal(chestTypesFile, ChestTypeConfig.class);
    }

    /**
     * Zwraca niemodyfikowalna kolekcje zawierajaca wszystkie
     * dostepne typy skrzynek.
     *
     * @return Dostepne typy skrzynek.
     */
    public Collection<ChestType> getChestTypes()
    {
        return unmodifiableCollection(this.typeConfig.getChestTypes());
    }

    /**
     * Zwraca typ skrzynki o podanej nazwie.
     * Jak nie znajdzie zwroci null.
     *
     * @param typeName Nazwa ktorej szukamy.
     * @return Typ skrzynki o danej nazwie.
     */
    public ChestType getType(final String typeName)
    {
        final List<ChestType> chestTypes = this.typeConfig.getChestTypes();
        return findInCollection(chestTypes, ChestType::getName, typeName);
    }

    /**
     * Zwraca ilosc posiadanych przez gracza skrzynek danego typu.
     *
     * @param player Gracz ktoremu sprawdzamy ilosc posiadanych skrzynek.
     * @param type Typ skrzynek.
     * @return Ilosc posiadanego przez gracza danego typu skrzynek.
     */
    public int getChests(final Player player, final ChestType type)
    {
        final IPlayer cachedPlayer = this.networkManager.getPlayers().unsafe().getNullable(Identity.of(player));
        final ChestData chestData = new ChestData(cachedPlayer);

        return chestData.getChests(type);
    }

    public boolean hasChest(final Player player, final ChestType type)
    {
        return this.getChests(player, type) > 0; // czy ilosc skrzynek jest wieksza od 0
    }

    public boolean takeChest(final Player player, final ChestType type)
    {
        this.logger.log(Level.FINE, "Taking chest {0} from {1}", new Object[]{type.getName(), player.getName()});

        return this.updateChests(player, chestData ->
        {
            final int chests = chestData.getChests(type);
            if (chests <= 0)
            {
                throw new IllegalStateException("Player has not enough chests");
            }
            chestData.setChests(type, chests - 1);
        });
    }

    public boolean addChests(final Player player, final ChestType type, final int amount)
    {
        Preconditions.checkState(amount > 0, "Amount must be greater than 0");
        this.logger.log(Level.FINE, "Adding {0} chests of type {1} to {2}", new Object[]{amount, type.getName(), player.getName()});

        return this.updateChests(player, chestData ->
        {
            final int current = chestData.getChests(type);
            chestData.setChests(type, current + amount);
        });
    }

    public boolean setChests(final Player player, final ChestType type, final int amount)
    {
        this.logger.log(Level.FINE, "Setting chests {0} amount of {1} to {2}", new Object[]{type.getName(), player.getName(), amount});
        return this.updateChests(player, chestData -> chestData.setChests(type, amount));
    }

    private boolean updateChests(final Player player, final Consumer<ChestData> updateFunction)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(Identity.of(player)))
        {
            final ChestData chestData = new ChestData(t.getPlayer());
            updateFunction.accept(chestData);

            return true;
        }
        catch (final Exception e)
        {
            this.logger.log(Level.SEVERE, format("Failed to update chests amount for {0}", player.getName()), e);
            return false;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("typeConfig", this.typeConfig).toString();
    }
}
