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

    // sprawdza czy mozna anulowac jakas przeciwna operacje do obecnie commitowanej.
    public boolean commit(final AutoScalerOperation newOperation)
    {
        final Iterator<AutoScalerOperation> operationIterator = this.operations.iterator();

        while (operationIterator.hasNext())
        {
            final AutoScalerOperation alreadyStartedOperation = operationIterator.next();

            // jak operacje jest przeciwna i udalo sie ja anulowac to usuwamy ja z listy operacji
            // i zwracamy true (chociaz tak na prawde commit nowej operacji nie nastapil)
            if (newOperation.isOpposite(alreadyStartedOperation) && alreadyStartedOperation.tryCancel())
            {
                this.logger.log(Level.INFO, "Instead committing {0} I cancelled {1}", new Object[]{newOperation,alreadyStartedOperation});
                operationIterator.remove();
                return true;
            }
        }

        return this.addAndStart(newOperation);
    }

    // probuje rozpoczac operacje i jak sie uda to dodaje do listy operacji
    private boolean addAndStart(final AutoScalerOperation operation)
    {
        this.logger.log(Level.INFO, "Starting operation {0}", operation);
        operation.start();

        // nie udalo sie uruchomic operacji wiec jej nawet nie dodajemy do listy
        if (operation.getCachedState() == ScalerOperationState.FAILED)
        {
            this.logger.log(Level.WARNING, "Failed to start operation {0}", operation);
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
