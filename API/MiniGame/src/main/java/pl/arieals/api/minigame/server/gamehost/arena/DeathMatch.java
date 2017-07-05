package pl.arieals.api.minigame.server.gamehost.arena;

import static com.google.common.base.Preconditions.checkState;


import java.io.File;
import java.util.logging.Logger;

import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.DeathMatchPrepareEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.MapSwitchedEvent;
import pl.arieals.api.minigame.server.gamehost.world.ILoadingProgress;
import pl.arieals.api.minigame.server.gamehost.world.IWorldManager;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.MapTemplate;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;
import pl.arieals.api.minigame.shared.api.cfg.DeathMatchConfig;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DeathMatch
{
    private final GameHostManager manager;
    private final LocalArena      arena;
    private DeathMatchState state;
    @Inject
    private BukkitApiCore   apiCore;
    @Inject
    private Logger          logger;

    public DeathMatch(final GameHostManager manager, final LocalArena arena)
    {
        this.manager = manager;
        this.arena = arena;
        this.state = DeathMatchState.NOT_STARTED;
    }

    public DeathMatchState getState()
    {
        return this.state;
    }

    public String getDeathMathWorldName()
    {
        return this.arena.getWorld().getName() + "_deathmatch";
    }

    public DeathMatchConfig getConfig()
    {
        return this.manager.getMiniGameConfig().getDeathMatch();
    }

    public void activateDeathMatch()
    {
        checkState(this.getConfig().getEnabled(), "Death match is disabled in this minigame!");
        checkState(this.state == DeathMatchState.NOT_STARTED, "Death match already started!");
        checkState(this.arena.getGamePhase() == GamePhase.STARTED, "Arena must be in STARTED gamephase");

        this.state = DeathMatchState.LOADING;
        final MapTemplate template = this.manager.getMapTemplateManager().getMapTemplate(this.getConfig().getTemplateName());

        final ArenaWorld arenaWorld = this.arena.getWorld();
        final IWorldManager worldManager = this.manager.getWorldManager();

        this.logger.info("Switching arena " + this.arena.getId() + " to death match mode!");

        final File templateDir = template.getMapDirectory();
        final ILoadingProgress progress = worldManager.loadWorld(this.getDeathMathWorldName(), templateDir, template.getMapConfig().getChunks());

        progress.onComplete(() ->
        {
            if (this.arena.getGamePhase() != GamePhase.STARTED || this.state != DeathMatchState.LOADING)
            {
                this.logger.info("Death match arena loaded, but arena is in gamephase " + this.arena.getGamePhase() + "! Unloading that world...");
                worldManager.clearWorld(this.getDeathMathWorldName());
                return;
            }

            this.apiCore.callEvent(new MapSwitchedEvent(this.arena, MapSwitchedEvent.MapSwitchReason.DEATH_MATCH));

            final World oldWorld = arenaWorld.getCurrentWorld();

            this.logger.info("Death match arena loaded successfully");
            this.state = DeathMatchState.STARTED;

            arenaWorld.switchMap(template, progress.getWorld(), progress);
            this.apiCore.callEvent(new DeathMatchPrepareEvent(this.arena, oldWorld, progress.getWorld()));
            if (! worldManager.clearWorld(arenaWorld.getName()))
            {
                this.logger.severe("Failed to remove regular world of arena " + this.arena.getId());
            }
        });
    }

    public void resetState()
    {
        if (this.state == DeathMatchState.LOADING)
        {
            this.logger.info("Reset state called while death match arena is loading. Loading will be canceled later.");
        }
        else if (this.state == DeathMatchState.STARTED)
        {
            this.logger.info("Removing death match world for arena " + this.arena.getId());
            if (! this.manager.getWorldManager().clearWorld(this.getDeathMathWorldName()))
            {
                this.logger.severe("Failed to remove death match world of arena " + this.arena.getId());
            }
        }
        this.state = DeathMatchState.NOT_STARTED;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("state", this.state).toString();
    }
}
