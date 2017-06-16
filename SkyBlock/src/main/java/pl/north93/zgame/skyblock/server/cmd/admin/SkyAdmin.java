package pl.north93.zgame.skyblock.server.cmd.admin;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class SkyAdmin extends NorthCommand
{
    public SkyAdmin()
    {
        super("skyadmin");
        this.setPermission("skyblock.admin");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        sender.sendRawMessage("&f&l> &6/odwiedz nick &7odwiedza gracza bez pozwolenia.");
        sender.sendRawMessage("&f&l> &6/skyaddmember nick nick &7dodaje gracza do wyspy.");
        sender.sendRawMessage("&f&l> &6/skysetowner nick nick &7zmienia wlasciciela wyspy.");
        sender.sendRawMessage("&f&l> &6/skybypass &7wlacza/wylacza ograniczenia budowania.");
        sender.sendRawMessage("&f&l> &6/skydelete nick &7usuwa wyspe danego gracza.");
        sender.sendRawMessage("&f&l> &6/skyresettimer nick &7resetuje czas oczekiwania na stworzenie wyspy.");
        sender.sendRawMessage("&f&l> &6/wtfisland nick lub $here &7informacje o wyspie.");
        sender.sendRawMessage("&f&l> &6/skypoints &7zarzadzanie punktami wysp.");
        sender.sendRawMessage("&f&l> &6/skyrankingswitch nick &7dodaje/usuwa wyspe z rankingu.");
    }
}
