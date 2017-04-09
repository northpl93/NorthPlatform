package pl.arieals.skyblock.quests.shared.api;

import java.util.List;

public interface IQuestsController
{
    List<IQuest> getQuests();

    void generateNewQuests();
}
