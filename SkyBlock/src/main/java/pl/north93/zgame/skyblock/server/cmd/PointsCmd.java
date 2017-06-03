package pl.north93.zgame.skyblock.server.cmd;

import org.bukkit.entity.Player;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.skyblock.shared.api.IslandData;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;


public class PointsCmd extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer server;

    public PointsCmd()
    {
        super("points", "punkty", "pkt", "pkty", "ranking");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label) {
        final String skyPlayerName;
        if (args.length() == 1 && ((Player) sender.unwrapped()).hasPermission("skyblock.points.others")) {
            skyPlayerName = args.asString(0);
            sender.sendMessage("&f&l> &7Pokazuje dane dla gracza: &6" + skyPlayerName);
        } else {
            skyPlayerName = sender.getName();
        }

        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(skyPlayerName)) {
            final SkyPlayer skyPlayer = SkyPlayer.get(t.getPlayer());
            if (!skyPlayer.hasIsland()) {
                sender.sendMessage("&f&l> &7Musisz miec wyspe, aby uzyc tej komendy!");
                return;
            }

            final IslandData data = this.server.getIslandDao().getIsland(skyPlayer.getIslandId());
            final long islandRankingPos = this.server.getIslandsRanking().getPosition(data.getIslandId());

            sender.sendMessage("&f&l> &7Punkty twojej wyspy: &6" + data.getPoints().intValue());
            sender.sendMessage("&f&l> &7Pozycja w rankingu: &6" + (islandRankingPos + 1));
        }
        catch (final PlayerNotFoundException e)
        {
            sender.sendMessage("&f&l> &7Nie znaleziono takiego gracza!");
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}
