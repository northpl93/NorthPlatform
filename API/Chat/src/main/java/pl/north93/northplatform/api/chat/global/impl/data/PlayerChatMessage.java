package pl.north93.northplatform.api.chat.global.impl.data;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.network.players.Identity;

public class PlayerChatMessage extends AbstractChatData
{
    private Identity sourcePlayer;
    private UUID     sourceServer;

    public PlayerChatMessage()
    {
    }

    public PlayerChatMessage(final String roomId, final String message, final Identity sourcePlayer, final UUID sourceServer)
    {
        super(roomId, message);
        this.sourcePlayer = sourcePlayer;
        this.sourceServer = sourceServer;
    }

    public Identity getSourcePlayer()
    {
        return this.sourcePlayer;
    }

    public UUID getSourceServer()
    {
        return this.sourceServer;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("sourcePlayer", this.sourcePlayer).append("sourceServer", this.sourceServer).toString();
    }
}
