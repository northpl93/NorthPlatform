package pl.arieals.skyblock.quests.shared.api;

public interface IObjective
{
    /**
     * Returns true if objective has been reached.
     * @param newValue new value of statistic
     * @return does objective has been reached.
     */
    boolean statisticChanged(int newValue);
}
