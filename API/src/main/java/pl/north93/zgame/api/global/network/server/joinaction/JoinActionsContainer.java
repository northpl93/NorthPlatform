package pl.north93.zgame.api.global.network.server.joinaction;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public final class JoinActionsContainer
{
    @Inject
    private static TemplateManager templateManager;
    private byte[] serverJoinActions; // reading from byte[] is optimised

    public JoinActionsContainer()
    {
    }

    public JoinActionsContainer(final IServerJoinAction[] serverJoinActions)
    {
        final JoinActionsDto joinActionsDto = new JoinActionsDto(serverJoinActions);
        this.serverJoinActions = templateManager.serialize(JoinActionsDto.class, joinActionsDto);
    }

    public IServerJoinAction[] getServerJoinActions()
    {
        final JoinActionsDto actionsDto = templateManager.deserialize(JoinActionsDto.class, this.serverJoinActions);
        return actionsDto.getActions();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverJoinActions", this.serverJoinActions).toString();
    }
}