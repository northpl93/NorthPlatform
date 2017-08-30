package pl.arieals.api.minigame.server.lobby.hub;

import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.shared.api.cfg.HubConfig;
import pl.north93.zgame.api.global.network.JoiningPolicy;

public class HubWorld
{
    private final String hubId;
    private final World  bukkitWorld;
    private JoiningPolicy policy;

    public HubWorld(final String hubId, final World bukkitWorld)
    {
        this.hubId = hubId;
        this.bukkitWorld = bukkitWorld;
    }

    public String getHubId()
    {
        return this.hubId;
    }

    public World getBukkitWorld()
    {
        return this.bukkitWorld;
    }

    public JoiningPolicy getPolicy()
    {
        return this.policy;
    }

    public void updateConfig(final HubConfig config)
    {
        this.policy = config.getJoiningPolicy();
    }

    /**
     * Teleportuje podanego gracza na tego huba.
     *
     * @param player Gracz do teleportacji.
     */
    /*default*/ void teleportPlayerHere(final Player player)
    {
        // todo
        player.teleport(this.bukkitWorld.getSpawnLocation());
    }
}
