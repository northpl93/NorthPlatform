package pl.arieals.skyblock.quests.shared.impl.objectives;

import pl.arieals.skyblock.quests.shared.api.IObjective;

public final class ObjectivesFactory
{
    private static final ObjectivesFactory INSTANCE = new ObjectivesFactory();

    /**
     * Creates new objective which is passed when player
     * does the action which is tracked by statistic at least once.
     *
     * @return DO objective.
     */
    public IObjective doObjective()
    {
        return new DoObjective();
    }
}
