package pl.north93.zgame.lobby.windows;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import pl.north93.zgame.api.bukkit.windows.Window;

public class MinigamePicker extends Window
{
    public MinigamePicker()
    {
        super("&aWybierz minigrę", 54);
    }

    @Override
    protected void onShow()
    {
        this.addElement(5, new ItemStack(Material.DIRT, 5), window -> Bukkit.broadcastMessage(window.toString()));
    }
}
