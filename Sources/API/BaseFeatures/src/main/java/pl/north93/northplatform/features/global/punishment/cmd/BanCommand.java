package pl.north93.northplatform.features.global.punishment.cmd;

import java.util.Optional;
import java.util.UUID;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.players.PlayerNotFoundException;
import pl.north93.northplatform.features.global.punishment.AbstractBan;
import pl.north93.northplatform.features.global.punishment.BanService;
import pl.north93.northplatform.features.global.punishment.cfg.PredefinedBanCfg;

public class BanCommand extends NorthCommand
{
    @Inject
    private BanService banService;
    @Inject
    private IPlayersManager playersManager;

    public BanCommand()
    {
        super("ban");
        this.setPermission("basefeatures.cmd.ban");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 1)
        {
            final AbstractBan ban = this.banService.getBan(Identity.create(null, args.asString(0)));
            if (ban == null)
            {
                sender.sendMessage("&cGracz o nicku {0} nie ma bana", args.asString(0));
            }
            else
            {
                final String adminNick = Optional.ofNullable(ban.getAdminId()).flatMap(this.playersManager::getNickFromUuid).orElse("<SERWER>");
                sender.sendMessage("&cZbanowany {0} przez {1}", ban.getGivenAt(), adminNick);
            }
        }
        else if (args.length() == 2)
        {
            final PredefinedBanCfg config = this.getBanCfg(args, 1);
            if (config == null)
            {
                sender.sendMessage("Niepoprawna nazwa bana {0}", args.asString(1));
                return;
            }

            final UUID adminId = this.getAdminId(sender);
            try
            {
                this.banService.createBan(Identity.create(null, args.asString(0)), adminId, config);
                sender.sendMessage("&cUzytkownik zbanowany");

            }
            catch (final PlayerNotFoundException e)
            {
                sender.sendMessage("&cNie ma takiego gracza");
            }
        }
        else
        {
            sender.sendMessage("&c/ban nick powód");
        }
    }

    private PredefinedBanCfg getBanCfg(final Arguments args, final int argNum)
    {
        final Integer banNumber = args.asInt(argNum);
        if (banNumber == null)
        {
            return this.banService.getConfigByName(args.asString(argNum));
        }

        return this.banService.getConfigById(banNumber);
    }

    private UUID getAdminId(final NorthCommandSender sender)
    {
        if (! sender.isPlayer())
        {
            return null;
        }

        return this.playersManager.getUuidFromNick(sender.getName()).orElse(null);
    }
}
