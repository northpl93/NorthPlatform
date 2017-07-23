package pl.arieals.minigame.bedwars.cmd;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.bedwars.shop.ShopManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.uri.IUriManager;

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
        final Player player = (Player) sender.unwrapped();

        if (args.length() != 1)
        {
            sender.sendRawMessage("&cJako argument podaj internalName z configu lub gui-zeby uruchomic gui");
            return;
        }

        if (args.asString(0).equalsIgnoreCase("gui"))
        {
            this.uriManager.call("/minigame/bedwars/shopCategory/main/" + player.getUniqueId());
            return;
        }

        final boolean result = this.manager.buy(player, args.asString(0));
        sender.sendRawMessage("&aWynik metody buy:" + result);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
