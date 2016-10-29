package pl.north93.zgame.api.global.data;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.collections.maps.CaseInsensitiveMap;

import pl.north93.zgame.api.global.API;

public class UsernameCache
{
    private final Map<String, UsernameDetails> localCache = new CaseInsensitiveMap<>(512);
    private final JsonParser json = new JsonParser();

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
        try (final Connection mysql = API.getMysql().getConnection())
        {
            final PreparedStatement statement = mysql.prepareStatement("SELECT username,premium,fetchtime FROM username_cache WHERE LOWER(username) = LOWER(?)");
            statement.setString(1, username);

            final ResultSet results = statement.executeQuery();
            if (results.next())
            {
                return Optional.of(new UsernameDetails(results.getString("username"), results.getBoolean("premium"), results.getTimestamp("fetchtime")));
            }
            else
            {
                return Optional.empty();
            }
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
            return Optional.empty();
        }
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
            this.insertUserDetailsToCache(detailsFromMojang);

            return fromMojang;
        }
        return Optional.empty();
    }

    private void insertUserDetailsToCache(final UsernameDetails details)
    {
        try (final Connection connection = API.getMysql().getConnection())
        {
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO username_cache (username,premium,fetchtime) VALUES (?, ?, ?)");
            preparedStatement.setString(1, details.getValidSpelling());
            preparedStatement.setBoolean(2, details.isPremium());
            preparedStatement.setTimestamp(3, new Timestamp(details.getFetchTime().getTime()));

            preparedStatement.execute();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("localCache", this.localCache).append("json", this.json).toString();
    }
}
