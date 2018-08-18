package pl.arieals.globalshops.server.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Reprezentuje dane gracza przechowywane w zserializoanej postaci.
 */
@ToString
@NoArgsConstructor
/*default*/ final class PlayerShopData implements Serializable
{
    private final ArrayList<PlayerItemInfo> items       = new ArrayList<>();
    private final HashMap<String, String>   activeItems = new HashMap<>();

    public PlayerItemInfo getItemInfo(final String groupId, final String itemId)
    {
        return this.getOptionalItemInfo(groupId, itemId).orElseGet(() ->
        {
            final PlayerItemInfo itemInfo = new PlayerItemInfo(groupId, itemId, 0, 0);
            this.items.add(itemInfo);

            return itemInfo;
        });
    }

    public Optional<PlayerItemInfo> getOptionalItemInfo(final String groupId, final String itemId)
    {
        for (final PlayerItemInfo item : this.items)
        {
            if (item.matches(groupId, itemId))
            {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    public Collection<PlayerItemInfo> getItems()
    {
        return Collections.unmodifiableCollection(this.items);
    }

    public Map<String, String> getActiveItems()
    {
        return this.activeItems;
    }
}
