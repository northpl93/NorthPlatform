package pl.arieals.lobby.gui.bedwars;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.uri.UriHandler;

public class BwGuiHelper
{
    @UriHandler("/lobby/shop/bedwars/openCategory/:categoryName/:playerId")
    public void openBwCategory(final String calledUri, final Map<String, String> parameters)
    {
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));

        final Gui gui;

        switch (parameters.get("categoryName"))
        {
            case "main":
                gui = new BwShopMain(player);
                break;
            case "elimination":
                gui = new BwShopElimination(player);
                break;
            case "shoppers":
                gui = new BwShopShoppers(player);
                break;
            case "perks":
                gui = new BwShopPerks(player);
                break;
            default:
                throw new IllegalArgumentException();
        }

        gui.open(player);
    }
}
