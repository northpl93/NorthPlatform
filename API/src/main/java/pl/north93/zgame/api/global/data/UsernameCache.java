package pl.north93.zgame.api.global.data;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;


import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;

public class UsernameCache extends Component
{
    private final JsonParser               json = new JsonParser();
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector               storage;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager            observationManager;
    private Cache<String, UsernameDetails> localCache;
    private MongoCollection<Document>      mongoCache;

    @Override
    protected void enableComponent()
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
    protected void disableComponent()
    {
    }

    public static class UsernameDetails
    {
        private String  validSpelling;
        private UUID    uuid;
        private Boolean isPremium;
        private Date    fetchTime;

        public UsernameDetails() // serialization
        {
        }

        public UsernameDetails(final String validSpelling, final UUID uuid, final boolean isPremium, final Date fetchTime)
        {
            this.validSpelling = validSpelling;
            this.uuid = uuid;
            this.isPremium = isPremium;
            this.fetchTime = fetchTime;
        }

        public String getValidSpelling()
        {
            return this.validSpelling;
        }

        public UUID getUuid()
        {
            return this.uuid;
        }

        public boolean isPremium()
        {
            return this.isPremium;
        }

        public Date getFetchTime()
        {
            return this.fetchTime;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("validSpelling", this.validSpelling).append("isPremium", this.isPremium).toString();
        }
    }

    private UsernameDetails fillCache(final String username)
    {
        final Pattern usernamePattern = compile('^' + Pattern.quote(username) + '$', CASE_INSENSITIVE);
        final Document results = this.mongoCache.find(new Document("validSpelling", usernamePattern)).first();
        if (results != null)
        {
            return new UsernameDetails(results.getString("validSpelling"), results.get("uuid", UUID.class), results.getBoolean("isPremium"), results.getDate("fetchTime"));
        }

        final Optional<UsernameDetails> usernameDetails = this.queryMojangApi(username);
        if (usernameDetails.isPresent())
        {
            final UsernameDetails fromMojang = usernameDetails.get();
            final Document document = new Document();
            document.put("validSpelling", fromMojang.validSpelling);
            document.put("uuid", fromMojang.uuid);
            document.put("isPremium", fromMojang.isPremium);
            document.put("fetchTime", fromMojang.fetchTime);
            this.mongoCache.updateOne(new Document("validSpelling", usernamePattern), new Document("$set", document), new UpdateOptions().upsert(true));
            return fromMojang;
        }

        return null;
    }

    private Optional<UsernameDetails> queryMojangApi(final String username)
    {
        try
        {
            final String response = IOUtils.toString(new URL("https://api.mojang.com/users/profiles/minecraft/" + username));
            if (StringUtils.isEmpty(response))
            {
                return Optional.of(new UsernameDetails(username, UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8)), false, new Date()));
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
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<UsernameDetails> getUsernameDetails(final String username)
    {
        return Optional.ofNullable(this.localCache.get(username));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("localCache", this.localCache).append("json", this.json).toString();
    }
}
