package pl.north93.zgame.api.bukkit.player.impl;

import static pl.north93.zgame.api.bukkit.player.impl.LanguageKeeper.updateLocale;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.player.IBukkitPlayers;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.player.event.PlayerDataLoadedEvent;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

class PlayerDataLoadTask implements Runnable
{
    @Inject
    private BukkitApiCore       apiCore;
    @Inject
    private IBukkitExecutor     executor;
    @Inject
    private IObservationManager observation;
    @Inject
    private IBukkitPlayers      bukkitPlayers;

    private final Player              player;

    public PlayerDataLoadTask(final Player player)
    {
        this.player = player;
    }

    @Override
    public void run()
    {
        final Logger logger = this.apiCore.getLogger();
        final NorthPlayer northPlayer = (NorthPlayer) this.bukkitPlayers.getPlayer(this.player);

        // wymuszamy pobranie danych i dodatkowo weryfikujemy czy one rzeczywiscie tu sa
        final IOnlinePlayer iOnlinePlayer = northPlayer.getValue().get();
        if (iOnlinePlayer == null)
        {
            logger.log(Level.SEVERE, "Player {0} ({1}) joined, but data is null in onJoin", new Object[]{northPlayer.getName(), northPlayer.getUniqueId()});
            return;
        }

        // aktualizujemy jezyk gracza; mozemy to zrobic bezpiecznie tutaj
        updateLocale(this.player, northPlayer.getLocale());

        // zmieniamy display name; mozna to zrobic bezpiecznie tutaj
        if (iOnlinePlayer.hasDisplayName())
        {
            northPlayer.setDisplayName(iOnlinePlayer.getDisplayName());
        }

        // pobieramy liste akcji do wykonania po wejsciu na serwer
        final Collection<IServerJoinAction> joinActions = this.fetchActions(northPlayer);

        this.executor.sync(() ->
        {
            // wywolujemy synchronicznie do serwera event o zaladowaniu danych gracza.
            this.apiCore.callEvent(new PlayerDataLoadedEvent(northPlayer, joinActions));

            // wywolujemy wszystkie akcje wejscia gracza
            for (final IServerJoinAction iServerJoinAction : joinActions)
            {
                iServerJoinAction.playerJoined(northPlayer);
            }
        });
    }

    private Collection<IServerJoinAction> fetchActions(final INorthPlayer player)
    {
        final Value<JoinActionsContainer> actions = this.observation.get(JoinActionsContainer.class, "serveractions:" + player.getName());
        final JoinActionsContainer joinActionsContainer = actions.getAndDelete();
        if (joinActionsContainer == null)
        {
            return Collections.emptyList();
        }
        return Arrays.asList(joinActionsContainer.getServerJoinActions());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
