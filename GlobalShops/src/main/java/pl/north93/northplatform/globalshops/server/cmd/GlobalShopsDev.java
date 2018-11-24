package pl.north93.northplatform.globalshops.server.cmd;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.globalshops.server.IGlobalShops;
import pl.north93.northplatform.globalshops.server.IPlayerContainer;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.globalshops.server.domain.ItemsGroup;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class GlobalShopsDev extends NorthCommand
{
    @Inject
    private IGlobalShops globalShops;

    public GlobalShopsDev()
    {
        super("globalshopsdev");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 0)
        {
            sender.sendMessage("&cUsage: buy <itemId>/myitems <groupId>/item <id>/group <id>/activate <itemId>");
            sender.sendMessage("&ceg /globalshopsdev myitems testGroup");
        }
        else if (args.length() == 2)
        {
            final String arg0 = args.asString(0);
            /*if (arg0.equalsIgnoreCase("item"))
            {
                final Item item = this.globalShops.getItem(args.asString(1));
                if (item == null)
                {
                    sender.sendMessage("&cno item");
                    return;
                }

                sender.sendMessage("&cItem id: " + item.getId());
                sender.sendMessage("&cItem group: " + item.getGroup());
            }
            else */if (arg0.equalsIgnoreCase("group"))
            {
                final ItemsGroup group = this.globalShops.getGroup(args.asString(1));
                if (group == null)
                {
                    sender.sendMessage("&cno group");
                    return;
                }

                sender.sendMessage("&cGroup id: " + group.getId());
                sender.sendMessage("&cGroup type: " + group.getGroupType());
                sender.sendMessage("&cItems: " + group.getItems().stream().map(Item::getId).collect(Collectors.joining(",")));
            }
            else if (arg0.equalsIgnoreCase("myitems"))
            {
                final ItemsGroup group = this.globalShops.getGroup(args.asString(1));
                if (group == null)
                {
                    sender.sendMessage("&cno group");
                    return;
                }

                final Player player = (Player) sender.unwrapped();
                final IPlayerContainer container = this.globalShops.getPlayer(player);

                final Collection<Item> boughtItems = container.getBoughtItems(group);
                sender.sendMessage(boughtItems.stream().map(Item::getId).collect(Collectors.joining(",")));
            }
            /*else if (arg0.equalsIgnoreCase("buy"))
            {
                final Item item = this.globalShops.getItem(args.asString(1));
                if (item == null)
                {
                    sender.sendMessage("&cno item");
                    return;
                }

                final Player player = (Player) sender.unwrapped();
                final IPlayerContainer container = this.globalShops.getPlayer(player);

                container.addItem(item);
                sender.sendMessage("&aok");
            }
            else if (arg0.equalsIgnoreCase("activate"))
            {
                final Item item = this.globalShops.getItem(args.asString(1));
                if (item == null)
                {
                    sender.sendMessage("&cno item");
                    return;
                }

                final Player player = (Player) sender.unwrapped();
                final IPlayerContainer container = this.globalShops.getPlayer(player);

                container.markAsActive(item);
                sender.sendMessage("&aok");
            }*/
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
