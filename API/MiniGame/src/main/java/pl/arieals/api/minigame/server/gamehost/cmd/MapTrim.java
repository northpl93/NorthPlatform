package pl.arieals.api.minigame.server.gamehost.cmd;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class MapTrim extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
    @Inject
    private MiniGameServer server;

    public MapTrim()
    {
        super("maptrim");
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
            player.sendMessage(ChatColor.RED + "Musisz byc na arenie!");
            return;
        }

        if (args.length() != 1)
        {
            player.sendMessage(ChatColor.RED + "/maptrim <nazwaSwiataDocelowego>");
            return;
        }

        final GameHostManager serverManager = this.server.getServerManager();

        player.sendMessage(ChatColor.GREEN + "Swiat z aktualnej areny zostanie przyciety i zapisany w nowym swiecie: " + args.asString(0));
        serverManager.getWorldManager().trimWorld(player.getWorld(), args.asString(0), arena.getWorld().getCurrentMapConfig().getChunks());
        player.sendMessage(ChatColor.GREEN + "Zrobione!");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
