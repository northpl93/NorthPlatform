package pl.north93.northplatform.api.minigame.server.gamehost.cmd;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.MiniGameServer;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.shared.api.PlayerJoinInfo;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.metadata.MetaStore;

/**
 * Jesli chcesz uzyc tej komendy:
 *   1. Zapewnij odpowiednia konfiguracje kontrolera (glowna grupa w network.yml)
 *   2. Uruchom tylko gamehosta
 *   3. Wejdz na bungee (powinienes trafic od razu na serwer hostujacy gre i otrzymac komunikat o wejsciu bez areny)
 *   4. Wpisz ta komende, trafisz na arene gry
 */
@Deprecated
public class SingleServerJoin extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
    @Inject
    private MiniGameServer server;

    public SingleServerJoin()
    {
        super("singleserverjoin", "ssjoin");
        this.setPermission("minigameapi.cmd.singleserverjoin");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (this.server.getServerManager() instanceof LobbyManager)
        {
            sender.sendMessage(this.messages, "cmd.general.only_gamehost");
            return;
        }

        final INorthPlayer player = INorthPlayer.wrap(sender);
        final GameHostManager serverManager = this.server.getServerManager();

        if (serverManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId()).isPresent())
        {
            sender.sendMessage("&cJuz jestes powiazany z jakas arena.");
            sender.sendMessage("&cPrzeczytaj instrukcje w klasie SingleServerJoin");
            return;
        }

        final List<PlayerJoinInfo> players = Collections.singletonList(new PlayerJoinInfo(player.getUniqueId(), false, false));

        for (final LocalArena localArena : serverManager.getArenaManager().getArenas())
        {
            final boolean addSuccess = localArena.getPlayersManager().tryAddPlayers(players, new MetaStore());
            if (! addSuccess)
            {
                continue;
            }

            localArena.getPlayersManager().playerConnected(player);
            sender.sendMessage("&aUdalo sie dodac do " + localArena.getId());
            return;
        }

        sender.sendMessage("&cNie udalo sie cie dodac do zadnej areny");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
