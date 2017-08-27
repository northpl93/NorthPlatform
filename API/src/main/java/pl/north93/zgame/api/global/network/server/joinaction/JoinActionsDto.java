package pl.north93.zgame.api.global.network.server.joinaction;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class JoinActionsDto
{
    private IServerJoinAction[] actions;

    public JoinActionsDto()
    {
    }

    public JoinActionsDto(final IServerJoinAction[] actions)
    {
        this.actions = actions;
    }

    public IServerJoinAction[] getActions()
    {
        return this.actions;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("actions", this.actions).toString();
    }
}
