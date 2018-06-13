package pl.north93.zgame.features.controller.broadcaster;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.math.DioriteRandomUtils;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.chat.global.ChatManager;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.features.controller.broadcaster.cfg.BroadcasterEntryCfg;
import pl.north93.zgame.features.controller.broadcaster.cfg.BroadcasterMessageCfg;

public class BroadcasterEntry implements Runnable
{
    @Inject
    private ChatManager chatManager;
    private final BroadcasterEntryCfg entryCfg;

    public BroadcasterEntry(final BroadcasterEntryCfg entryCfg)
    {
        this.entryCfg = entryCfg;
    }

    @Override
    public void run()
    {
        final Map<Locale, BaseComponent> messages = this.getRandomMessages();

        final Collection<ChatRoom> rooms = this.findRooms();
        for (final ChatRoom room : rooms)
        {
            messages.forEach(room::broadcast);
        }
    }

    private Map<Locale, BaseComponent> getRandomMessages()
    {
        final Map<String, List<BroadcasterMessageCfg>> messages = this.entryCfg.getMessages().stream().collect(Collectors.groupingBy(BroadcasterMessageCfg::getLocale));
        return messages.entrySet().stream().collect(Collectors.toMap(entry -> Locale.forLanguageTag(entry.getKey()), entry ->
        {
            final BroadcasterMessageCfg messageCfg = DioriteRandomUtils.getRandom(entry.getValue());

            final BaseComponent component = ChatUtils.fromLegacyText(messageCfg.getMessage());
            return messageCfg.getLayout().processMessage(component);
        }));
    }

    private Collection<ChatRoom> findRooms()
    {
        final List<Pattern> patterns = this.entryCfg.getRooms().stream().map(Pattern::compile).collect(Collectors.toList());

        final Collection<ChatRoom> rooms = this.chatManager.getChatRooms();
        return rooms.stream().filter(room ->
        {
            for (final Pattern pattern : patterns)
            {
                if (pattern.matcher(room.getId()).matches())
                {
                    return true;
                }
            }

            return false;
        }).collect(Collectors.toList());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("entryCfg", this.entryCfg).toString();
    }
}
