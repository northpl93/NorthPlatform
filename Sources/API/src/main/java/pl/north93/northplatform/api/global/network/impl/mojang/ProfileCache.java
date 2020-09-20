package pl.north93.northplatform.api.global.network.impl.mojang;

import static java.text.MessageFormat.format;

import static pl.north93.northplatform.api.global.network.impl.mojang.MojangCacheImpl.JSON_PARSER;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.network.mojang.CachedMojangProfile;
import pl.north93.northplatform.api.global.network.mojang.CachedMojangProfileProperty;
import pl.north93.northplatform.api.global.storage.StorageConnector;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

@Slf4j
/*default*/ class ProfileCache
{
    private final MongoCollection<CachedMojangProfile> collection;

    @Bean
    private ProfileCache(final StorageConnector connector)
    {
        final MongoCollection<Document> collection = connector.getMainDatabase().getCollection("profile_cache");
        this.collection = collection.withDocumentClass(CachedMojangProfile.class);
    }

    public void updateProfile(final CachedMojangProfile profile)
    {
        this.collection.insertOne(profile);
    }

    public Optional<CachedMojangProfile> getProfile(final UUID profileId)
    {
        final CachedMojangProfile cachedProfile = this.collection.find(new Document("_id", profileId)).first();
        if (cachedProfile == null)
        {
            return this.queryMojangAndFillCache(profileId);
        }

        return Optional.of(cachedProfile);
    }

    private Optional<CachedMojangProfile> queryMojangAndFillCache(final UUID uuid)
    {
        final String url = this.composeProfileFetchUrl(uuid);

        try
        {
            final String response = this.doQueryMojang(new URL(url));
            if (StringUtils.isEmpty(response))
            {
                return Optional.empty();
            }

            final JsonObject jsonObject = JSON_PARSER.parse(response).getAsJsonObject();

            final String name = jsonObject.get("name").getAsString();

            final JsonArray jsonProperties = jsonObject.get("properties").getAsJsonArray();
            final List<CachedMojangProfileProperty> properties = this.buildPropertiesList(jsonProperties);

            final CachedMojangProfile cachedProfile = new CachedMojangProfile(uuid, name, properties);
            this.collection.insertOne(cachedProfile);

            log.info("Fetched profile from Mojang {}", cachedProfile);
            return Optional.of(cachedProfile);
        }
        catch (final Exception exception)
        {
            log.error("Failed to fetch {} profile", uuid, exception);
            return Optional.empty();
        }
    }

    private String doQueryMojang(final URL url) throws IOException
    {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        final boolean isError = urlConnection.getResponseCode() >= HttpURLConnection.HTTP_INTERNAL_ERROR;
        try (final InputStream stream = isError ? urlConnection.getErrorStream() : urlConnection.getInputStream())
        {
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        }
    }

    private List<CachedMojangProfileProperty> buildPropertiesList(final JsonArray jsonArray)
    {
        final List<CachedMojangProfileProperty> properties = new ArrayList<>(jsonArray.size());
        for (final JsonElement element : jsonArray)
        {
            final JsonObject object = element.getAsJsonObject();

            final String name = object.get("name").getAsString();
            final String value = object.get("value").getAsString();
            final String signature = object.has("signature") ? object.get("signature").getAsString() : null;

            properties.add(new CachedMojangProfileProperty(name, value, signature));
        }

        return properties;
    }

    private String composeProfileFetchUrl(final UUID profileId)
    {
        final String mojangUuid = StringUtils.remove(profileId.toString(), '-');
        return format("https://sessionserver.mojang.com/session/minecraft/profile/{0}?unsigned=false", mojangUuid);
    }
}
