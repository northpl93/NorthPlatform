package pl.north93.northplatform.api.minigame.shared.api.booster.type;

import pl.north93.northplatform.api.minigame.shared.api.booster.IBooster;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IPlayer;

public class ShopBooster implements IBooster
{
    public static final MetaKey SHOP_BOOSTER_EXPIRATION = MetaKey.get("shopBoosterExpiration");

    @Override
    public String getId()
    {
        return "shop";
    }

    @Override
    public long getExpiration(final IPlayer player)
    {
        return getBoosterExpiration(player);
    }

    @Override
    public double getMultiplier(final IPlayer player)
    {
        return this.isBoosterValid(player) ? 0.5 : 0;
    }

    @Override
    public boolean isBoosterValid(final IPlayer player)
    {
        return getBoosterExpiration(player) > System.currentTimeMillis();
    }

    public static long getBoosterExpiration(final IPlayer player)
    {
        final MetaStore metaStore = player.getMetaStore();
        if (! metaStore.contains(SHOP_BOOSTER_EXPIRATION))
        {
            return -1;
        }

        return metaStore.get(SHOP_BOOSTER_EXPIRATION);
    }

    public static void extendBoost(final IPlayer player, final long millis)
    {
        final long currentTimeMillis = System.currentTimeMillis();

        final MetaStore metaStore = player.getMetaStore();
        if (metaStore.contains(SHOP_BOOSTER_EXPIRATION))
        {
            final long expiration = metaStore.get(SHOP_BOOSTER_EXPIRATION);
            if (expiration > currentTimeMillis)
            {
                metaStore.set(SHOP_BOOSTER_EXPIRATION, expiration + millis);
                return;
            }
        }

        metaStore.set(SHOP_BOOSTER_EXPIRATION, currentTimeMillis + millis);
    }
}
