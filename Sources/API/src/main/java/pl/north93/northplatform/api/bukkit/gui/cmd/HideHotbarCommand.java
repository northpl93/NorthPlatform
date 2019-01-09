package pl.north93.northplatform.api.bukkit.gui.cmd;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.gui.impl.GuiTracker;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

/**
 * Komenda developerska umozliwiajaca usuniecie swojego hotbara, aby
 * nie trzeba bylo modyfikowac kodu i usuwac jego ustawiania.
 */
public class HideHotbarCommand extends NorthCommand
{
    @Inject
    private GuiTracker guiTracker;

    public HideHotbarCommand()
    {
        super("hidehotbar", "removehotbar");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        if (this.guiTracker.hasHotbarMenu(player))
        {
            this.guiTracker.closeHotbarMenu(player);
            sender.sendMessage("&aUsunieto hotbar");
        }
        else
        {
            sender.sendMessage("&cNie posiadasz otwartego hotbara");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
