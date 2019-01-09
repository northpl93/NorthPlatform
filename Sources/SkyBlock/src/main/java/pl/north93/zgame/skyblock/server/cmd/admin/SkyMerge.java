package pl.north93.zgame.skyblock.server.cmd.admin;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class SkyMerge extends NorthCommand
{
    public SkyMerge()
    {
        super("skymerge");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 0)
        {
            sender.sendRawMessage("&c/skymerge export uuid - eksportuje wszystkie wyspy na dany serwer");
            sender.sendRawMessage("&c/skymerge import - importuje schematy wysp");
            return;
        }
        else if (args.length() == 1)
        {
            final String arg0 = args.asString(0);
            if (arg0.equals("export"))
            {

            }
            else if (arg0.equals("import"))
            {

            }
            else
            {

            }
        }
        else if (args.length() == 2)
        {
            final String arg0 = args.asString(0);
            if (arg0.equals("export"))
            {

            }
            else
            {

            }
        }
    }
}
