package pl.north93.zgame.api.global.network.players;

import java.util.Locale;
import java.util.UUID;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.metadata.Metadatable;
import pl.north93.zgame.api.global.network.PrivateMessages;
import pl.north93.zgame.api.global.permissions.Group;

public interface IPlayer extends Metadatable
{
    UUID getUuid();

    /**
     * Zwraca nick pod którym był ostatnio widziany gracz.
     * @return ostatnio widziany nick.
     */
    String getLatestNick();

    boolean isBanned();

    void setBanned(boolean banned);

    Group getGroup();

    long getGroupExpireAt(); // zwraca w milis kiedy grupa wygasa

    void setGroup(Group group);

    void setGroupExpireAt(long expireAt);

    default boolean isGroupExpired()
    {
        final long groupExpireAt = this.getGroupExpireAt();
        return groupExpireAt != 0 && System.currentTimeMillis() > groupExpireAt;
    }

    default PrivateMessages privateMessagesPolicy()
    {
        final MetaKey policy = MetaKey.get("privateMessages");
        final MetaStore metaStore = this.getMetaStore();
        if (! metaStore.contains(policy))
        {
            return PrivateMessages.ENABLED;
        }
        return PrivateMessages.values()[metaStore.getInteger(policy)];
    }

    default void setPrivateMessagesPolicy(final PrivateMessages newPolicy)
    {
        this.getMetaStore().setInteger(MetaKey.get("privateMessages"), newPolicy.ordinal());
    }

    /**
     * Sprawdza czy ten gracz jest online na serwerze.
     *
     * @return true - jesli gracz jest online.
     */
    boolean isOnline();

    default void setLocale(final Locale locale)
    {
        this.getMetaStore().setString(MetaKey.get("lang"), locale.toLanguageTag());
    }
}
