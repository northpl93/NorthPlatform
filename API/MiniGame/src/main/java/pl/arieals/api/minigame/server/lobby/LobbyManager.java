package pl.arieals.api.minigame.server.lobby;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.IGameHostRpc;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectNewInstance;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;

public class LobbyManager implements IServerManager
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager     rpcManager;
    @InjectNewInstance
    private ArenaManager    arenaManager;

    @Override
    public void start()
    {

    }

    @Override
    public void stop()
    {

    }

    // TODO temporary code.
    public void tryConnectPlayer(final Player player, final UUID arena)
    {
        final RemoteArena arena1 = this.arenaManager.getArena(arena);
        final IGameHostRpc rpcProxy = this.rpcManager.createRpcProxy(IGameHostRpc.class, Targets.server(arena1.getServerId()));
        final ArrayList<PlayerJoinInfo> arrayList = new ArrayList<>();
        arrayList.add(new PlayerJoinInfo(player.getUniqueId(), false));
        if (rpcProxy.tryConnectPlayers(arrayList, arena, false))
        {
            player.sendMessage("Od serwera przyszlo true, przenosze na gamehosta.");
            this.networkManager.getOnlinePlayer(player.getUniqueId()).get().connectTo(this.networkManager.getServer(arena1.getServerId()).get());
        }
        else
        {
            player.sendMessage("Od serwera przyszlo false, nie mozna dolaczyc do areny.");
        }
    }
}
