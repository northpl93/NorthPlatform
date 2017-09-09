package pl.north93.zgame.controller.servers.scaler;

import pl.north93.zgame.api.global.network.daemon.config.rules.RuleEntryConfig;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;
import pl.north93.zgame.controller.servers.operation.DeployServerOperation;
import pl.north93.zgame.controller.servers.operation.RemoveServerOperation;

public enum ScalerDecision
{
    ADD_SERVER
            {
                @Override
                public boolean apply(final LocalManagedServersGroup serversGroup)
                {
                    return serversGroup.commitOperation(new DeployServerOperation(serversGroup));
                }
            },
    REMOVE_SERVER
            {
                @Override
                public boolean apply(final LocalManagedServersGroup serversGroup)
                {
                    return serversGroup.commitOperation(new RemoveServerOperation(serversGroup));
                }
            },
    DO_NOTHING
            {
                @Override
                public boolean apply(final LocalManagedServersGroup serversGroup)
                {
                    return true;
                }
            };

    public abstract boolean apply(LocalManagedServersGroup serversGroup);

    public static ScalerDecision fromConfig(final RuleEntryConfig.Action action)
    {
        switch (action)
        {
            case CREATE_SERVER:
                return ADD_SERVER;
            case REMOVE_SERVER:
                return REMOVE_SERVER;
            case NOTHING:
                return DO_NOTHING;
            default:
                throw new IllegalArgumentException(action.name());
        }
    }
}
