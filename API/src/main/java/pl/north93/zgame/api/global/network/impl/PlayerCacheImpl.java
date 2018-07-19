package pl.north93.zgame.api.global.network.impl;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;


import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import org.diorite.commons.io.DioriteURLUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.UsernameDetails;
import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.storage.StorageConnector;

@Slf4j
class PlayerCacheImpl implements IPlayersManager.IPlayerCache
{
    private final JsonParser json = new JsonParser();
    @Inject
    private StorageConnector               storage;
    @Inject
    private IObservationManager            observationManager;
    private Cache<String, UsernameDetails> localCache;
    private MongoCollection<Document>      mongoCache;

    public PlayerCacheImpl()
    {
        this.mongoCache = this.storage.getMainDatabase().getCollection("username_cache");
        this.localCache = this.observationManager.cacheBuilder(String.class, UsernameDetails.class)
                                                 .name("mojangapicache:")
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
        final Pattern usernamePattern = compile('^' + Pattern.quote(username) + '$', CASE_INSENSITIVE);
        final Document results = this.mongoCache.find(new Document("validSpelling", usernamePattern)).first();
        if (results != null)
        {
            final boolean isPremium = results.getBoolean("isPremium");
            final Date fetchTime = results.getDate("fetchTime");
            if (isPremium)
            {
                return new UsernameDetails(results.getString("validSpelling"), results.get("uuid", UUID.class), true, fetchTime);
            }
            return new UsernameDetails(username, fetchTime);
        }

        final Optional<UsernameDetails> usernameDetails = this.queryMojangForProfileByUsername(username);
        if (usernameDetails.isPresent())
        {
            final UsernameDetails fromMojang = usernameDetails.get();
            final Document document = new Document();
            document.put("validSpelling", fromMojang.getValidSpelling());
            if (fromMojang.isPremium())
            {
                document.put("uuid", fromMojang.getUuid());
            }
            document.put("isPremium", fromMojang.isPremium());
            document.put("fetchTime", fromMojang.getFetchTime());
            this.mongoCache.updateOne(new Document("validSpelling", usernamePattern), new Document("$set", document), new UpdateOptions().upsert(true));
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
                return Optional.of(new UsernameDetails(username, new Date()));
            }
            final JsonObject jsonObject = this.json.parse(response).getAsJsonObject();

            final String validUsername = jsonObject.getAsJsonPrimitive("name").getAsString();
            final String uuidString = jsonObject.getAsJsonPrimitive("id").getAsString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            final UUID uuid = UUID.fromString(uuidString);
            final boolean premium = ! (jsonObject.has("demo") && jsonObject.getAsJsonPrimitive("demo").getAsBoolean());

            return Optional.of(new UsernameDetails(validUsername, uuid, premium, new Date()));
        }
        catch (final IOException e)
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
