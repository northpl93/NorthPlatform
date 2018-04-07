package pl.arieals.api.minigame.server.lobby.hub;

import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.shared.api.cfg.HubConfig;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.network.JoiningPolicy;

/**
 * Reprezentuje pojedynczy swiat huba znajdujacy sie na serwerze hostujacym huby.
 * Kazdy serwer ma (a w kazdym razie powinien miec) taki sam zestaw tych obiektow.
 */
public class HubWorld
{
    private final String   hubId;
    private final World    bukkitWorld;
    private final ChatRoom chatRoom;
    private JoiningPolicy  policy;

    public HubWorld(final String hubId, final World bukkitWorld, final ChatRoom chatRoom)
    {
        this.hubId = hubId;
        this.bukkitWorld = bukkitWorld;
        this.chatRoom = chatRoom;
    }

    public String getHubId()
    {
        return this.hubId;
    }

    public World getBukkitWorld()
    {
        return this.bukkitWorld;
    }

    public ChatRoom getChatRoom()
    {
        return this.chatRoom;
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
