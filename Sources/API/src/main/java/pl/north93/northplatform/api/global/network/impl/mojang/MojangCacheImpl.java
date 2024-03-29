package pl.north93.northplatform.api.global.network.impl.mojang;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import com.google.gson.JsonParser;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.network.mojang.CachedMojangProfile;
import pl.north93.northplatform.api.global.network.mojang.MojangApiException;
import pl.north93.northplatform.api.global.network.mojang.UsernameDetails;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.network.mojang.IMojangCache;

@Slf4j
class MojangCacheImpl implements IMojangCache
{
    static final Pattern UUID_PATTERN   = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    static final JsonParser JSON_PARSER = new JsonParser();
    private final UsernameCache usernameCache;
    private final ProfileCache  profileCache;

    @Bean
    private MojangCacheImpl(final UsernameCache usernameCache, final ProfileCache profileCache)
    {
        this.usernameCache = usernameCache;
        this.profileCache = profileCache;
    }

    @Override
    public UsernameDetails lookupUsernameAndUpdateDb(final String username) throws MojangApiException
    {
        return this.usernameCache.lookupUsernameAndUpdateDb(username);
    }

    @Override
    public Optional<UsernameDetails> lookupUsernameInLocalDatabase(final String username)
    {
        return this.usernameCache.lookupUsernameInLocalDatabase(username);
    }

    @Override
    public void updateProfile(final CachedMojangProfile profile)
    {
        this.profileCache.updateProfile(profile);
    }

    @Override
    public Optional<CachedMojangProfile> getProfile(final UUID profileId)
    {
        return this.profileCache.getProfile(profileId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
