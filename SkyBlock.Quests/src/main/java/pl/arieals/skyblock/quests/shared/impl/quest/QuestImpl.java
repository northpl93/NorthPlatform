package pl.arieals.skyblock.quests.shared.impl.quest;

import static java.util.Collections.unmodifiableList;


import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.skyblock.quests.shared.api.IQuest;
import pl.arieals.skyblock.quests.shared.api.ITrackedStatistic;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayListTemplate;

class QuestImpl implements IQuest
{
    private UUID                    uuid;
    private String                  name;
    @MsgPackCustomTemplate(ArrayListTemplate.class)
    private List<ITrackedStatistic> trackedStatistics;

    public QuestImpl() // for serialization
    {
    }

    public QuestImpl(final UUID uuid, final String name, final List<ITrackedStatistic> trackedStatistics)
    {
        this.uuid = uuid;
        this.name = name;
        this.trackedStatistics = trackedStatistics;
    }

    @Override
    public UUID getId()
    {
        return this.uuid;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public List<ITrackedStatistic> getTrackedStatistics()
    {
        return unmodifiableList(this.trackedStatistics);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("name", this.name).append("trackedStatistics", this.trackedStatistics).toString();
    }
}
