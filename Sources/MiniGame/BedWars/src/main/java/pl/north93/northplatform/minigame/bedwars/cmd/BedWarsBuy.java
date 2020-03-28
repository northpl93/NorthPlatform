package pl.north93.northplatform.minigame.bedwars.cmd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.minigame.bedwars.shop.ShopManager;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.uri.IUriManager;

public class BedWarsBuy extends NorthCommand
{
    @Inject
    private ShopManager manager;
    @Inject
    private IUriManager uriManager;


    public BedWarsBuy()
    {
        super("bedwarsbuy");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final INorthPlayer player = INorthPlayer.wrap(sender);

        if (args.length() != 1)
        {
            sender.sendMessage("&cJako argument podaj internalName z configu lub gui-zeby uruchomic gui");
            return;
        }

        if (args.asString(0).equalsIgnoreCase("gui"))
        {
            this.uriManager.call("/minigame/bedwars/shopCategory/main/" + player.getUniqueId());
            return;
        }

        final boolean result = this.manager.buy(player, args.asString(0));
        sender.sendMessage("&aWynik metody buy:" + result);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
