package pl.north93.zgame.api.bukkit.utils;

import static org.diorite.commons.reflections.DioriteReflectionUtils.getField;


import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.metadata.EntityMetadataStore;
import org.bukkit.craftbukkit.v1_12_R1.metadata.PlayerMetadataStore;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import org.apache.commons.lang3.StringUtils;

import org.diorite.commons.reflections.FieldAccessor;

/**
 * Utilsy do Bukkitowego {@link org.bukkit.metadata.Metadatable}.
 */
public final class MetadataUtils
{
    private static final FieldAccessor<Map<String, Map<Plugin, MetadataValue>>> internalMap;

    static
    {
        internalMap = getField(MetadataStoreBase.class, "metadataMap");
    }

    /**
     * Usuwa wszystkie metadane entity o UUID podanym w argumencie.
     *
     * @param entityId UUID entity któremu usuwamy metadane.
     */
    public static void removeEntityMetadata(final UUID entityId)
    {
        final String keyStart = entityId + ":";
        removeMetadata(getEntityMetadata(), keyStart);
    }

    /**
     * Usuwa wszystkie metadane gracza podanego w argumencie.
     *
     * @param player Gracz któremu kasujemy metadane.
     */
    public static void removePlayerMetadata(final Player player)
    {
        final String keyStart = player.getName().toLowerCase(Locale.ROOT) + ":";
        removeMetadata(getPlayerMetadata(), keyStart);
    }

    private static EntityMetadataStore getEntityMetadata()
    {
        final CraftServer server = (CraftServer) Bukkit.getServer();
        return server.getEntityMetadata();
    }

    private static PlayerMetadataStore getPlayerMetadata()
    {
        final CraftServer server = (CraftServer) Bukkit.getServer();
        return server.getPlayerMetadata();
    }

    private static void removeMetadata(final MetadataStoreBase<?> metadataStoreBase, final String keyStart)
    {
        final Map<String, Map<Plugin, MetadataValue>> map = internalMap.get(metadataStoreBase);
        assert map != null;

        map.entrySet().removeIf(entry -> StringUtils.startsWith(entry.getKey(), keyStart));
    }
}
