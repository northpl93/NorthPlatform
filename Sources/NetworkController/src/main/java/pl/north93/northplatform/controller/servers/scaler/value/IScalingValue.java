package pl.north93.northplatform.controller.servers.scaler.value;

import pl.north93.northplatform.controller.servers.groups.LocalManagedServersGroup;

/**
 * Represents a value that can be used to configure servers scaling.
 */
public interface IScalingValue
{
    /**
     * @return unique identifier of this value.
     */
    String getId();

    /**
     * Calculates value for specified servers group.
     * Value is cached during one decision-making cycle.
     *
     * @param managedServersGroup Servers group for which we will calculate the value.
     * @return The calculated value.
     */
    double calculate(LocalManagedServersGroup managedServersGroup);
}
