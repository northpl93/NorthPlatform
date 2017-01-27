package pl.north93.zgame.webauth.controller;

import static spark.Spark.get;
import static spark.Spark.halt;


import java.text.MessageFormat;
import java.util.UUID;

import com.google.gson.Gson;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.restful.models.PlayerModel;
import pl.north93.zgame.webauth.IWebAuthManager;
import spark.Request;
import spark.Response;

public class WebAuthManagerImpl implements IWebAuthManager
{
    private static final Gson gson = new Gson();
    private final WebAuthConfig config;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observer;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    private Cache<String, UUID> keys;

    public WebAuthManagerImpl(final WebAuthConfig config)
    {
        this.config = config;
        get("webauth/:key", this::requestHandler, gson::toJson);
    }

    @PostInject
    private void init()
    {
        this.keys = this.observer.cacheBuilder(String.class, UUID.class)
                                 .name("webauth:")
                                 .keyMapper(ObjectKey::new)
                                 .expire(this.config.getExpireTime())
                                 .build();
    }

    private Object requestHandler(final Request request, final Response response)
    {
        final Value<UUID> keyValue = this.keys.getValue(request.params(":key"));
        final UUID playerId = keyValue.get();
        if (playerId == null)
        {
            halt(404);
        }
        keyValue.delete();

        try (final IPlayerTransaction transaction = this.networkManager.getPlayers().transaction(playerId))
        {
            final IPlayer player = transaction.getPlayer();
            final boolean isOnline = player.isOnline();
            final String nick = isOnline ? ((IOnlinePlayer) player).getNick() : player.getLatestNick();
            return new PlayerModel(playerId, nick, isOnline, player.getGroup().getName(), player.getMetaStore());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            halt(500);
        }

        return null;
    }

    @Override
    public String getLoginUrl(final UUID playerId)
    {
        final String key = RandomStringUtils.randomAlphanumeric(this.config.getKeyLength());
        this.keys.put(key, playerId);
        return MessageFormat.format(this.config.getLoginUrl(), key);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("config", this.config).toString();
    }
}
