package pl.arieals.api.minigame.server.gamehost.cmd;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class GamePhaseCmd extends NorthCommand
{
    @InjectMessages("MiniGameApi")
    private MessagesBox    messages;
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    public GamePhaseCmd()
    {
        super("gamephase");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (this.server.getServerManager() instanceof LobbyManager)
        {
            sender.sendMessage(this.messages, "cmd.general.only_gamehost");
            return;
        }

        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "cmd.general.args");
            return;
        }

        final Player player = (Player) sender.unwrapped();
        final LocalArena arena = getArena(player);

        final GamePhase gamePhase = args.asEnumValue(GamePhase.class, 0);
        if (gamePhase == null)
        {
            sender.sendMessage("&cUwazaj co wpisujesz.");
            return;
        }
        arena.setGamePhase(gamePhase);
        arena.getPlayersManager().broadcast(this.messages, "arena.forced_gamephase", gamePhase);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
