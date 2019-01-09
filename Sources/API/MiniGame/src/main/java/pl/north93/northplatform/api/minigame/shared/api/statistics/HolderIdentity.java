package pl.north93.northplatform.api.minigame.shared.api.statistics;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

/**
 * Umo
 */
public final class HolderIdentity
{
    private final String type;
    private final UUID   uuid;

    public HolderIdentity(final String type, final UUID uuid)
    {
        this.type = type;
        this.uuid = uuid;
    }

    public String getType()
    {
        return this.type;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public Document asBson()
    {
        final Document document = new Document("type", this.type);
        document.put("uuid", this.uuid);

        return document;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).append("uuid", this.uuid).toString();
    }
}
