package pl.north93.zgame.datashare.sharedimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.IDataShareManager;
import pl.north93.zgame.datashare.api.data.IDataUnit;
import pl.north93.zgame.datashare.api.data.IDataUnitPersistence;
import pl.north93.zgame.datashare.api.data.IDataUnitSerialization;

public class DataShareManagerImpl implements IDataShareManager
{
    @InjectComponent("API.Database.Redis.Subscriber")
    private RedisSubscriber subscriber;
    @InjectComponent("API.Database.Redis.MessagePackSerializer")
    private TemplateManager msgPack;
    private final ShareDataDao                    dataDao   = new ShareDataDao();
    private final Map<String, RegisteredDataUnit> dataUnits = new HashMap<>();

    @Override
    public void registerDataUnit(final String name, final IDataUnitSerialization serialization, final IDataUnitPersistence persistence)
    {
        //noinspection unchecked
        this.dataUnits.put(name, new RegisteredDataUnit(name, serialization, persistence));
    }

    @Override
    public void savePlayer(final DataSharingGroup group, final Player player)
    {
        final HashMap<String, IDataUnit> dataUnitHashMap = new HashMap<>();
        final Document document = new Document("uuid", player.getUniqueId());
        for (final String dataUnitName : group.getDataUnits())
        {
            final RegisteredDataUnit dataUnit = this.dataUnits.get(dataUnitName);
            final IDataUnit dataObject = dataUnit.getSerialization().toRedis(player);
            dataUnitHashMap.put(dataUnitName, dataObject);
            document.put(dataUnitName, dataUnit.getPersistence().toDatabase(dataObject));
        }

        final byte[] bytes = this.msgPack.serialize(DataContainer.class, new DataContainer(player.getUniqueId(), dataUnitHashMap));
        this.subscriber.publish("playersdata:" + group.getName(), bytes);

        this.dataDao.save(group.getName(), document);
    }

    @Override
    public void loadPlayer(final DataSharingGroup group, final UUID playerId)
    {
        final Document document = this.dataDao.load(group.getName(), playerId);
        if (document == null)
        {
            return; // player is first time in this group
        }
        final HashMap<String, IDataUnit> dataUnitHashMap = new HashMap<>();

        for (final String dataUnitName : group.getDataUnits())
        {
            final RegisteredDataUnit dataUnit = this.dataUnits.get(dataUnitName);
            final Document dataUnitDocument = document.get(dataUnitName, Document.class);
            if (dataUnitDocument == null)
            {
                continue;
            }
            final IDataUnit iDataUnit = dataUnit.getPersistence().fromDatabase(dataUnitDocument);
            dataUnitHashMap.put(dataUnitName, iDataUnit);
        }
        final byte[] bytes = this.msgPack.serialize(DataContainer.class, new DataContainer(playerId, dataUnitHashMap));
        this.subscriber.publish("playersdata:" + group.getName(), bytes);
    }

    @Override
    public void applyDataTo(final DataSharingGroup group, final Player player, final DataContainer container)
    {
        final Map<String, IDataUnit> data = container.getData();
        for (final String dataUnitName : group.getDataUnits())
        {
            final RegisteredDataUnit dataUnit = this.dataUnits.get(dataUnitName);
            final IDataUnit dataUnitDocument = data.get(dataUnitName);
            if (dataUnitDocument == null)
            {
                continue;
            }
            dataUnit.getSerialization().fromRedis(player, dataUnitDocument);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("dataUnits", this.dataUnits).toString();
    }
}
