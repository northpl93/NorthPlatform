package pl.north93.zgame.itemshop.controller.handlers;

import java.util.HashMap;
import java.util.Map;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.itemshop.shared.IDataHandler;

public class ChestsDataHandler implements IDataHandler
{
    private static final MetaKey CHESTS = MetaKey.get("chests"); // pilnować żeby było zgodne z ChestData w Lobby
    @Inject
    private INetworkManager networkManager;

    @Override
    public String getId()
    {
        return "chests";
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean process(final Identity player, final Map<String, String> data)
    {
        final String chestType = data.get("type");
        final int amount = Integer.parseInt(data.get("amount"));

        return this.networkManager.getPlayers().access(player, playerObj ->
        {
            final MetaStore metaStore = playerObj.getMetaStore();

            Map<String, Integer> chests = (Map<String, Integer>) metaStore.get(CHESTS);
            if (chests == null)
            {
                chests = new HashMap<>(1);
                metaStore.set(CHESTS, chests);
            }

            chests.put(chestType, chests.getOrDefault(chestType, 0) + amount);
        });
    }
}
