package pl.north93.northplatform.api.global.network.impl.mojang;


import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import org.diorite.commons.io.DioriteURLUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.network.mojang.UsernameDetails;
import pl.north93.northplatform.api.global.redis.observable.Cache;
import pl.north93.northplatform.api.global.redis.observable.ICacheBuilder;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.observable.ObjectKey;
import pl.north93.northplatform.api.global.storage.StorageConnector;

@Slf4j
class UsernameCache
{
    private final MongoCollection<UsernameDetails> collection;
    private final Cache<String, UsernameDetails>   localCache;

    @Bean
    private UsernameCache(final StorageConnector connector, final IObservationManager observationManager)
    {
        this.collection = connector.getMainDatabase().getCollection("username_cache").withDocumentClass(UsernameDetails.class);
        this.localCache = this.setupUsernameCache(observationManager);
    }

    public Optional<UsernameDetails> getUsernameDetails(final String username)
    {
        return this.localCache.get(username).map(details -> this.fillCrackedUuid(details, username));
    }

    // poprawia wpis o zcrackowanym graczu dodajac poprawny nick o który pytalismy i wyliczone crackowane UUID.
    private UsernameDetails fillCrackedUuid(final UsernameDetails details, final String username)
    {
        if (details.getUuid() == null)
        {
            final UUID crackedUuid = this.getCrackedUuidFromName(username);
            return new UsernameDetails(username, crackedUuid, false, details.getFetchTime());
        }

        return details;
    }

    private UsernameDetails fillCache(final String username)
    {
        final Pattern usernamePattern = Pattern.compile(Pattern.quote(username), Pattern.CASE_INSENSITIVE);
        final Document query = new Document("username", usernamePattern);

        final UsernameDetails details = this.collection.find(query).first();
        if (details != null)
        {
            return details;
        }

        final Optional<UsernameDetails> usernameDetails = this.queryMojangForProfileByUsername(username);
        if (usernameDetails.isPresent())
        {
            final UsernameDetails fromMojang = usernameDetails.get();
            this.collection.insertOne(fromMojang);

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
                // taki nick nie istnieje u mojangu - pirat
                return Optional.of(new UsernameDetails(username, Instant.now()));
            }

            final JsonObject jsonObject = MojangCacheImpl.JSON_PARSER.parse(response).getAsJsonObject();

            final String uuidString = MojangCacheImpl.UUID_PATTERN.matcher(jsonObject.getAsJsonPrimitive("id").getAsString())
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

    private Cache<String, UsernameDetails> setupUsernameCache(final IObservationManager observationManager)
    {
        final ICacheBuilder<String, UsernameDetails> builder = observationManager.cacheBuilder(String.class, UsernameDetails.class);
        builder.name("mojangapicache:");
        builder.keyMapper(username -> new ObjectKey(username.toLowerCase(Locale.ROOT)));
        builder.provider(this::fillCache);
        builder.expire((int) TimeUnit.DAYS.toSeconds(1));
        return builder.build();
    }

    private UUID getCrackedUuidFromName(final String username)
    {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }
}
