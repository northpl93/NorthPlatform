package pl.arieals.skyblock.quests.server.api;

import pl.arieals.skyblock.quests.shared.api.IQuestsController;

public interface IServerQuestsComponent
{
    IQuestsController getQuestsController();

    IServerQuestsManager getServerQuestsManager();
}
