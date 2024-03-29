package pl.north93.northplatform.controller.playerfixer;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.api.global.storage.StorageConnector;

/**
 * Klasa ma za zadanie usuwać z Redisa dane graczy których już nie ma na bungee
 * (czyli naprawiać zbugowanych)
 * Przydatne gdy bungee się zcrashuje, utraci kontankt z bazą itp.
 */
@Slf4j
public class PlayersDataFixer extends Component implements Runnable
{
    @Inject
    private StorageConnector storage;
    @Inject
    private IPlayersManager  playersManager;

    @Override
    protected void enableComponent()
    {
        // wykonywanie zadania co 10 minut
        this.getApiCore().getHostConnector().runTaskAsynchronously(this, 20 * 60 * 10);
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public void run()
    {
        final RedisCommands<String, byte[]> redis = this.storage.getRedis();

        final List<String> keys = redis.keys("players:*");
        keys.stream().map(s -> StringUtils.replace(s, "players:", "")).forEach(this::checkPlayer);
    }

    private void checkPlayer(final String nick)
    {
        final Value<IOnlinePlayer> player = this.playersManager.unsafe().getOnlineValue(nick);
        final IOnlinePlayer cache = player.get();
        if (cache == null)
        {
            return;
        }

        try
        {
            if (cache.isOnline()) // isOnline wysyła zapytanie bezpośrednio do bungee
            {
                return;
            }
        }
        catch (final Exception exception) // RpcException
        {
            log.warn("[PlayersDataFixer] Exception occurred while checking player {}", nick, exception);
            return;
        }

        this.playersManager.getInternalData().savePlayer(cache);
        player.delete();

        log.info("[PlayersDataFixer] Flushed data of player {} because he isn't online in bungee", nick);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
