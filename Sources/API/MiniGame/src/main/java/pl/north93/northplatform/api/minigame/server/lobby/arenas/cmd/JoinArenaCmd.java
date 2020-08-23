package pl.north93.northplatform.api.minigame.server.lobby.arenas.cmd;

import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.lobby.arenas.IArenaClient;
import pl.north93.northplatform.api.minigame.shared.api.PlayerJoinInfo;
import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.api.minigame.shared.impl.arena.ArenaManager;

public class JoinArenaCmd extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox messages;
    @Inject
    private ArenaManager arenaManager;
    @Inject
    private IArenaClient arenaClient;

    public JoinArenaCmd()
    {
        super("joinarena");
        this.setAsync(true);
        this.setPermission("minigameapi.cmd.joinarena");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final RemoteArena arena = this.arenaManager.getArena(UUID.fromString(args.asString(0)));

        this.arenaClient.connect(arena, new PlayerJoinInfo(player.getUniqueId(), false, false));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
