package pl.north93.northplatform.minigame.bedwars.cmd;

import static java.text.MessageFormat.format;


import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.minigame.bedwars.shop.gui.UpgradesGui;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.uri.IUriManager;

public class BedWarsUpgrade extends NorthCommand
{
    @Inject
    private IUriManager uriManager;

    public BedWarsUpgrade()
    {
        super("bedwarsupgrade");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        if (args.asString(0).equalsIgnoreCase("gui"))
        {
            final UpgradesGui upgradesGui = new UpgradesGui(player);
            upgradesGui.open(player);
            return;
        }

        final String call = format("/minigame/bedwars/upgrade/{0}/{1}/buy", args.asString(0), player.getUniqueId());
        sender.sendMessage("&aInvoking {0}...", call);
        this.uriManager.call(call);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
