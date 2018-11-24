package pl.north93.northplatform.antycheat.cheat.movement.check;

import pl.north93.northplatform.antycheat.analysis.event.EventAnalyser;
import pl.north93.northplatform.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.northplatform.antycheat.event.impl.VelocityAppliedTimelineEvent;
import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.antycheat.cheat.movement.JumpController;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.timeline.PlayerTickInfo;

/**
 * Check pomocniczy który służy tylko do informowania innych checków o zmianie velocity.
 */
public class VelocityChangeCheck implements EventAnalyser<VelocityAppliedTimelineEvent>
{
    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.order(-10);
        config.whitelistEvent(VelocityAppliedTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final VelocityAppliedTimelineEvent event)
    {
        if (MovementManipulationChecker.shouldSkip(data))
        {
            return null;
        }

        final JumpController jumpController = JumpController.get(data);
        jumpController.changeVelocity(event);
        return null;
    }
}
