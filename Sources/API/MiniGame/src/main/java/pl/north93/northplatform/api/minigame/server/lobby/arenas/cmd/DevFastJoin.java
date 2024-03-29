package pl.north93.northplatform.api.minigame.server.lobby.arenas.cmd;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.north93.northplatform.api.minigame.server.lobby.arenas.IArenaClient;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.PlayerJoinInfo;

public class DevFastJoin extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox messages;
    @Inject
    private IArenaClient arenaClient;

    public DevFastJoin()
    {
        super("devfastjoin", "fastdevjoin"); // czesto sie myli
        this.setAsync(true);
        this.setPermission("minigameapi.cmd.devfastjoin");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
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
