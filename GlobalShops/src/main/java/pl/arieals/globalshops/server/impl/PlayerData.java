package pl.arieals.globalshops.server.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public List<String> getBoughtItems()
    {
        List<String> boughtItems = (List<String>) this.store.get(BOUGHT_ITEMS);
        if (boughtItems == null)
        {
            boughtItems = new ArrayList<>(0);
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

    /*public Document toDocument()
    {
        final Document doc = new Document();
        doc.put("boughtItems", this.boughtItems);
        doc.put("activeItems", new Document((Map) this.activeItems));
        return doc;
    }*/
}
