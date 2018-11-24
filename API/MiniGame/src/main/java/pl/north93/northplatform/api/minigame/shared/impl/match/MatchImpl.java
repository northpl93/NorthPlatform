package pl.north93.northplatform.api.minigame.shared.impl.match;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.match.IMatchAccess;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.global.network.players.Identity;

/*default*/ class MatchImpl implements IMatchAccess
{
    private final MatchManagerImpl matchManager;
    private final IStatisticHolder statisticHolder;
    private final MatchData        data;

    public MatchImpl(final MatchManagerImpl matchManager, final IStatisticHolder statisticHolder, final MatchData data)
    {
        this.matchManager = matchManager;
        this.statisticHolder = statisticHolder;
        this.data = data;
    }

    @Override
    public void endMatch()
    {
        final Instant endTime = Instant.now();
        this.data.setEndedAt(endTime);
        this.matchManager.save(this.data);
    }

    @Override
    public UUID getMatchId()
    {
        return this.data.getMatchId();
    }

    @Override
    public UUID getArenaId()
    {
        return this.data.getArenaId();
    }

    @Override
    public UUID getServerId()
    {
        return this.data.getServerId();
    }

    @Override
    public GameIdentity getGameIdentity()
    {
        return this.data.getGame();
    }

    @Override
    public Instant getStartedAt()
    {
        return this.data.getStartTime();
    }

    @Override
    public String getMapId()
    {
        return this.data.getMapId();
    }

    @Override
    public Collection<Identity> getStartParticipants()
    {
        return this.data.getStartParticipants();
    }

    @Override
    public boolean isEnded()
    {
        return this.data.getEndedAt() != null;
    }

    @Override
    public Instant getEndedAt()
    {
        return this.data.getEndedAt();
    }

    @Override
    public IStatisticHolder getStatistics()
    {
        return this.statisticHolder;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("statisticHolder", this.statisticHolder).append("data", this.data).toString();
    }
}
