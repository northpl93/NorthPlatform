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

    /**
     * Identity jest lekkim obiektem umozliwiajacym identyfikowanie
     * gracza w sieci. Ma zaimplementowane equals i hashCode
     * porownujace po UUID.
     * @return identity gracza.
     */
    Identity getIdentity();

    /**
     * Określa czy to konto zostało utworzone w online-mode.
     *
     * @return True jeśli to jest konto online-mode.
     */
    boolean isPremium();

    /**
     * Sprawdza czy dany gracz ma ustawiona niestandardowa nazwe
     * wyswietlana.
     * @return true jesli gracz ma niestandardowa nazwe wyswietlana.
     */
    boolean hasDisplayName();

    /**
     * Zwraca niestandardowa nazwe wyswietlana ustawiona dla
     * tego gracza. Jesli brak zwroci nick lub ostatnio znany nick.
     * @return nazwa wyswietlana gracza.
     */
    String getDisplayName();

    /**
     * Zmienia niestandardowa nazwe wyswietlana danego gracza.
     * Ustaw tu null jesli chcesz usunac nazwe.
     */
    void setDisplayName(String newName);

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
        return PrivateMessages.values()[metaStore.<Integer>get(policy)];
    }

    default void setPrivateMessagesPolicy(final PrivateMessages newPolicy)
    {
        this.getMetaStore().set(MetaKey.get("privateMessages"), newPolicy.ordinal());
    }

    /**
     * Sprawdza czy ten gracz jest online na serwerze.
     *
     * @return true - jesli gracz jest online.
     */
    boolean isOnline();

    default void setLocale(final Locale locale)
    {
        this.getMetaStore().set(MetaKey.get("lang"), locale.toLanguageTag());
    }

    /**
     * Zwraca MetaStore przechowujący dane tylko podczas gdy gracz jest online.
     * Przy wyjściu dane są tracone razem z instancją IOnlinePlayer.
     * Instancja IOfflinePlayer nie zapisuje ustawianych tu wartości.
     *
     * @return Instancja MetaStore przechowująca dane tylko gdy gracz jest online.
     */
    MetaStore getOnlineMetaStore();
}
