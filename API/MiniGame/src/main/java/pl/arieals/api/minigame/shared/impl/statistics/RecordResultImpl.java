package pl.arieals.api.minigame.shared.impl.statistics;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IRecordResult;

public class RecordResultImpl implements IRecordResult
{
    private final boolean isReversed;
    private final IRecord processed;
    private final IRecord oldPersonal;
    private final IRecord oldGlobal;

    public RecordResultImpl(final IRecord processed, final IRecord oldPersonal, final IRecord oldGlobal, final boolean isReversed)
    {
        this.isReversed = isReversed;
        this.processed = processed;
        this.oldPersonal = oldPersonal;
        this.oldGlobal = oldGlobal;
    }

    @Override
    public IRecord getProcessedRecord()
    {
        return this.processed;
    }

    @Override
    public boolean isNewPersonalRecord()
    {
        return this.oldPersonal == null || (this.isReversed ? this.processed.value() < this.oldPersonal.value() : this.oldPersonal.value() < this.processed.value());
    }

    @Override
    public IRecord previousPersonal()
    {
        return this.oldPersonal;
    }

    @Override
    public boolean isNewGlobalRecord()
    {
        return this.oldGlobal == null || (this.isReversed ? this.processed.value() < this.oldGlobal.value() : this.oldGlobal.value() < this.processed.value());
    }

    @Override
    public IRecord previousGlobal()
    {
        return this.oldGlobal;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("processed", this.processed).append("oldPersonal", this.oldPersonal).append("oldGlobal", this.oldGlobal).toString();
    }
}
