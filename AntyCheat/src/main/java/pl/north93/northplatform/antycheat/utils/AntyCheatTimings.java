package pl.north93.northplatform.antycheat.utils;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.global.API;

public final class AntyCheatTimings
{
    private static final BukkitApiCore bukkitApiCore = (BukkitApiCore) API.getApiCore();

    public static final Timing SUMMARY;
    public static final Timing PRE_TICK;
    public static final Timing POST_TICK;

    public static final Timing ANALYSER_EVENT;
    public static final Timing ANALYSER_TIMELINE;

    static
    {
        SUMMARY = Timings.of(bukkitApiCore.getPluginMain(), "Anti Cheat - Summary");
        PRE_TICK = Timings.of(bukkitApiCore.getPluginMain(), "Anti Cheat - Pre tick", SUMMARY);
        POST_TICK = Timings.of(bukkitApiCore.getPluginMain(), "Anti Cheat - Post tick", SUMMARY);

        ANALYSER_EVENT = Timings.of(bukkitApiCore.getPluginMain(), "Anti Cheat - Event analysers", POST_TICK);
        ANALYSER_TIMELINE = Timings.of(bukkitApiCore.getPluginMain(), "Anti Cheat - Timeline analysers", POST_TICK);
    }

    public static Timing eventAnalyserTiming(final String name)
    {
        return Timings.ofStart(bukkitApiCore.getPluginMain(), name, ANALYSER_EVENT);
    }

    public static Timing timelineAnalyserTiming(final String name)
    {
        return Timings.ofStart(bukkitApiCore.getPluginMain(), name, ANALYSER_TIMELINE);
    }
}
