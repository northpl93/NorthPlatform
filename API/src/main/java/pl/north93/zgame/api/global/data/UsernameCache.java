package pl.north93.zgame.api.global.data;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import org.diorite.utils.collections.maps.CaseInsensitiveMap;

import pl.north93.zgame.api.global.ApiCore;

public class UsernameCache
{
    private final Map<String, UsernameDetails>     localCache = new CaseInsensitiveMap<>(512);
    private final JsonParser                       json = new JsonParser();
    private final MongoCollection<UsernameDetails> mongoCache = null; // TODO

    public UsernameCache(final ApiCore apiCore)
    {
        //this.mongoCache = apiCore.getMainDatabase().getCollection("username_cache", UsernameDetails.class);
    }

    public static class UsernameDetails
    {
        private final String  validSpelling;
        private final boolean isPremium;
        private final Date    fetchTime;

        public UsernameDetails(final String validSpelling, final boolean isPremium, final Date fetchTime)
        {
            this.validSpelling = validSpelling;
            this.isPremium = isPremium;
            this.fetchTime = fetchTime;
        }

        public String getValidSpelling()
        {
            return this.validSpelling;
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

    public Optional<UsernameDetails> queryCache(final String username)
    {
        final Iterator<UsernameDetails> results = this.mongoCache.find(new Document("username", "/" + username + "/i")).limit(1).iterator();
        if (results.hasNext())
        {
            return Optional.of(results.next());
        }

        return Optional.empty();
    }

    public Optional<UsernameDetails> queryMojangApi(final String username)
    {
        try
        {
            final String response = IOUtils.toString(new URL("https://api.mojang.com/users/profiles/minecraft/" + username));
            if (StringUtils.isEmpty(response))
            {
                return Optional.of(new UsernameDetails(username, false, new Date()));
            }
            final JsonObject jsonObject = this.json.parse(response).getAsJsonObject();

            final String validUsername = jsonObject.getAsJsonPrimitive("name").getAsString();
            final boolean premium = !(jsonObject.has("demo") && jsonObject.getAsJsonPrimitive("demo").getAsBoolean());

            return Optional.of(new UsernameDetails(validUsername, premium, new Date()));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<UsernameDetails> getUsernameDetails(final String username)
    {
        final UsernameDetails fromLocalCache = this.localCache.get(username);
        if (fromLocalCache != null)
        {
            return Optional.of(fromLocalCache);
        }
        final Optional<UsernameDetails> fromCache = this.queryCache(username);
        if (fromCache.isPresent())
        {
            this.localCache.put(username, fromCache.get());
            return fromCache;
        }
        final Optional<UsernameDetails> fromMojang = this.queryMojangApi(username);
        if (fromMojang.isPresent())
        {
            final UsernameDetails detailsFromMojang = fromMojang.get();

            this.localCache.put(username, detailsFromMojang);
            this.mongoCache.insertOne(detailsFromMojang);

            return fromMojang;
        }
        return Optional.empty();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("localCache", this.localCache).append("json", this.json).toString();
    }
}
