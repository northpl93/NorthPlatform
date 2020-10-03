package pl.north93.northplatform.api.global.network.impl.players;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.network.players.IOfflinePlayer;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.serializer.platform.annotations.NorthField;

/**
 * Represents a player saved in the MongoDB database.
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
/*default*/ class PersistedPlayer
{
    private UUID uuid;
    private boolean premium;
    private String latestKnownUsername;
    private String displayName;
    private String group;
    private Instant groupExpireAt;
    private boolean savedWhileOnline;
    private Instant savedAt;
    @NorthField(type = HashMap.class)
    private Map<String, Object> metadata;

    public static PersistedPlayer create(final IPlayer player)
    {
        final PersistedPlayerBuilder builder = new PersistedPlayerBuilder();

        builder.uuid(player.getUuid());
        builder.premium(player.isPremium());
        builder.displayName(player.hasDisplayName() ? player.getDisplayName() : null);
        builder.group(player.getGroup().getName());
        builder.groupExpireAt(Instant.ofEpochMilli(player.getGroupExpireAt()));
        builder.savedAt(Instant.now());

        if (player instanceof IOnlinePlayer)
        {
            final IOnlinePlayer onlinePlayer = (IOnlinePlayer) player;
            builder.latestKnownUsername(onlinePlayer.getNick());
            builder.savedWhileOnline(true);
        }
        else if (player instanceof IOfflinePlayer)
        {
            builder.latestKnownUsername(player.getLatestNick());
            builder.savedWhileOnline(false);
        }

        final Map<String, Object> metadata = new HashMap<>();
        for (final Map.Entry<MetaKey, Object> entry : player.getMetaStore().getInternalMap().entrySet())
        {
            final MetaKey metaKey = entry.getKey();
            metadata.put(metaKey.getKey(), entry.getValue());
        }
        builder.metadata(metadata);

        return builder.build();
    }
}
