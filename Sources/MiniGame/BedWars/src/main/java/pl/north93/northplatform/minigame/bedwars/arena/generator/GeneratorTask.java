package pl.north93.northplatform.minigame.bedwars.arena.generator;

import org.bukkit.scheduler.BukkitRunnable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsArena;

public final class GeneratorTask extends BukkitRunnable
{
    private final LocalArena arena;

    public GeneratorTask(final LocalArena arena)
    {
        this.arena = arena;
    }

    @Override
    public void run()
    {
        final BedWarsArena arenaData = this.arena.getArenaData();
        if (this.arena.getGamePhase() != GamePhase.STARTED || arenaData == null)
        {
            this.cancel(); // arena nie jest teraz uruchomiona wiec nic nie spawnimy i konczymy taska
            return;
        }
        arenaData.getGenerators().forEach(GeneratorController::tick);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arena", this.arena).toString();
    }
}
