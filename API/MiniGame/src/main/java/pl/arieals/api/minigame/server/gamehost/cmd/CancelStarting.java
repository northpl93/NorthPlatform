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
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class CancelStarting extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
    @Inject
    private MiniGameServer server;

    public CancelStarting()
    {
        super("cancelstarting");
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

        final Player player = (Player) sender.unwrapped();
        final LocalArena arena = getArena(player);
        if (arena == null)
        {
            sender.sendRawMessage("&cBrak areny powiazanej z graczem.");
            return;
        }

        if (arena.getGamePhase() != GamePhase.LOBBY)
        {
            sender.sendRawMessage("&cAnulowac start mozna tylko w gamephase lobby.");
            return;
        }

        if (arena.getStartScheduler().isStartScheduled())
        {
            sender.sendRawMessage("&aAnulowano start.");
            arena.getStartScheduler().cancelStarting();
        }
        else
        {
            sender.sendRawMessage("&cOdliczanie do startu nie jest aktywne.");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
