package pl.north93.zgame.api.global.network.impl.mojang;

import java.util.Optional;
import java.util.regex.Pattern;

import com.google.gson.JsonParser;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.network.mojang.IMojangCache;
import pl.north93.zgame.api.global.network.mojang.UsernameDetails;

@Slf4j
class MojangCacheImpl implements IMojangCache
{
    static final Pattern UUID_PATTERN   = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    static final JsonParser JSON_PARSER = new JsonParser();
    private final UsernameCache usernameCache;

    @Bean
    private MojangCacheImpl(final UsernameCache usernameCache)
    {
        this.usernameCache = usernameCache;
    }

    @Override
    public Optional<UsernameDetails> getUsernameDetails(final String username)
    {
        return this.usernameCache.getUsernameDetails(username);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
