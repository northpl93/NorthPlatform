package pl.north93.zgame.api.bukkit.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;

import pl.north93.zgame.api.bukkit.gui.impl.GuiTracker;
import pl.north93.zgame.api.bukkit.gui.impl.IClickHandler;
import pl.north93.zgame.api.bukkit.gui.impl.XmlLayoutRegistry;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.Vars;
import pl.north93.zgame.api.global.utils.lang.ClassUtils;

public class Gui implements IClickHandler
{
    private static GuiTracker guiTracker;
    
    private final GuiContent content;
    
    private Vars<Object> variables;

    private MessagesBox messagesBox;
    
    protected Gui(MessagesBox messagesBox, String layout)
    {
        this.variables = Vars.empty();
        this.messagesBox = messagesBox;
        if ( StringUtils.isNotEmpty(layout) )
        {
            final ClassLoader callerClassLoader = ClassUtils.getCallerClass().getClassLoader();
            content = XmlLayoutRegistry.getGuiLayout(callerClassLoader, layout).createGuiContent(this);
        }
        else
        {
            content = new GuiContent(this, 6);
        }

        callOnInit();
    }
    
    public final void open(Player player)
    {
        guiTracker.openGui(player, this);
    }
    
    public final boolean close(Player player)
    {
        if ( guiTracker.getCurrentGui(player) == this )
        {
            guiTracker.closeGui(player);
            return true;
        }
        
        return false;
    }
    
    public final void closeAll()
    {
        guiTracker.getEntries(this).forEach(entry -> close(entry.getPlayer()));
    }
    
    public final void click(Player player, GuiElement element, ClickType type)
    {
        GuiClickEvent event = new GuiClickEvent(player, type, element);
        guiTracker.getGuiClickHandlerManager().callClickEvent(this, element, event);
    }
    
    public final Collection<Player> getViewers()
    {
        return guiTracker.getEntries(this).stream().map(entry -> entry.getPlayer())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public final MessagesBox getMessagesBox()
    {
        return messagesBox;
    }

    public final GuiContent getContent()
    {
        return content;
    }
    
    public final Vars<Object> getVariables()
    {
        return variables;
    }
    
    public final void setVariables(Vars<Object> variables)
    {
        this.variables = variables;
    }
    
    public final void addVariables(Vars<Object> variables)
    {
        this.variables = this.variables.and(variables);
    }
    
    public final boolean isDirty()
    {
        return content.isDirty();
    }
    
    public final void markDirty()
    {
        content.markDirty();
    }
    
    public final void callOnInit()
    {
        catchException("onInit", () -> onInit());
    }

    public final void callOnRender()
    {
        catchException("onRender", this::onRender);
    }

    public final void callOnOpen(Player player)
    {
        catchException("onOpen", () -> onOpen(player));
    }
    
    public final void callOnClose(Player player)
    {
        catchException("onClose", () -> onClose(player));
    }
    
    private void catchException(String method, Runnable runnable)
    {
        try
        {
            runnable.run();
        }
        catch ( Throwable e )
        {
            System.err.println(method + "() in " + getClass().getName() + " throws an exception:");
            e.printStackTrace();
        }
    }

    protected void onInit()
    {
    }

    protected void onRender()
    {
    }
    
    protected void onOpen(Player player)
    {
    }
    
    protected void onClose(Player player)
    {
    }
    
    public static void setGuiTracker(GuiTracker guiTracker)
    {
        Preconditions.checkState(Gui.guiTracker == null);
        Gui.guiTracker = guiTracker;
    }
    
    @ClickHandler
    public final void closeGui(GuiClickEvent event)
    {
        close(event.getWhoClicked());
    }
}
