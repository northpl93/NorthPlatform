package pl.arieals.lobby.chest;

import static java.util.Collections.unmodifiableCollection;

import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


import javax.xml.bind.JAXB;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Klasa zarzadzajaca iloscia skrzynek posiadanych przez gracza i
 * symulowaniem otwierania skrzynek.
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

    public int getChests(final Player player, final ChestType type)
    {
        final IPlayer cachedPlayer = this.networkManager.getPlayers().unsafe().get(Identity.of(player));
        final ChestData chestData = new ChestData(cachedPlayer);

        return chestData.getChests(type);
    }

    public boolean hasChest(final Player player, final ChestType type)
    {
        return this.getChests(player, type) > 0; // czy ilosc skrzynek jest wieksza od 0
    }

    public boolean takeChest(final Player player, final ChestType type)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(Identity.of(player)))
        {
            final ChestData chestData = new ChestData(t.getPlayer());

            final int chests = chestData.getChests(type);
            chestData.setChests(type, chests - 1);

            return true;
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
