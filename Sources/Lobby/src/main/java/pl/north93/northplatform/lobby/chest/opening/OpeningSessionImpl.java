package pl.north93.northplatform.lobby.chest.opening;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.lobby.chest.loot.LootResult;

class OpeningSessionImpl implements IOpeningSession
{
    private final INorthPlayer player;
    private final HubWorld hub;
    private final HubOpeningConfig openingConfig;
    private int chests;
    private CompletableFuture<LootResult> lastResults;

    public OpeningSessionImpl(final INorthPlayer player, final HubWorld hub, final HubOpeningConfig openingConfig)
    {
        this.player = player;
        this.hub = hub;
        this.openingConfig = openingConfig;
    }

    @Override
    public INorthPlayer getPlayer()
    {
        return this.player;
    }

    @Override
    public HubWorld getHub()
    {
        return this.hub;
    }

    @Override
    public int getChestsAmount()
    {
        return this.chests;
    }

    public void setChests(final int chests)
    {
        this.chests = chests;
    }

    @Override
    public Location getPlayerLocation()
    {
        return this.openingConfig.getPlayerLocation().toBukkit(this.hub.getBukkitWorld());
    }

    @Override
    public HubOpeningConfig getConfig()
    {
        return this.openingConfig;
    }

    public CompletableFuture<LootResult> getLastResults()
    {
        return this.lastResults;
    }

    public void setLastResults(final CompletableFuture<LootResult> lastResults)
    {
        this.lastResults = lastResults;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("hub", this.hub).append("openingConfig", this.openingConfig).append("lastResults", this.lastResults).toString();
    }
}
