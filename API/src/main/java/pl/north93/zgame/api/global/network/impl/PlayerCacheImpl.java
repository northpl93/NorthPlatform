package pl.north93.zgame.api.global.network.impl;

import java.net.URL;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import org.diorite.commons.io.DioriteURLUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.UsernameDetails;
import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.ICacheBuilder;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.storage.StorageConnector;

@Slf4j
class PlayerCacheImpl implements IPlayersManager.IPlayerCache
{
    private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    private final JsonParser json = new JsonParser();
    @Inject
    private StorageConnector               storage;
    @Inject
    private IObservationManager            observationManager;
    private Cache<String, UsernameDetails> localCache;

    public PlayerCacheImpl()
    {
        final ICacheBuilder<String, UsernameDetails> builder = this.observationManager.cacheBuilder(String.class, UsernameDetails.class);
        this.localCache = builder.name("mojangapicache:")
                                 .keyMapper(ObjectKey::new)
                                 .provider(this::fillCache)
                                 .expire((int) TimeUnit.DAYS.toSeconds(1))
                                 .build();
    }

    @Override
    public Optional<UsernameDetails> getNickDetails(final String username)
    {
        return this.localCache.get(username.toLowerCase(Locale.ROOT));
    }

    private UsernameDetails fillCache(final String username)
    {
        final Datastore datastore = this.storage.getDatastore();
        final Query<UsernameDetails> query = datastore.createQuery(UsernameDetails.class)
                                                      .field("validSpelling").equalIgnoreCase(username);

        final UsernameDetails details = query.get();
        if (details != null)
        {
            return details;
        }

        final Optional<UsernameDetails> usernameDetails = this.queryMojangForProfileByUsername(username);
        if (usernameDetails.isPresent())
        {
            final UsernameDetails fromMojang = usernameDetails.get();
            datastore.updateFirst(query, fromMojang, true);

            log.info("Updating nick cache for {}", username);
            return fromMojang;
        }

        return null;
    }

    private Optional<UsernameDetails> queryMojangForProfileByUsername(final String username)
    {
        try
        {
            final String url = "https://api.mojang.com/users/profiles/minecraft/" + DioriteURLUtils.encodeUTF8(username);
            final String response = IOUtils.toString(new URL(url));
            if (StringUtils.isEmpty(response))
            {
                return Optional.of(new UsernameDetails(username, Instant.now()));
            }
            final JsonObject jsonObject = this.json.parse(response).getAsJsonObject();

            final String uuidString = UUID_PATTERN.matcher(jsonObject.getAsJsonPrimitive("id").getAsString())
                                                  .replaceAll("$1-$2-$3-$4-$5");
            final UUID uuid = UUID.fromString(uuidString);

            final String validUsername = jsonObject.getAsJsonPrimitive("name").getAsString();
            final boolean premium = ! (jsonObject.has("demo") && jsonObject.getAsJsonPrimitive("demo").getAsBoolean());

            return Optional.of(new UsernameDetails(validUsername, uuid, premium, Instant.now()));
        }
        catch (final Exception e)
        {
            log.error("Failed to query Mojang API to check username {}", username, e);
            return Optional.empty();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
