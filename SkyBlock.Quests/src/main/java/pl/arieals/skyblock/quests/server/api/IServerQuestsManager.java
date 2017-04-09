package pl.arieals.skyblock.quests.server.api;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

import pl.arieals.skyblock.quests.shared.api.IQuest;
import pl.arieals.skyblock.quests.shared.api.ITrackedStatistic;

public interface IServerQuestsManager
{
    IQuest getQuest(UUID questId);

    Collection<IQuest> getQuests();

    void resetQuest(IQuest quest, UUID playerId);

    void bumpStatisticIf(UUID player, Predicate<ITrackedStatistic> condition);

    void generateNewQuests();
}
