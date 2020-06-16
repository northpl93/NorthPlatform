package pl.north93.northplatform.api.bukkit.utils.chat;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Konwerter tekstu legacy na BaseComponent skopiowany z Bungee, z
 * zaaplikowanymi poprawkami.
 */
public final class LegacyTextConverter
{
    public static final Pattern URL_PATTERN = Pattern.compile("^(?:(https?)://)?([-\\w_.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    public static BaseComponent[] fromLegacyText(final String message)
    {
        return fromLegacyText(ChatUtils.translateAlternateColorCodes(message), ChatColor.WHITE);
    }

    public static BaseComponent[] fromLegacyText(final String message, final ChatColor defaultColor)
    {
        final ArrayList<BaseComponent> components = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        TextComponent component = new TextComponent();
        final Matcher matcher = URL_PATTERN.matcher(message);

        for (int i = 0; i < message.length(); i++)
        {
            char c = message.charAt(i);
            if (c == ChatColor.COLOR_CHAR)
            {
                if (++ i >= message.length())
                {
                    break;
                }
                c = message.charAt(i);
                if (c >= 'A' && c <= 'Z')
                {
                    c += 32;
                }
                ChatColor format = ChatColor.getByChar(c);
                if (format == null)
                {
                    continue;
                }
                if (builder.length() > 0)
                {
                    final TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }
                switch (format)
                {
                    case BOLD:
                        component.setBold(true);
                        break;
                    case ITALIC:
                        component.setItalic(true);
                        break;
                    case UNDERLINE:
                        component.setUnderlined(true);
                        break;
                    case STRIKETHROUGH:
                        component.setStrikethrough(true);
                        break;
                    case MAGIC:
                        component.setObfuscated(true);
                        break;
                    case RESET:
                        format = defaultColor;
                    default:
                        component = new TextComponent();
                        component.setColor(format);
                        break;
                }
                continue;
            }
            int pos = message.indexOf(' ', i);
            if (pos == - 1)
            {
                pos = message.length();
            }
            if (matcher.region(i, pos).find())
            { //Web link handling

                if (builder.length() > 0)
                {
                    final TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }

                final TextComponent old = component;
                component = new TextComponent(old);
                final String urlString = message.substring(i, pos);
                component.setText(urlString);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        urlString.startsWith("http") ? urlString : "http://" + urlString));
                components.add(component);
                i += pos - i - 1;
                component = old;
                continue;
            }
            builder.append(c);
        }

        component.setText(builder.toString());
        components.add(component);

        return components.toArray(new BaseComponent[0]);
    }
}
