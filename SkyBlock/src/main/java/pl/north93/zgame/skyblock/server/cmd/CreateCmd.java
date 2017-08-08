package pl.north93.zgame.skyblock.server.cmd;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.utils.DateUtil;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.gui.IslandTypePicker;
import pl.north93.zgame.skyblock.shared.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class CreateCmd extends NorthCommand
{
    @Inject
    private BukkitApiCore   apiCore;
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;
    @Inject @Messages("SkyBlock")
    private MessagesBox     messages;

    public CreateCmd()
    {
        super("create", "stworz");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final SkyPlayer skyPlayer = SkyPlayer.get(this.networkManager.getPlayers().unsafe().getOnline(sender.getName()));
        if (skyPlayer.hasIsland())
        {
            sender.sendMessage(this.messages, "error.already_has_island");
            return;
        }

        if (! this.server.getServerManager().canGenerateIsland(skyPlayer))
        {
            final String createTime = DateUtil.formatDateDiff(skyPlayer.getIslandCooldown() + this.server.getSkyBlockConfig().getIslandGenerateCooldown());
            sender.sendMessage(this.messages, "error.create_cooldown", createTime);
            return;
        }

        final Player player = (Player) sender.unwrapped();

        final List<IslandConfig> islandTypes = this.server.getSkyBlockConfig().getIslandTypes();
        final List<IslandConfig> availableIslands = islandTypes.stream()
                                                               .filter(is -> player.hasPermission("skyblock.island." + is.getName()))
                                                               .collect(Collectors.toList());

        if (availableIslands.isEmpty())
        {
            sender.sendMessage(this.messages, "error.no_any_island_type_available");
        }
        else if (availableIslands.size() == 1)
        {
            this.server.getSkyBlockManager().createIsland(availableIslands.get(0).getName(), sender.getName());
        }
        else
        {
            this.showIslandPicker(player, availableIslands);
        }
    }

    private void showIslandPicker(final Player player, final List<IslandConfig> config)
    {
        final Consumer<String> callback = type -> this.server.getSkyBlockManager().createIsland(type, player.getName());
        final IslandTypePicker window = new IslandTypePicker("Wybierz rodzaj wyspy", config, callback);

        this.apiCore.getWindowManager().openWindow(player, window);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
