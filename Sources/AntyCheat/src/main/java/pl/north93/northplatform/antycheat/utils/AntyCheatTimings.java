package pl.north93.northplatform.antycheat.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;

public final class AntyCheatTimings
{
    private static final JavaPlugin PLUGIN = (JavaPlugin) Bukkit.getPluginManager().getPlugin("API"); // todo

    public static final Timing SUMMARY;
    public static final Timing PRE_TICK;
    public static final Timing POST_TICK;

    public static final Timing ANALYSER_EVENT;
    public static final Timing ANALYSER_TIMELINE;

    static
    {
        SUMMARY = Timings.of(PLUGIN, "Anti Cheat - Summary");
        PRE_TICK = Timings.of(PLUGIN, "Anti Cheat - Pre tick", SUMMARY);
        POST_TICK = Timings.of(PLUGIN, "Anti Cheat - Post tick", SUMMARY);

        ANALYSER_EVENT = Timings.of(PLUGIN, "Anti Cheat - Event analysers", POST_TICK);
        ANALYSER_TIMELINE = Timings.of(PLUGIN, "Anti Cheat - Timeline analysers", POST_TICK);
    }

    public static Timing eventAnalyserTiming(final String name)
    {
        return Timings.ofStart(PLUGIN, name, ANALYSER_EVENT);
    }

    public static Timing timelineAnalyserTiming(final String name)
    {
        return Timings.ofStart(PLUGIN, name, ANALYSER_TIMELINE);
    }
}
