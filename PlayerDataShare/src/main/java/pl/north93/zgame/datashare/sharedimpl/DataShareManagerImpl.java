package pl.north93.zgame.datashare.sharedimpl;

import static java.text.MessageFormat.format;

import static pl.north93.zgame.api.global.utils.StringUtils.toBytes;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.datashare.api.DataSharingGroup;
import pl.north93.zgame.datashare.api.IDataShareManager;
import pl.north93.zgame.datashare.api.data.IDataUnit;
import pl.north93.zgame.datashare.api.data.IDataUnitPersistence;
import pl.north93.zgame.datashare.api.data.IDataUnitSerialization;

public class DataShareManagerImpl implements IDataShareManager
{
    @Inject
    private ApiCore             apiCore;
    @Inject
    private RedisSubscriber     subscriber;
    @Inject
    private TemplateManager     msgPack;
    @Inject
    private IObservationManager observer;
    private final ShareDataDao                    dataDao   = new ShareDataDao();
    private final Map<String, RegisteredDataUnit> dataUnits = new HashMap<>();

    @Override
    public void registerDataUnit(final String name, final IDataUnitSerialization serialization, final IDataUnitPersistence persistence)
    {
        //noinspection unchecked
        this.dataUnits.put(name, new RegisteredDataUnit(name, serialization, persistence));
    }

    @Override
    public void savePlayer(final DataSharingGroup group, final Player player, final boolean redis)
    {
        if (! player.isDataLoaded())
        {
            final String message = "[PlayerDataShare] savePlayer({0},{1},{2}) has been called, but player data isn't loaded! Skipped saving...";
            this.apiCore.getLogger().warning(format(message, group.getName(), player.getName(), redis));
            return;
        }
        final HashMap<String, IDataUnit> dataUnitHashMap = new HashMap<>();
        final Document document = new Document("uuid", player.getUniqueId());
        document.put("savedAt", System.currentTimeMillis());
        document.put("savedBy", this.apiCore.getId());
        for (final String dataUnitName : group.getDataUnits())
        {
            final RegisteredDataUnit dataUnit = this.dataUnits.get(dataUnitName);
            final IDataUnit dataObject = dataUnit.getSerialization().toRedis(player);
            dataUnitHashMap.put(dataUnitName, dataObject);
            document.put(dataUnitName, dataUnit.getPersistence().toDatabase(dataObject));
        }

        if (redis)
        {
            final byte[] bytes = this.msgPack.serialize(DataContainer.class, new DataContainer(player.getUniqueId(), dataUnitHashMap));
            this.subscriber.publish("playersdata:" + group.getName(), bytes);
        }

        this.dataDao.save(group.getName(), document);
    }

    @Override
    public void loadPlayer(final DataSharingGroup group, final UUID playerId)
    {
        final Document document = Optional.ofNullable(this.dataDao.load(group.getName(), playerId)).orElseGet(Document::new);
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

        final Value<byte[]> cache = this.observer.get(byte[].class, "playersdata:" + group.getName() + playerId);
        cache.setExpire(bytes, 10, TimeUnit.SECONDS);

        this.subscriber.publish("playersdata:" + group.getName(), bytes);
    }

    @Override
    public DataContainer getFromRedisKey(final DataSharingGroup group, final UUID playerId)
    {
        final Value<byte[]> cache = this.observer.get(byte[].class, "playersdata:" + group.getName() + playerId);
        final byte[] bytes = cache.getAndDelete();
        if (bytes == null)
        {
            return null;
        }
        return this.msgPack.deserialize(DataContainer.class, bytes);
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
        player.setDataLoaded(true);

        final String message = "applyDataTo({0}, {1}, data container with size {2})";
        this.apiCore.getLogger().fine(format(message, group.getName(), player.getName(), data.size()));
    }

    @Override
    public JoiningPolicy getChatPolicy(final DataSharingGroup group)
    {
        return this.observer.get(JoiningPolicy.class, "pds:chatpolicy:" + group.getName()).getOr(() -> JoiningPolicy.EVERYONE);
    }

    @Override
    public void setChatPolicy(final DataSharingGroup group, final JoiningPolicy policy)
    {
        this.observer.get(JoiningPolicy.class, "pds:chatpolicy:" + group.getName()).set(policy);
    }

    /*@Override
    public boolean isChatEnabled(final DataSharingGroup group)
    {
        return this.observer.get(Boolean.class, "pds:chatisenabled:" + group.getName()).getOr(() -> Boolean.TRUE);
    }

    @Override
    public void setChatEnabled(final DataSharingGroup group, final boolean enabled)
    {
        this.observer.get(Boolean.class, "pds:chatisenabled:" + group.getName()).set(enabled);
    }*/

    @Override
    public void broadcast(final DataSharingGroup group, final String message)
    {
        this.subscriber.publish("broadcast:" + group.getName(), toBytes(message));
    }

    @Override
    public void ann(final DataSharingGroup group, final String message)
    {
        this.subscriber.publish("ann:" + group.getName(), toBytes(message));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("dataUnits", this.dataUnits).toString();
    }
}
