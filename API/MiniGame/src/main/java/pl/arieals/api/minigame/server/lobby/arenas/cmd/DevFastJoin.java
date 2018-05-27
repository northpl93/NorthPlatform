package pl.arieals.api.minigame.server.lobby.arenas.cmd;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaClient;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class DevFastJoin extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
    @Inject
    private MiniGameServer server;
    @Inject
    private IArenaClient   arenaClient;

    public DevFastJoin()
    {
        super("devfastjoin", "fastdevjoin"); // czesto sie myli
        this.setAsync(true);
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (this.server.getServerManager() instanceof GameHostManager)
        {
            sender.sendMessage(this.messages, "cmd.general.only_lobby");
            return;
        }

        final Player player = (Player) sender.unwrapped();

        sender.sendMessage("&aSzybkie laczenie z dowolna wolna arena");

        final List<PlayerJoinInfo> players = Collections.singletonList(new PlayerJoinInfo(player.getUniqueId(), false, false));

        if (this.arenaClient.connect(ArenaQuery.create().gamePhase(GamePhase.LOBBY), players))
        {
            sender.sendMessage("&aZnaleziono arene, laczenie...");
        }
        else
        {
            sender.sendMessage("&cNie znaleziono zadnej wolnej areny");
        }
    }
}
