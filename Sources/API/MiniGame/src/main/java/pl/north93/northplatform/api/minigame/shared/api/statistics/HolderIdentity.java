package pl.north93.northplatform.api.minigame.shared.api.statistics;

import java.util.UUID;

import org.bson.Document;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Umo
 */
@ToString
@AllArgsConstructor
public final class HolderIdentity
{
    private final String type;
    private final UUID uuid;

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
}
