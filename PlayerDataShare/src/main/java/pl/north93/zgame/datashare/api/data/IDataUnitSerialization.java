package pl.north93.zgame.datashare.api.data;

import org.bukkit.entity.Player;

public interface IDataUnitSerialization<T extends IDataUnit>
{
    T toRedis(Player player);

    void fromRedis(Player player, T dataUnit);
}
