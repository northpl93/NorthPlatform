package pl.arieals.skyblock.quests.shared.impl.objectives;

import pl.arieals.skyblock.quests.shared.api.IObjective;

class DoObjective implements IObjective
{
    @Override
    public boolean statisticChanged(final int newValue)
    {
        return newValue >= 1;
    }
}
