package pl.north93.zgame.antycheat.cheat.movement.check;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.cheat.movement.JumpController;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.event.impl.VelocityAppliedTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;

/**
 * Check pomocniczy który służy tylko do informowania innych checków o zmianie velocity.
 */
public class VelocityChangeCheck implements EventAnalyser<VelocityAppliedTimelineEvent>
{
    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.whitelistEvent(VelocityAppliedTimelineEvent.class);
        config.fireBefore(ClientMoveTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final VelocityAppliedTimelineEvent event)
    {
        if (MovementManipulationChecker.shouldSkip(data, tickInfo))
        {
            return null;
        }

        final JumpController jumpController = JumpController.get(data);
        jumpController.changeVelocity(event);
        return null;
    }
}
