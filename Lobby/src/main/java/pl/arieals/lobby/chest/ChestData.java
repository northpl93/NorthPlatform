package pl.arieals.lobby.chest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.players.IPlayer;

/**
 * Klasa wrapujaca IPlayera sluzaca do zarzadzania iloscia skrzynek.
 */
class ChestData
{
    private static final MetaKey CHESTS = MetaKey.get("chests");
    private final IPlayer player;

    public ChestData(final IPlayer player)
    {
        this.player = player;
    }

    @SuppressWarnings("unchecked")
    public int getChests(final ChestType type)
    {
        final Map<String, Integer> chests = (Map<String, Integer>) this.player.getMetaStore().get(CHESTS);
        if (chests == null)
        {
            return 0;
        }
        return chests.getOrDefault(type.getName(), 0);
    }

    @SuppressWarnings("unchecked")
    public void setChests(final ChestType type, final int newChests)
    {
        Map<String, Integer> chests = (Map<String, Integer>) this.player.getMetaStore().get(CHESTS);
        if (chests == null)
        {
            chests = new HashMap<>(1);
            this.player.getMetaStore().set(CHESTS, chests);
        }
        chests.put(type.getName(), newChests);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
