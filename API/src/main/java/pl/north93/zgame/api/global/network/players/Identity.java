package pl.north93.zgame.api.global.network.players;

import javax.annotation.Nullable;

import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Maly obiekt umozliwiajacy jednoznaczne zidentyfikowanie gracza
 * w sieci.
 */
public final class Identity
{
    private final UUID uuid;
    private final String nick;
    private final String displayName;

    private Identity(final UUID uuid, final String nick, final String displayName)
    {
        this.uuid = uuid;
        this.nick = nick;
        this.displayName = displayName;
    }

    public static Identity of(final Player bukkitPlayer) // nie jebnie o ile nie wykonamy na innej platformie
    {
        return new Identity(bukkitPlayer.getUniqueId(), bukkitPlayer.getName(), bukkitPlayer.getDisplayName());
    }

    public static Identity of(final ProxiedPlayer proxiedPlayer) // nie jebnie o ile nie wykonamy na innej platformie
    {
        return new Identity(proxiedPlayer.getUniqueId(), proxiedPlayer.getName(), proxiedPlayer.getDisplayName());
    }

    public static Identity of(final IOnlinePlayer onlinePlayer)
    {
        return new Identity(onlinePlayer.getUuid(), onlinePlayer.getNick(), onlinePlayer.getDisplayName());
    }

    public static Identity create(final UUID uuid, final String nick, final String displayName)
    {
        return new Identity(uuid, nick, displayName);
    }

    public @Nullable UUID getUuid()
    {
        return this.uuid;
    }

    public @Nullable String getNick()
    {
        return this.nick;
    }

    public @Nullable String getDisplayName()
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("nick", this.nick).append("displayName", this.displayName).toString();
    }
}
