package pl.north93.zgame.api.global.messages;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;

/**
 * Klasa odpowiedzialna za formatowanie wiadomości.
 */
public enum MessageLayout
{
    /**
     * Domyślny format, tylko koloruje wiadomość.
     */
    DEFAULT
            {
                @Override
                public BaseComponent processMessage(final BaseComponent component)
                {
                    return component;
                }
            },
    /**
     * Wiadomość otaczają spacje linijkę wyżej i niżej.
     */
    SEPARATED
            {
                @Override
                public BaseComponent processMessage(final BaseComponent component)
                {
                    return new TextComponent(NEW_LINE, component, NEW_LINE);
                }
            },
    /**
     * Wiadomość jest wyśrodkowana sprytnym algorytmem.
     */
    CENTER
            {
                @Override
                public BaseComponent processMessage(final BaseComponent component)
                {
                    return ChatUtils.centerMessage(component);
                }
            },
    SEPARATED_CENTER
            {
                @Override
                public BaseComponent processMessage(final BaseComponent component)
                {
                    return SEPARATED.processMessage(CENTER.processMessage(component));
                }
            };

    public abstract BaseComponent processMessage(BaseComponent component);

    private static final BaseComponent NEW_LINE = new TextComponent("\n");
}
