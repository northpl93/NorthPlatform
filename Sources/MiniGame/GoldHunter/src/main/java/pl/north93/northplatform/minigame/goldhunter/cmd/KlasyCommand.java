package pl.north93.northplatform.minigame.goldhunter.cmd;

import org.bukkit.entity.Player;

import pl.north93.northplatform.minigame.goldhunter.GoldHunter;
import pl.north93.northplatform.minigame.goldhunter.gui.SelectClassGui;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class KlasyCommand extends NorthCommand
{
    @Inject
    private static GoldHunter goldHunter;
    
    public KlasyCommand()
    {
        super("klasy", "classes");
    }
    
    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label)
    {
        GoldHunterPlayer player = goldHunter.getPlayer((Player) sender.unwrapped());
        new SelectClassGui(player).open(player.getPlayer());
    }
}
