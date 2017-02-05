package pl.north93.zgame.datashare.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.datashare.api.data.IDataUnitPersistence;
import pl.north93.zgame.datashare.api.data.IDataUnitSerialization;
import pl.north93.zgame.datashare.sharedimpl.DataContainer;

public interface IDataShareManager
{
    void registerDataUnit(String name, IDataUnitSerialization serialization, IDataUnitPersistence persistence);

    void savePlayer(DataSharingGroup group, Player player, boolean redis);

    void loadPlayer(DataSharingGroup group, UUID playerId);

    DataContainer getFromRedisKey(DataSharingGroup group, UUID playerId);

    void applyDataTo(DataSharingGroup group, Player player, DataContainer container);

    //boolean isChatEnabled(DataSharingGroup group);

    //void setChatEnabled(DataSharingGroup group,  boolean enabled);

    JoiningPolicy getChatPolicy(DataSharingGroup group);

    void setChatPolicy(DataSharingGroup group, JoiningPolicy policy);

    void broadcast(DataSharingGroup group, String message);

    void ann(DataSharingGroup group, String message);
}
