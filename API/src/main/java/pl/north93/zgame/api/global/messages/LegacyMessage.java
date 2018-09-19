package pl.north93.zgame.api.global.messages;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.ToString;
import net.md_5.bungee.api.ChatColor;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;

/**
 * Reprezentuje wiadomość zapisaną w starym systemie kolorowania,
 * wciąż używanym w niektórych częściach minecrafta.
 */
@ToString
public class LegacyMessage
{
    public static final LegacyMessage EMPTY = new LegacyMessage("");
    private final String   message;
    private final String[] lines;

    public LegacyMessage(final String legacyText)
    {
        this.message = legacyText;
        this.lines = this.message.split("\n");
    }

    /**
     * Zwraca listę linijek tej wiadomości.
     *
     * @return Lista linijek tej wiadomości.
     */
    public List<String> asList()
    {
        return Arrays.asList(this.lines);
    }

    public String asString()
    {
        return this.message;
    }

    public String asNonColoredString()
    {
        return ChatColor.stripColor(this.message);
    }
    
    public String asNonEmptyString()
    {
        if (StringUtils.isEmpty(this.message))
        {
            return ChatUtils.COLOR_CHAR + "r";
        }

        return this.message;
    }

    public void send(final Messageable messageable)
    {
        for (final String line : this.lines)
        {
            messageable.sendMessage(line);
        }
    }
    
    /**
     * Creates LegacyMessage from string
     * Note that this method translates alternate color codes.
     */
    public static LegacyMessage fromString(final String legacyString)
    {
        return new LegacyMessage(ChatUtils.translateAlternateColorCodes(legacyString));
    }
}
