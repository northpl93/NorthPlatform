package pl.north93.northplatform.api.global.messages;

import pl.north93.northplatform.api.global.component.annotations.bean.DynamicBean;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;

public class MessagesProvider
{
    @DynamicBean
    public static MessagesBox provideMessagesBox(final Messages messages, final @Named("Source") Class<?> caller)
    {
        return new MessagesBox(caller.getClassLoader(), messages.value());
    }
}
