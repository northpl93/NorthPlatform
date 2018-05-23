package pl.north93.zgame.features.bukkit.chat.admin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.chat.global.ChatManager;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public class AdminChatService
{
    private final ChatRoom adminRoom;

    @Bean
    private AdminChatService(final ChatManager chatManager)
    {
        this.adminRoom = chatManager.getOrCreateRoom("admin", new AdminChatFormatter(), 0);
    }

    public ChatRoom getAdminRoom()
    {
        return this.adminRoom;
    }

    public void broadcast(final BaseComponent component)
    {
        this.adminRoom.broadcast(component);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("adminRoom", this.adminRoom).toString();
    }
}
