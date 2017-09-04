package pl.north93.zgame.controller.servers.operation;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class OperationCommitter
{
    @Inject
    private Logger logger;
    private final Set<AutoScalerOperation> operations;

    public OperationCommitter(final Set<AutoScalerOperation> operations)
    {
        this.operations = operations;
    }

    public boolean commit(final AutoScalerOperation newOperation)
    {
        final Iterator<AutoScalerOperation> operationIterator = this.operations.iterator();

        while (operationIterator.hasNext())
        {
            final AutoScalerOperation alreadyStartedOperation = operationIterator.next();

            if (newOperation.isOpposite(alreadyStartedOperation) && alreadyStartedOperation.tryCancel())
            {
                this.logger.log(Level.INFO, "Replacing opposite operation {0} by {1}", new Object[]{alreadyStartedOperation, newOperation});

                operationIterator.remove();
                return this.addAndStart(newOperation);
            }
        }

        return this.addAndStart(newOperation);
    }

    private boolean addAndStart(final AutoScalerOperation operation)
    {
        this.logger.log(Level.INFO, "Starting operation {0}", operation);

        try
        {
            operation.start();
        }
        catch (final Exception e)
        {
            this.logger.log(Level.SEVERE, "Failed to start operation", e);
            return false;
        }

        this.operations.add(operation);
        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("operations", this.operations).toString();
    }
}
