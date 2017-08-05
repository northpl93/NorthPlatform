package pl.arieals.globalshops.server.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;

@SuppressWarnings("unchecked")
class PlayerData implements Serializable
{
    private static final MetaKey BOUGHT_ITEMS = MetaKey.get("globalShops_bought");
    private static final MetaKey ACTIVE_ITEMS = MetaKey.get("globalShops_active");
    private MetaStore store;

    public PlayerData(final MetaStore store)
    {
        this.store = store;
    }

    public Map<String, Integer> getBoughtItems()
    {
        Map<String, Integer> boughtItems = (Map<String, Integer>) this.store.get(BOUGHT_ITEMS);
        if (boughtItems == null)
        {
            boughtItems = new HashMap<>(0);
            this.store.set(BOUGHT_ITEMS, boughtItems);
        }
        return boughtItems;
    }

    public Map<String, String> getActiveItems()
    {
        Map<String, String> active = (Map<String, String>) this.store.get(ACTIVE_ITEMS);
        if (active == null)
        {
            active = new HashMap<>(0);
            this.store.set(ACTIVE_ITEMS, active);
        }
        return active;
    }
}
