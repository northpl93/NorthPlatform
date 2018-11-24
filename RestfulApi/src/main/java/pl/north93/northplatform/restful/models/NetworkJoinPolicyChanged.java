package pl.north93.northplatform.restful.models;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.network.JoiningPolicy;

public class NetworkJoinPolicyChanged
{
    private final JoiningPolicy oldPolicy;
    private final JoiningPolicy newPolicy;

    public NetworkJoinPolicyChanged(final JoiningPolicy oldPolicy, final JoiningPolicy newPolicy)
    {
        this.oldPolicy = oldPolicy;
        this.newPolicy = newPolicy;
    }

    public JoiningPolicy getOldPolicy()
    {
        return this.oldPolicy;
    }

    public JoiningPolicy getNewPolicy()
    {
        return this.newPolicy;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("oldPolicy", this.oldPolicy).append("newPolicy", this.newPolicy).toString();
    }
}
