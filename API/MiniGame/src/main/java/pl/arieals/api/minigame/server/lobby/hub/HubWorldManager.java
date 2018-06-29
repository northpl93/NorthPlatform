package pl.arieals.api.minigame.server.lobby.hub;

import static java.util.Optional.ofNullable;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.cfg.HubConfig;
import pl.mcpiraci.world.properties.IWorldProperties;
import pl.mcpiraci.world.properties.IWorldPropertiesManager;
import pl.north93.zgame.api.bukkit.world.IWorldLoadCallback;
import pl.north93.zgame.api.bukkit.world.IWorldManager;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class HubWorldManager
{
    @Inject
    private IWorldManager           worldManager;
    @Inject
    private IWorldPropertiesManager worldPropertiesManager;

    @Bean
    private HubWorldManager()
    {
    }

    public HubWorld createHubWorld(final HubConfig hubConfig, final ChatRoom chatRoom)
    {
        final World world = this.createBukkitWorld(hubConfig);

        final HubWorld hubWorld = new HubWorld(hubConfig.getHubId(), world, chatRoom);
        hubWorld.updateConfig(hubConfig);

        final IWorldProperties properties = this.worldPropertiesManager.getProperties(world);
        final Location spawn = ofNullable(properties.getSpawn()).orElse(world.getSpawnLocation());
        hubWorld.setSpawn(new Location(world, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()));

        return hubWorld;
    }

    private World createBukkitWorld(final HubConfig hubConfig)
    {
        if (hubConfig.getWorldName().equals("main"))
        {
            return Bukkit.getWorlds().get(0);
        }

        final IWorldLoadCallback callback = this.worldManager.loadWorld(hubConfig.getWorldName(), true, true);
        final World world = callback.getWorld();

        world.setAutoSave(false);
        world.setKeepSpawnInMemory(true); // na hubach trzymamy spawn w pamieci, aby zapobiec mieleniu dyskiem

        return world;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
