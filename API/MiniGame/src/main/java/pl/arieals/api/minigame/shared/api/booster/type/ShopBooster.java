package pl.arieals.api.minigame.shared.api.booster.type;

import pl.arieals.api.minigame.shared.api.booster.IBooster;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IPlayer;

public class ShopBooster implements IBooster
{
    public static final MetaKey SHOP_BOOSTER_EXPIRATION = MetaKey.get("shopBoosterExpiration");

    @Override
    public String getId()
    {
        return "shop";
    }

    @Override
    public double getMultiplier(final IPlayer player)
    {
        return isBoosterValid(player) ? 0.5 : 0;
    }

    public static boolean isBoosterValid(final IPlayer player)
    {
        final MetaStore metaStore = player.getMetaStore();
        if (! metaStore.contains(SHOP_BOOSTER_EXPIRATION))
        {
            return false;
        }

        final long expiration = metaStore.get(SHOP_BOOSTER_EXPIRATION);
        return expiration > System.currentTimeMillis();
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
