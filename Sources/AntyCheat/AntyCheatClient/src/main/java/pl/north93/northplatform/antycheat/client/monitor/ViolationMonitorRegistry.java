package pl.north93.northplatform.antycheat.client.monitor;

import pl.north93.northplatform.antycheat.analysis.impl.AnalysisManager;
import pl.north93.northplatform.antycheat.cheat.fight.FightViolation;
import pl.north93.northplatform.antycheat.cheat.movement.MovementViolation;
import pl.north93.northplatform.antycheat.client.monitor.action.AlertAdminAction;
import pl.north93.northplatform.antycheat.client.monitor.action.AlertAdminAction.AlertLevel;
import pl.north93.northplatform.antycheat.client.monitor.action.BanPlayerAction;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

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

        monitor.addAction(5, new AlertAdminAction(AlertLevel.WARNING, "no-fall"));
        monitor.addAction(10, new AlertAdminAction(AlertLevel.ALERT, "no-fall"));
        monitor.addAction(20, new BanPlayerAction());
    }

    private void survivalFly(final AnalysisManager analysisManager)
    {
        final WrappedViolationMonitor monitor = new WrappedViolationMonitor(analysisManager.createMonitor(MovementViolation.SURVIVAL_FLY, 4 * 20));

        monitor.addAction(5, new AlertAdminAction(AlertLevel.WARNING, "fly/high-jump"));
        monitor.addAction(10, new AlertAdminAction(AlertLevel.ALERT, "fly/high-jump"));
        monitor.addAction(20, new BanPlayerAction());
    }

    private void hitRatio(final AnalysisManager analysisManager)
    {
        final WrappedViolationMonitor monitor = new WrappedViolationMonitor(analysisManager.createMonitor(FightViolation.HIT_RATIO, 10 * 20)); // todo AttackFrequencyCheck testujemy 5->1, przywrocic 15

        monitor.addAction(3, new AlertAdminAction(AlertLevel.WARNING, "clicker/aura"));
        monitor.addAction(6, new AlertAdminAction(AlertLevel.ALERT, "clicker/aura"));
        monitor.addAction(10, new BanPlayerAction());
    }

    private void hitTarget(final AnalysisManager analysisManager)
    {
        final WrappedViolationMonitor monitor = new WrappedViolationMonitor(analysisManager.createMonitor(FightViolation.HIT_TARGET, 5 * 20));

        monitor.addAction(3, new AlertAdminAction(AlertLevel.WARNING, "aura"));
        monitor.addAction(6, new AlertAdminAction(AlertLevel.ALERT, "aura"));
        monitor.addAction(10, new BanPlayerAction());
    }
}
