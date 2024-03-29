package pl.north93.northplatform.api.global.network.players;

import java.util.UUID;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.global.messages.Messageable;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.network.server.joinaction.IServerJoinAction;

public interface IOnlinePlayer extends IPlayer, Messageable
{
    void transferDataFrom(IOfflinePlayer offlinePlayer);

    @Override
    default Identity getIdentity()
    {
        return Identity.create(this.getUuid(), this.getNick());
    }

    /**
     * Zwraca nick pod którym aktualnie jest zalogowany gracz.
     * @return nick zalogowanego gracza.
     */
    String getNick();

    String getProxyId();

    UUID getServerId();

    void setServerId(UUID serverId);

    void kick(BaseComponent message);

    void connectTo(Server server, IServerJoinAction... actions);

    void connectTo(String serversGroupName, IServerJoinAction... actions);
}
