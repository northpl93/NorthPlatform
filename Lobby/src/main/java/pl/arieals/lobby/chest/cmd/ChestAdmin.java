package pl.arieals.lobby.chest.cmd;

import static java.text.MessageFormat.format;


import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.chest.ChestService;
import pl.arieals.lobby.chest.ChestType;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ChestAdmin extends NorthCommand
{
    @Inject
    private ChestService chestService;

    public ChestAdmin()
    {
        super("chestadmin");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            final String types = this.chestService.getChestTypes().stream().map(ChestType::getName).collect(Collectors.joining(", "));
            sender.sendMessage("&cDostepne typy skrzynek: " + types);
            sender.sendMessage("&c* add <gracz> <typ> <ilosc>");
            sender.sendMessage("&c* check <gracz> <typ>");
            sender.sendMessage("&c* reset <gracz> <typ>");
            return;
        }

        if ("add".equalsIgnoreCase(args.asString(0)))
        {
            if (args.length() == 4)
            {
                final Player player = Bukkit.getPlayer(args.asString(1));
                final String chestType = args.asString(2);
                final Integer amount = args.asInt(3);

                final ChestType type = this.chestService.getType(chestType);
                this.chestService.addChests(player, type, amount);
                sender.sendMessage(format("&aDodano {0} {1} skrzynek {2}", player.getName(), amount, type.getName()));
            }
            else
            {
                sender.sendMessage("&cZbyt malo argumentow; <gracz> <typ> <ilosc>");
            }
        }
        else if ("check".equalsIgnoreCase(args.asString(0)))
        {
            if (args.length() == 3)
            {
                final Player player = Bukkit.getPlayer(args.asString(1));
                final String chestType = args.asString(2);

                final ChestType type = this.chestService.getType(chestType);
                final int chests = this.chestService.getChests(player, type);
                sender.sendMessage(format("&aIlosc skrzynek {0} to {1}", player.getName(), chests));
            }
            else
            {
                sender.sendMessage("&cZbyt malo argumentow; <gracz> <typ>");
            }
        }
        else if ("reset".equalsIgnoreCase(args.asString(0)))
        {
            if (args.length() == 3)
            {
                final Player player = Bukkit.getPlayer(args.asString(1));
                final String chestType = args.asString(2);

                final ChestType type = this.chestService.getType(chestType);
                this.chestService.setChests(player, type, 0);
                sender.sendMessage(format("&aZresetowano ilosc skrzynek {0} gracza {1}", chestType, player.getName()));
            }
            else
            {
                sender.sendMessage("&cZbyt malo argumentow; <gracz> <typ>");
            }
        }
        else
        {
            sender.sendMessage("&cNiepoprawny argument");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
