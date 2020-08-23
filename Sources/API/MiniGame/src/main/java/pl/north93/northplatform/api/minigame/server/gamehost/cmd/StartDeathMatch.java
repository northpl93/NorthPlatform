package pl.north93.northplatform.api.minigame.server.gamehost.cmd;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.world.DeathMatch;

public class StartDeathMatch extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox messages;

    public StartDeathMatch()
    {
        super("startdeathmatch");
        this.setPermission("minigameapi.cmd.startdeathmatch");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final LocalArena arena = getArena(player);

        final DeathMatch deathMatch = arena.getDeathMatch();
        if (deathMatch.getConfig().getEnabled())
        {
            deathMatch.activateDeathMatch();
            player.sendMessage(ChatColor.GREEN + "Done");
        }
        else
        {
            player.sendMessage(ChatColor.RED + "DeathMatch jest wylaczony w tej minigrze");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
