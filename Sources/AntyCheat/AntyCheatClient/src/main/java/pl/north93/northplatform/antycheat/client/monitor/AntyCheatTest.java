package pl.north93.northplatform.antycheat.client.monitor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.northplatform.antycheat.analysis.impl.AnalysisManager;
import pl.north93.northplatform.antycheat.analysis.reaction.DefaultViolationMapper;
import pl.north93.northplatform.antycheat.analysis.reaction.ITriggerLevel;
import pl.north93.northplatform.antycheat.analysis.reaction.ITriggerListener;
import pl.north93.northplatform.antycheat.analysis.reaction.IViolationMonitor;
import pl.north93.northplatform.antycheat.analysis.reaction.MappedPointsCondition;
import pl.north93.northplatform.antycheat.cheat.movement.MovementViolation;

public class AntyCheatTest
{
    //@Bean
    public AntyCheatTest(final AnalysisManager analysisManager)
    {
        this.onGroundManipulationTest(analysisManager);
        this.survivalFlyTest(analysisManager);
    }

    private void onGroundManipulationTest(final AnalysisManager analysisManager)
    {
        // 4 sekundy bo tyle max można latać
        final IViolationMonitor monitor = analysisManager.createMonitor(MovementViolation.ON_GROUND_INCONSISTENCY, 4 * 20);

        final ITriggerLevel level4 = monitor.addLevel(new MappedPointsCondition(new DefaultViolationMapper(), 4));
        level4.registerListener(this.createListener(4, "on_ground modwarn")); // raport do modów

        final ITriggerLevel level8 = monitor.addLevel(new MappedPointsCondition(new DefaultViolationMapper(), 8));
        level8.registerListener(this.createListener(8, "on_ground ban")); // ban
    }

    private void survivalFlyTest(final AnalysisManager analysisManager)
    {
        // 4 sekundy bo tyle max można latać
        final IViolationMonitor monitor = analysisManager.createMonitor(MovementViolation.SURVIVAL_FLY, 4 * 20);

        final ITriggerLevel level4 = monitor.addLevel(new MappedPointsCondition(new DefaultViolationMapper(), 4));
        level4.registerListener(this.createListener(4, "sv_fly modwarn")); // raport do modów

        final ITriggerLevel level8 = monitor.addLevel(new MappedPointsCondition(new DefaultViolationMapper(), 8));
        level8.registerListener(this.createListener(8, "sv_fly ban")); // ban
    }

    private ITriggerListener createListener(final int points, final String message)
    {
        return new ITriggerListener()
        {
            @Override
            public void onTriggered(final Player player)
            {
                Bukkit.broadcastMessage("Player " + player.getName() + " reached " + points + " points (" + message + ")");
            }

            @Override
            public void onUnTriggered(final Player player)
            {
                Bukkit.broadcastMessage("Player " + player.getName() + " less than " + points + " points (" + message + ")");
            }
        };
    }
}
