package pl.arieals.api.minigame.server.lobby.cmd;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaClient;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DevFastJoin extends NorthCommand
{
    @Inject
    private IArenaClient arenaClient;

    public DevFastJoin()
    {
        super("devfastjoin", "fastdevjoin"); // czesto sie myli
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        sender.sendRawMessage("&aSzybkie laczenie z dowolna wolna arena");

        final List<PlayerJoinInfo> players = Collections.singletonList(new PlayerJoinInfo(player.getUniqueId(), false, false));

        if (this.arenaClient.connect(ArenaQuery.create(), players))
        {
            sender.sendRawMessage("&aZnaleziono arene, laczenie...");
        }
        else
        {
            sender.sendRawMessage("&cNie znaleziono zadnej wolnej areny");
        }
    }
}
