package pl.north93.northplatform.api.bukkit.gui.event;

import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;

import pl.north93.northplatform.api.bukkit.gui.Gui;

public class GuiOpenEvent extends InventoryOpenEvent
{
    private final Gui gui;
    
    public GuiOpenEvent(InventoryView transaction, Gui gui)
    {
        super(transaction);
        this.gui = gui;
    }
    
    public Gui getGui()
    {
        return gui;
    }
}
