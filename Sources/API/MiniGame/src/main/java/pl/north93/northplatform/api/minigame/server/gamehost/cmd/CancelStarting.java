package pl.north93.northplatform.api.minigame.server.gamehost.cmd;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.MiniGameServer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class CancelStarting extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
    @Inject
    private MiniGameServer server;

    public CancelStarting()
    {
        super("cancelstarting", "abortstarting");
        this.setPermission("minigameapi.cmd.cancelstarting");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (this.server.getServerManager() instanceof LobbyManager)
        {
            sender.sendMessage(this.messages, "cmd.general.only_gamehost");
            return;
        }

        final Player player = (Player) sender.unwrapped();
        final LocalArena arena = getArena(player);
        if (arena == null)
        {
            sender.sendMessage("&cBrak areny powiazanej z graczem.");
            return;
        }

        if (arena.getGamePhase() != GamePhase.LOBBY)
        {
            sender.sendMessage("&cAnulowac start mozna tylko w gamephase lobby.");
            return;
        }

        if (arena.getStartScheduler().isStartScheduled())
        {
            sender.sendMessage("&aAnulowano start. (uzyj ponownie aby wznowic)");
            arena.getStartScheduler().cancelStarting();
        }
        else
        {
            sender.sendMessage("&aWznowiono odliczanie do startu.");
            arena.getStartScheduler().scheduleStart();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
