package pl.north93.zgame.api.global.network.server.joinaction;

import java.util.UUID;

import org.diorite.commons.arrays.DioriteArrayUtils;

import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.serializer.platform.NorthSerializer;

@ToString
@NoArgsConstructor
public final class JoinActionsContainer
{
    @Inject
    private static NorthSerializer<byte[]> templateManager;
    private UUID   serverId;
    private byte[] serverJoinActions;

    public JoinActionsContainer(final UUID serverId, final IServerJoinAction[] serverJoinActions)
    {
        this.serverId = serverId;
        if (serverJoinActions == null || serverJoinActions.length == 0)
        {
            this.serverJoinActions = DioriteArrayUtils.EMPTY_BYTES;
        }
        else
        {
            final JoinActionsDto joinActionsDto = new JoinActionsDto(serverJoinActions);
            this.serverJoinActions = templateManager.serialize(JoinActionsDto.class, joinActionsDto);
        }
    }

    /**
     * @return UUID of server for which this container is intended.
     */
    public UUID getServerId()
    {
        return this.serverId;
    }

    /**
     * Returns true if uuid specified in argument doesn't match the one in this object.
     *
     * @param serverId UUID of server which is trying to handle this container.
     * @return True if server shouldn't handle this container.
     */
    public boolean isInvalidServer(final UUID serverId)
    {
        return ! this.serverId.equals(serverId);
    }

    public IServerJoinAction[] getServerJoinActions()
    {
        final JoinActionsDto actionsDto = templateManager.deserialize(JoinActionsDto.class, this.serverJoinActions);
        return actionsDto.getActions();
    }

    public boolean isEmpty()
    {
        return this.serverJoinActions.length == 0;
    }
}