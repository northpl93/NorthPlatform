package pl.north93.northplatform.datashare.api.chat;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class ChatMessage
{
    private UUID   sourceServerId;
    private UUID   playerId;
    private String message;

    public ChatMessage() // serialization
    {
    }

    public ChatMessage(final UUID sourceServerId, final UUID playerId, final String message)
    {
        this.sourceServerId = sourceServerId;
        this.playerId = playerId;
        this.message = message;
    }

    public UUID getSourceServerId()
    {
        return this.sourceServerId;
    }

    public UUID getPlayerId()
    {
        return this.playerId;
    }

    public String getMessage()
    {
        return this.message;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("sourceServerId", this.sourceServerId).append("playerId", this.playerId).append("message", this.message).toString();
    }
}
