package pl.north93.zgame.api.global.network.players;

import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Maly obiekt umozliwiajacy jednoznaczne zidentyfikowanie gracza
 * w sieci.
 */
public final class Identity
{
    private final UUID uuid;
    private final String nick;

    private Identity(final UUID uuid, final String nick)
    {
        this.uuid = uuid;
        this.nick = nick;
    }

    public Identity(final Document document)
    {
        this(document.get("uuid", UUID.class), document.getString("nick"));
    }

    public static Identity of(final Player bukkitPlayer) // nie jebnie o ile nie wykonamy na innej platformie
    {
        return new Identity(bukkitPlayer.getUniqueId(), bukkitPlayer.getName());
    }

    public static Identity of(final ProxiedPlayer proxiedPlayer) // nie jebnie o ile nie wykonamy na innej platformie
    {
        return new Identity(proxiedPlayer.getUniqueId(), proxiedPlayer.getName());
    }

    public static Identity of(final IOnlinePlayer onlinePlayer)
    {
        return new Identity(onlinePlayer.getUuid(), onlinePlayer.getNick());
    }

    public static Identity create(final UUID uuid, final String nick)
    {
        return new Identity(uuid, nick);
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public String getNick()
    {
        return this.nick;
    }

    /**
     * Sprawdza czy to Identity jest poprawny, czyli czy ma ustawione UUID lub nick.
     * Jesli zarówno nick i UUID sa null to tu zostanie zwrócone false.
     *
     * @return True jeśli to Identity jest poprawne.
     */
    public boolean isValid()
    {
        return this.uuid != null || this.nick != null;
    }

    /**
     * Zamienia to Identity w dokument który moze zostac zapisany w MongoDB.
     *
     * @return Identity zapisane jako dokument bson.
     */
    public Document toDocument()
    {
        final Document document = new Document();
        document.put("uuid", this.uuid);
        document.put("nick", this.nick);

        return document;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("nick", this.nick).toString();
    }
}
