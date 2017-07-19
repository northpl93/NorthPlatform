package pl.north93.zgame.api.global.network.players;

import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Maly obiekt umozliwiajacy jednoznaczne zidentyfikowanie gracza
 * w sieci.
 */
public final class Identity
{
    private final UUID uuid;
    private final String displayName;

    public Identity(final UUID uuid, final String displayName)
    {
        this.uuid = uuid;
        this.displayName = displayName;
    }

    public static Identity of(final Player bukkitPlayer) // nie jebnie o ile nie wykonamy na innej platformie
    {
        return new Identity(bukkitPlayer.getUniqueId(), bukkitPlayer.getDisplayName());
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final Identity identity = (Identity) o;

        return this.uuid.equals(identity.uuid);
    }

    @Override
    public int hashCode()
    {
        return this.uuid.hashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("displayName", this.displayName).toString();
    }
}
