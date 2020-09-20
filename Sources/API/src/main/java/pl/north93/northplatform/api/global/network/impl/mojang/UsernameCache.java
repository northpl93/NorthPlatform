package pl.north93.northplatform.api.global.network.impl.mojang;


import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import org.diorite.commons.io.DioriteURLUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.network.mojang.MojangApiException;
import pl.north93.northplatform.api.global.network.mojang.UsernameDetails;
import pl.north93.northplatform.api.global.storage.StorageConnector;

@Slf4j
class UsernameCache
{
    private final MongoCollection<UsernameDetails> collection;

    @Bean
    private UsernameCache(final StorageConnector connector)
    {
        this.collection = connector.getMainDatabase().getCollection("username_cache").withDocumentClass(UsernameDetails.class);
    }

    public UsernameDetails lookupUsernameAndUpdateDb(final String username) throws MojangApiException
    {
        final Optional<UsernameDetails> detailsFromDb = this.lookupUsernameInLocalDatabase(username);
        if (detailsFromDb.isPresent())
        {
            return detailsFromDb.get();
        }

        final UsernameDetails computedDetails = this.queryMojangForProfileByUsername(username).orElseGet(() ->
        {
            // if mojang doesn't know anything about this user, then create
            // cracked UsernameDetails
            final UUID crackedUuid = this.getCrackedUuidFromName(username);
            return new UsernameDetails(username, crackedUuid, false, Instant.now());
        });

        // save our new UsernameDetails into db so we don't have to ask mojang next time
        this.collection.insertOne(computedDetails);
        log.info("Saved new UsernameDetails in local db for user {}, premium {}", computedDetails.getUsername(), computedDetails.isPremium());

        return computedDetails;
    }

    public Optional<UsernameDetails> lookupUsernameInLocalDatabase(final String username)
    {
        final Pattern idPattern = Pattern.compile(Pattern.quote(username), Pattern.CASE_INSENSITIVE);
        final Document query = new Document("_id", idPattern);

        final UsernameDetails details = this.collection.find(query).first();
        return Optional.ofNullable(details);
    }

    private Optional<UsernameDetails> queryMojangForProfileByUsername(final String username) throws MojangApiException
    {
        try
        {
            final String url = "https://api.mojang.com/users/profiles/minecraft/" + DioriteURLUtils.encodeUTF8(username);
            final String response = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);

            if (StringUtils.isEmpty(response))
            {
                // such username doesn't exist - cracked
                return Optional.empty();
            }

            final JsonObject jsonObject = MojangCacheImpl.JSON_PARSER.parse(response).getAsJsonObject();

            final String mojangUuidString = jsonObject.getAsJsonPrimitive("id").getAsString();
            final UUID uuid = this.convertMojangUuid(mojangUuidString);

            final String validUsername = jsonObject.getAsJsonPrimitive("name").getAsString();
            final boolean premium = ! (jsonObject.has("demo") && jsonObject.getAsJsonPrimitive("demo").getAsBoolean());

            return Optional.of(new UsernameDetails(validUsername, uuid, premium, Instant.now()));
        }
        catch (final Exception e)
        {
            log.error("Failed to query Mojang API to check username {}", username, e);
            throw new MojangApiException("Exception thrown while accessing Mojang API", e);
        }
    }

    private UUID convertMojangUuid(final String mojangUuid)
    {
        final Matcher uuidMatcher = MojangCacheImpl.UUID_PATTERN.matcher(mojangUuid);
        return UUID.fromString(uuidMatcher.replaceAll("$1-$2-$3-$4-$5"));
    }

    private UUID getCrackedUuidFromName(final String username)
    {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }
}
