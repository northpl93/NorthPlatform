package pl.arieals.skyblock.quests.server.impl;

import static java.util.Collections.unmodifiableCollection;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import com.mongodb.client.MongoCollection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.arieals.skyblock.quests.server.api.IServerQuestsComponent;
import pl.arieals.skyblock.quests.server.api.IServerQuestsManager;
import pl.arieals.skyblock.quests.shared.api.IQuest;
import pl.arieals.skyblock.quests.shared.api.ITrackedStatistic;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.storage.StorageConnector;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;

public class ServerQuestsManagerImpl implements IServerQuestsManager
{
    @Inject
    private StorageConnector       storage;
    @Inject
    private RedisSubscriber        subscriber;
    @Inject
    private IServerQuestsComponent component;
    private Map<UUID, IQuest>      quests;

    public ServerQuestsManagerImpl()
    {
        this.downloadQuests();
        this.subscriber.subscribe("skyquests_update", (channel, message) -> this.downloadQuests());
    }

    private void downloadQuests()
    {
        final List<IQuest> quests = this.component.getQuestsController().getQuests();
        for (final IQuest quest : quests)
        {
            this.quests.put(quest.getId(), quest);
        }
    }

    @Override
    public IQuest getQuest(final UUID questId)
    {
        return this.quests.get(questId);
    }

    @Override
    public Collection<IQuest> getQuests()
    {
        return unmodifiableCollection(this.quests.values());
    }

    @Override
    public void resetQuest(final IQuest quest, final UUID playerId)
    {

    }

    @Override
    public void bumpStatisticIf(final UUID player, final Predicate<ITrackedStatistic> condition)
    {
        final MongoCollection<Document> quests = this.storage.getMainDatabase().getCollection("quests");

        // todo

        for (final IQuest quest : this.quests.values())
        {
            for (final ITrackedStatistic statistic : quest.getTrackedStatistics())
            {
                if (! condition.test(statistic))
                {
                    continue;
                }


            }
        }
    }

    @Override
    public void generateNewQuests()
    {
        this.component.getQuestsController().generateNewQuests();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("quests", this.quests).toString();
    }
}
