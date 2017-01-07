package pl.north93.zgame.datashare.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import pl.north93.zgame.datashare.api.data.IDataUnitPersistence;
import pl.north93.zgame.datashare.api.data.IDataUnitSerialization;
import pl.north93.zgame.datashare.sharedimpl.DataContainer;

public interface IDataShareManager
{
    void registerDataUnit(String name, IDataUnitSerialization serialization, IDataUnitPersistence persistence);

    void savePlayer(DataSharingGroup group, Player player);

    void loadPlayer(DataSharingGroup group, UUID playerId);

    void applyDataTo(DataSharingGroup group, Player player, DataContainer container);
}
