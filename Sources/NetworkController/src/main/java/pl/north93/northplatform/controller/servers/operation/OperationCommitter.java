package pl.north93.northplatform.controller.servers.operation;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperationCommitter
{
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
                log.info("Instead committing {} I cancelled {}", newOperation,alreadyStartedOperation);
                operationIterator.remove();
                return true;
            }
        }

        return this.addAndStart(newOperation);
    }

    // probuje rozpoczac operacje i jak sie uda to dodaje do listy operacji
    private boolean addAndStart(final AutoScalerOperation operation)
    {
        log.info("Starting operation {}", operation);
        operation.start();

        // nie udalo sie uruchomic operacji wiec jej nawet nie dodajemy do listy
        if (operation.getCachedState() == ScalerOperationState.FAILED)
        {
            log.warn("Failed to start operation {}", operation);
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
