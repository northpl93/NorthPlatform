package pl.north93.zgame.api.global.network.server.joinaction;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class JoinActionsContainer
{
    private IServerJoinAction[] serverJoinActions;

    public JoinActionsContainer()
    {
    }

    public JoinActionsContainer(final IServerJoinAction[] serverJoinActions)
    {
        this.serverJoinActions = serverJoinActions;
    }

    public IServerJoinAction[] getServerJoinActions()
    {
        return this.serverJoinActions;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverJoinActions", this.serverJoinActions).toString();
    }
}
