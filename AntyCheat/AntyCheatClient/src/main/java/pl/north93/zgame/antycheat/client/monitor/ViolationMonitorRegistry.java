package pl.north93.zgame.antycheat.client.monitor;

import pl.north93.zgame.antycheat.analysis.impl.AnalysisManager;
import pl.north93.zgame.antycheat.cheat.fight.FightViolation;
import pl.north93.zgame.antycheat.cheat.movement.MovementViolation;
import pl.north93.zgame.antycheat.client.monitor.AlertAdminAction.AlertLevel;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public class ViolationMonitorRegistry
{
    @Bean
    private ViolationMonitorRegistry(final AnalysisManager analysisManager)
    {
        this.onGroundManipulation(analysisManager);
        this.survivalFly(analysisManager);

        this.hitRatio(analysisManager);
        this.hitTarget(analysisManager);
    }

    private void onGroundManipulation(final AnalysisManager analysisManager)
    {
        final WrappedViolationMonitor monitor = new WrappedViolationMonitor(analysisManager.createMonitor(MovementViolation.ON_GROUND_INCONSISTENCY, 4 * 20));

        monitor.addAction(4, new AlertAdminAction(AlertLevel.WARNING, "no-fall (manipulacja on-ground)"));
        // 8-10? ban
    }

    private void survivalFly(final AnalysisManager analysisManager)
    {
        final WrappedViolationMonitor monitor = new WrappedViolationMonitor(analysisManager.createMonitor(MovementViolation.SURVIVAL_FLY, 4 * 20));

        monitor.addAction(4, new AlertAdminAction(AlertLevel.WARNING, "fly/high-jump (niemozliwe ruchy)"));
    }

    private void hitRatio(final AnalysisManager analysisManager)
    {
        final WrappedViolationMonitor monitor = new WrappedViolationMonitor(analysisManager.createMonitor(FightViolation.HIT_RATIO, 15 * 20));

        monitor.addAction(3, new AlertAdminAction(AlertLevel.WARNING, "clicker/aura (podejrzana czestotliwosc uderzen)"));

        // 6 ban - maksymalna ilosc punktów to 18
    }

    private void hitTarget(final AnalysisManager analysisManager)
    {
        final WrappedViolationMonitor monitor = new WrappedViolationMonitor(analysisManager.createMonitor(FightViolation.HIT_TARGET, 5 * 20));

        monitor.addAction(2, new AlertAdminAction(AlertLevel.WARNING, "aura (duzy dystans/bicie przez sciany)"));

        // 5 ban?
    }
}
