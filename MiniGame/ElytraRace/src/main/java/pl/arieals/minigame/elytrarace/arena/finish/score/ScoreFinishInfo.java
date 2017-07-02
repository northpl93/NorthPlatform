package pl.arieals.minigame.elytrarace.arena.finish.score;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.statistics.IRecordResult;
import pl.arieals.minigame.elytrarace.arena.finish.FinishInfo;

public class ScoreFinishInfo extends FinishInfo
{
    private final CompletableFuture<IRecordResult> recordResult;

    public ScoreFinishInfo(final UUID uuid, final String displayName, final CompletableFuture<IRecordResult> recordResult)
    {
        super(uuid, displayName);
        this.recordResult = recordResult;
    }

    public CompletableFuture<IRecordResult> getRecordResult()
    {
        return this.recordResult;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("recordResult", this.recordResult).toString();
    }
}