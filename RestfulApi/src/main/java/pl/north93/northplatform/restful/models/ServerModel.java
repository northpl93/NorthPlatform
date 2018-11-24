package pl.north93.northplatform.restful.models;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ServerModel
{
    private UUID    uuid;
    private String  serverType;
    private boolean launchedViaDaemon;
    private String  serverState;
    private String  joiningPolicy;
    private String  serversGroup;

    public ServerModel(final UUID uuid, final String serverType, final boolean launchedViaDaemon, final String serverState, final String joiningPolicy, final String serversGroup)
    {
        this.uuid = uuid;
        this.serverType = serverType;
        this.launchedViaDaemon = launchedViaDaemon;
        this.serverState = serverState;
        this.joiningPolicy = joiningPolicy;
        this.serversGroup = serversGroup;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("serverType", this.serverType).append("launchedViaDaemon", this.launchedViaDaemon).append("serverState", this.serverState).append("joiningPolicy", this.joiningPolicy).append("serversGroup", this.serversGroup).toString();
    }
}
