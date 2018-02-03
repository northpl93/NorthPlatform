package pl.arieals.api.minigame.server.lobby.hub;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.shared.api.cfg.HubConfig;
import pl.arieals.api.minigame.shared.api.cfg.HubsConfig;
import pl.arieals.api.minigame.shared.api.hub.IHubServer;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.NetConfig;

/**
 * Reprezentuje lokalny serwer hostujący huby.
 */
public class LocalHubServer implements IHubServer
{
    @Inject
    private BukkitApiCore         apiCore;
    @Inject @NetConfig(type = HubsConfig.class, id = "hubs")
    private IConfig<HubsConfig>   hubsConfig;
    private Map<String, HubWorld> hubWorlds = new HashMap<>();

    @Override
    public UUID getServerId()
    {
        return this.apiCore.getServerId();
    }

    /**
     * Zwraca swiat huba powiazany z danym ID huba.
     *
     * @param hubId ID huba (z pliku konfiguracyjnego)
     * @return Obiekt reprezentujacy tego huba na tej instancji serwera.
     */
    public HubWorld getHubWorld(final String hubId)
    {
        return this.hubWorlds.get(hubId);
    }

    /**
     * Zwraca obiekt swiata huba powiazany z danym swiatem Bukkita.
     * Moze zwrocic null jesli swiat nie jest powiazany z zadnym lobby.
     *
     * @param world Swiat dla ktorego pobieramy HubWorld.
     * @return HubWorld.
     */
    public HubWorld getHubWorld(final World world)
    {
        for (final HubWorld hubWorld : this.hubWorlds.values())
        {
            if (hubWorld.getBukkitWorld().equals(world))
            {
                return hubWorld;
            }
        }

        return null;
    }

    /**
     * Zwraca huba na ktorym jest obecnie podany gracz.
     * Moze zwrocic null tylko w wypadku gdy na serwerze jest wiecej swiatow
     * niz hubow.
     *
     * @param player Gracz ktoremu sprawdzamy hub.
     * @return Hub na ktorym znajduje sie gracz.
     */
    public HubWorld getHubWorld(final Player player)
    {
        return this.getHubWorld(player.getWorld());
    }

    /**
     * Zwraca niemodyfikowalną listę lokalnych hubów.
     *
     * @return Lista lokalnych hubów.
     */
    public Collection<HubWorld> getLocalWorlds()
    {
        return Collections.unmodifiableCollection(this.hubWorlds.values());
    }

    /**
     * Teleportuje podanego gracza na hub o podanym ID znajdujacego sie
     * na tym serwerze. Wywoluje eventy itd.
     *
     * @param player Gracz ktorego przenosimy.
     * @param hubId ID huba na ktorego przenosimy gracza.
     */
    public void movePlayerToHub(final Player player, final String hubId)
    {
        final HubWorld hubWorld = this.getHubWorld(hubId);
        if (hubWorld == null)
        {
            this.apiCore.getLogger().log(Level.WARNING, "Tried teleport {0} to non-existing hub {1}", new Object[]{player.getName(), hubId});
            return;
        }

        hubWorld.teleportPlayerHere(player);
        this.apiCore.callEvent(new PlayerSwitchedHubEvent(player, hubWorld));
    }

    /**
     * Teleportuje podanego gracza do huba ustawionego jako domyslny.
     *
     * @see #movePlayerToHub(Player, String)
     * @param player Gracz do przeniesienia do domyslnego huba.
     */
    public void movePlayerToDefaultHub(final Player player)
    {
        final String mainHubId = this.hubsConfig.get().getMainHub();
        this.movePlayerToHub(player, mainHubId);
    }

    /**
     * Odswieza konfiguracje hubow i ewentualnie dodaje nowe.
     * Aktualnie nie wspiera usuwania.
     */
    public void refreshConfiguration()
    {
        final HubsConfig hubsConfig = this.hubsConfig.get();
        if (hubsConfig == null)
        {
            this.apiCore.getLogger().log(Level.WARNING, "HubsConfig is null in LocalHubServer#refreshConfiguration()! Did controller is set up properly?");
            return;
        }

        for (final HubConfig hubConfig : hubsConfig.getHubs())
        {
            final HubWorld actualHub = this.getHubWorld(hubConfig.getHubId());
            if (actualHub != null)
            {
                // juz mamy stworzony hub o tym id
                actualHub.updateConfig(hubConfig);
                continue;
            }

            this.createNewHubWorld(hubConfig);
        }
    }

    private void createNewHubWorld(final HubConfig hubConfig)
    {
        final World bukkitWorld = this.createWorld(hubConfig);

        final HubWorld hubWorld = new HubWorld(hubConfig.getHubId(), bukkitWorld);
        hubWorld.updateConfig(hubConfig);

        this.hubWorlds.put(hubConfig.getHubId(), hubWorld);
        this.apiCore.getLogger().log(Level.INFO, "Created hub with ID {0}", hubConfig.getHubId());
    }

    private World createWorld(final HubConfig hubConfig)
    {
        final String mainHubName = this.hubsConfig.get().getMainHub();
        if (mainHubName.equals(hubConfig.getHubId()))
        {
            return Bukkit.getWorlds().get(0);
        }

        final WorldCreator creator = WorldCreator.name(hubConfig.getWorldName());
        return creator.createWorld();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("hubsConfig", this.hubsConfig).append("hubWorlds", this.hubWorlds).toString();
    }
}