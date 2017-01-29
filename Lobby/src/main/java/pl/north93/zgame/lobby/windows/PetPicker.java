package pl.north93.zgame.lobby.windows;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.north93.pets.IPet;
import pl.north93.pets.exceptions.PetNotFoundException;
import pl.north93.pets.system.support.HeadCreator;
import pl.north93.zgame.api.bukkit.windows.ClickInfo;
import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.impl.Injector;
import pl.north93.zgame.lobby.LobbyFeatures;
import pl.north93.zgame.lobby.PlayerPetsManager;
import pl.north93.zgame.lobby.config.LobbyConfig;
import pl.north93.zgame.lobby.config.LobbyConfig.PetConfig;

public class PetPicker extends Window
{
    @InjectComponent("Lobby.Features")
    private LobbyFeatures component;

    public PetPicker()
    {
        super("&aWybierz zwierzaka", 54);
        Injector.inject(API.getApiCore().getComponentManager(), this);
    }

    @Override
    protected void onShow()
    {
        final LobbyConfig lobbyConfig = this.component.getLobbyConfig();
        final Iterator<PetConfig> pets = lobbyConfig.pets.iterator();

        for (int y = 1; y < 5; y++)
        {
            for (int x = 1; x < 8; x++)
            {
                if (! pets.hasNext())
                {
                    return;
                }

                final int slot = 9 * y + x;
                final PetConfig petConfig = pets.next();

                final ItemStack head = HeadCreator.createHead(UUID.randomUUID(), petConfig.headOwnerData);
                head.setAmount(1);
                final ItemMeta itemMeta = head.getItemMeta();
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', petConfig.displayName));
                itemMeta.setLore(petConfig.description);
                head.setItemMeta(itemMeta);

                this.addElement(slot, head, (event) -> this.switchPet(event, petConfig));
            }
        }
    }

    private void switchPet(final ClickInfo event, final PetConfig petConfig)
    {
        final Player player = event.getWindow().getPlayer();
        final PlayerPetsManager playersPets = this.component.getPlayerPetsManager();

        if (playersPets.hasPet(player))
        {
            final IPet pet = playersPets.getPlayersPet(player);
            pet.setOwner(null);
            pet.kill();
        }

        final IPet newPet;
        try
        {
             newPet = pl.north93.pets.Main.getInstance().getPetManager().spawnPet(petConfig.systemName, player.getLocation());
        }
        catch (final PetNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        newPet.setOwner(player);
        playersPets.setPlayersPet(player, newPet);
        player.closeInventory();
    }
}
