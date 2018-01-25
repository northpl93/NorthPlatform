package pl.north93.zgame.antycheat.utils;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;

public final class AntyCheatTimings
{
    public static final Timing SUMMARY;
    public static final Timing PRE_TICK;
    public static final Timing POST_TICK;

    static
    {
        final BukkitApiCore bukkitApiCore = (BukkitApiCore) API.getApiCore();
        SUMMARY = Timings.of(bukkitApiCore.getPluginMain(), "Anti Cheat - Summary");
        PRE_TICK = Timings.of(bukkitApiCore.getPluginMain(), "Anti Cheat - Pre tick", SUMMARY);
        POST_TICK = Timings.of(bukkitApiCore.getPluginMain(), "Anti Cheat - Post tick", SUMMARY);
    }
}
