package pl.north93.northplatform.lobby.gui.elytrarace;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.global.uri.UriHandler;

public class ElytraGuiHelper
{
    @UriHandler("/lobby/shop/elytra/openCategory/:categoryName/:playerId")
    public void openElytraCategory(final String calledUri, final Map<String, String> parameters)
    {
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));

        final Gui gui;

        switch (parameters.get("categoryName"))
        {
            case "main":
                gui = new ElytraShopMain(player);
                break;
            case "hats":
                gui = new ElytraShopHats(player);
                break;
            case "heads":
                gui = new ElytraShopHeads(player);
                break;
            case "effects":
                gui = new ElytraShopEffects(player);
                break;
            default:
                throw new IllegalArgumentException();
        }

        gui.open(player);
    }
}
