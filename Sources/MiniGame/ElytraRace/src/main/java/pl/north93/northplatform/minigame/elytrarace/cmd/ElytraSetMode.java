package pl.north93.northplatform.minigame.elytrarace.cmd;

import static org.diorite.commons.reflections.DioriteReflectionUtils.getField;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.FieldAccessor;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.minigame.elytrarace.ElytraRaceMode;

public class ElytraSetMode extends NorthCommand
{
    private static final FieldAccessor<String> VARIANT_FIELD = getField(GameIdentity.class, "variantId");

    public ElytraSetMode()
    {
        super("elytrasetmode");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final INorthPlayer player = INorthPlayer.wrap(sender);
        if (! ElytraDevMode.checkDevMode(player))
        {
            return;
        }

        if (args.length() != 1)
        {
            player.sendMessage(ChatColor.RED + "/elytrasetmode <RACE_MODE/SCORE_MODE>");
            player.sendMessage(ChatColor.RED + "Nastapi ponowne zainicjowanie areny z wybranym trybem. Wszystkie nowo uruchomione areny beda mialy ten tryb.");
            return;
        }

        final LocalArena arena = getArena(player);
        if (arena == null)
        {
            player.sendMessage(ChatColor.RED + "Musisz byc na jakiejs arenie!");
            return;
        }

        final ElytraRaceMode newMode = args.asEnumValue(ElytraRaceMode.class, 0);

        for (final Player arenaPlayer : arena.getPlayersManager().getPlayers())
        {
            arenaPlayer.sendMessage(ChatColor.DARK_GREEN + "Zmiana trybu gry na " + newMode + "! Arena zostanie ponownie zainicjowana.");
        }


        VARIANT_FIELD.set(arena.getMiniGame(), newMode == ElytraRaceMode.RACE_MODE ? "race" : "score");

        arena.endGame();
        arena.prepareNewCycle();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
