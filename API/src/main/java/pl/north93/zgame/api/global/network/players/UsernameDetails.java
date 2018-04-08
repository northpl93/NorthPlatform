package pl.north93.zgame.api.global.network.players;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Przedstawia zcachowane informacje o danej nazwie uzytkownika,
 * jej statusie premium, mapowaniu na uuid.
 */
public final class UsernameDetails
{
    private String  validSpelling;
    private UUID    uuid;
    private Boolean isPremium;
    private Date    fetchTime;

    public UsernameDetails() // serialization
    {
    }

    public UsernameDetails(final String validSpelling, final Date fetchTime)
    {
        this.validSpelling = validSpelling;
        this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + validSpelling).getBytes(StandardCharsets.UTF_8));
        this.isPremium = false;
        this.fetchTime = fetchTime;
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
