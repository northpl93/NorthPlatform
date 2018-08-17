package pl.north93.zgame.api.chat.global.impl;

import javax.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;

/**
 * Przechowuje informacje dotyczące pokojów czatu danego gracza.
 */
/*default*/ class ChatPlayerData
{
    private static final MetaKey CHAT_DATA = MetaKey.get("chatData");
    private String      mainRoomId;
    private Set<String> rooms;

    public ChatPlayerData()
    {
        this.rooms = new HashSet<>(4);
    }

    @Nullable
    public String getMainRoomId()
    {
        return this.mainRoomId;
    }

    public void setMainRoomId(final String mainRoomId)
    {
        this.mainRoomId = mainRoomId;
    }

    public Set<String> getRooms()
    {
        return this.rooms;
    }

    /**
     * Pobiera z metadanych podanego gracza instancję tej klasy.
     *
     * @param player Gracz z którego pobieramy instancję.
     * @return Instancja tej klasy powiązana z graczem.
     */
    public static ChatPlayerData get(final IOnlinePlayer player)
    {
        final MetaStore metaStore = player.getOnlineMetaStore();
        return (ChatPlayerData) metaStore.getInternalMap().computeIfAbsent(CHAT_DATA, key -> new ChatPlayerData());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mainRoomId", this.mainRoomId).append("rooms", this.rooms).toString();
    }
}
