package pl.arieals.api.minigame.shared.impl.statistics;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.arieals.api.minigame.shared.api.statistics.IRecord;

public class RecordImpl implements IRecord
{
    private final UUID owner;
    private final long value;
    private final long recordedAt;

    public RecordImpl(final UUID owner, final long value, final long recordedAt)
    {
        this.owner = owner;
        this.value = value;
        this.recordedAt = recordedAt;
    }

    public RecordImpl(final Document document)
    {
        this.owner = document.get("ownerId", UUID.class);
        this.value = document.getLong("value");
        this.recordedAt = document.getLong("recordedAt");
    }

    @Override
    public UUID getOwner()
    {
        return this.owner;
    }

    @Override
    public long value()
    {
        return this.value;
    }

    @Override
    public long recordedAt()
    {
        return this.recordedAt;
    }

    public Document toDocument()
    {
        final Document doc = new Document();
        doc.put("ownerId", this.owner);
        doc.put("value", this.value);
        doc.put("recordedAt", this.recordedAt);
        return doc;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("owner", this.owner).append("value", this.value).append("recordedAt", this.recordedAt).toString();
    }
}
