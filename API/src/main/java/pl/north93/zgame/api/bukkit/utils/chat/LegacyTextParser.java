package pl.north93.zgame.api.bukkit.utils.chat;

import static java.lang.Character.toLowerCase;

import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.translateAlternateColorCodes;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Ulepszony parser legacy tekstu do BaseComponent z obsługą parametrów.
 * <p>
 * Placeholdery w tekst wstawiamy jako {@code {INDEX}}, czyli podobnie
 * jak w {@link java.text.MessageFormat}.
 */
public final class LegacyTextParser
{
    private static final Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    private LegacyTextParser()
    {
    }

    /**
     * Punkt wejścia do parsera.
     *
     * @param legacyText Tekst w formacie legacy, możliwe użycie parametrów.
     * @param params Lista parametrów która zostanie podstawiona.
     * @return Wynikowy BaseComponent.
     */
    public static BaseComponent parseLegacyText(final String legacyText, final Object... params)
    {
        final BaseComponent[] components = fromLegacyText(translateAlternateColorCodes(legacyText), params);
        if (components.length == 1)
        {
            return components[0];
        }

        return new TextComponent(components);
    }

    // konwertuje dany parametr na BaseComponent. Można tu się dopisywać z formatowaniem
    // daty/czasu itp.
    private static TextComponent getParameterAsBaseComponent(final Object object)
    {
        if (object instanceof BaseComponent)
        {
            return packToTextComponent((BaseComponent) object);
        }

        return packToTextComponent(parseLegacyText(String.valueOf(object)));
    }

    private static BaseComponent[] fromLegacyText(final String message, final Object[] params)
    {
        final ArrayList<BaseComponent> components = new ArrayList<>();
        final Matcher matcher = url.matcher(message);

        StringBuilder builder = new StringBuilder();
        TextComponent component = new TextComponent();

        for (int i = 0; i < message.length(); i++)
        {
            final char c = message.charAt(i);
            if (c == ChatColor.COLOR_CHAR)
            {
                if (++ i >= message.length())
                {
                    break;
                }
                final char colorCode = toLowerCase(message.charAt(i)); // tutaj i jest przesuniete o jeden w ifie wyzej
                ChatColor format = ChatColor.getByChar(colorCode);
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
                        format = ChatColor.WHITE;
                    default:
                        component = new TextComponent();
                        component.setColor(format);
                        break;
                }
            }
            else if (c == '{')
            {
                final int closingPos = message.indexOf('}', i); // szukamy zamknięcia
                if (closingPos == - 1)
                {
                    // jeśli brak zamknięcia klamry to ignorujemy i idziemy dalej
                    continue;
                }

                final String parameter = message.substring(i + 1, closingPos);
                final int paramIndex = Integer.valueOf(parameter); // zostanie rzucony wyjątek w przypadku niepoprawnej liczby

                if (builder.length() > 0) // jeśli mamy już tekst w buforze to go zrzucamy
                {
                    final TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }

                final TextComponent old = component;
                component = getParameterAsBaseComponent(params[paramIndex]);
                components.add(component);
                migrateFormatting(old, component); // zapewniamy że dalsze komponenty będą miały takie samo formatowanie jak argument
                component = old;

                i = closingPos; // przenosimy kursor na klamrę zamykającą
            }
            else
            {
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
        }

        if (builder.length() > 0)
        {
            component.setText(builder.toString());
            components.add(component);
        }
        else if (components.isEmpty())
        {
            // The client will crash if the array is empty
            components.add(component);
        }

        return components.toArray(new BaseComponent[0]);
    }

    // uzywane w parsowaniu parametru, zapewnia ze tekst za parametrem zachowa formatowanie
    private static void migrateFormatting(final BaseComponent from, final BaseComponent to)
    {
        if (to.getColorRaw() == null)
        {
            to.setColor(from.getColorRaw());
        }
        if (to.isBoldRaw() == null)
        {
            to.setBold(from.isBoldRaw());
        }
        if (to.isItalicRaw() == null)
        {
            to.setItalic(from.isItalicRaw());
        }
        if (to.isUnderlinedRaw() == null)
        {
            to.setUnderlined(from.isUnderlinedRaw());
        }
        if (to.isStrikethroughRaw() == null)
        {
            to.setStrikethrough(from.isStrikethroughRaw());
        }

        from.setColor(to.getColorRaw());
        from.setBold(to.isBoldRaw());
        from.setItalic(to.isItalicRaw());
        from.setUnderlined(to.isUnderlinedRaw());
        from.setStrikethrough(to.isStrikethroughRaw());
    }

    // opakowuje konponent w TextComponent lub zwraca go jeśli już jest TextComponentem
    private static TextComponent packToTextComponent(final BaseComponent component)
    {
        if (component instanceof TextComponent)
        {
            return (TextComponent) component;
        }
        return new TextComponent(component);
    }
}