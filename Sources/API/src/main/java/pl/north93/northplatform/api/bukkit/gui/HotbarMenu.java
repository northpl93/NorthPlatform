package pl.north93.northplatform.api.bukkit.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.gui.event.HotbarClickEvent;
import pl.north93.northplatform.api.bukkit.gui.impl.ClickHandlerManager;
import pl.north93.northplatform.api.bukkit.gui.impl.GuiTracker;
import pl.north93.northplatform.api.bukkit.gui.impl.XmlLayoutRegistry;
import pl.north93.northplatform.api.bukkit.gui.impl.XmlReaderContext;
import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickHandler;
import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickSource;
import pl.north93.northplatform.api.bukkit.gui.impl.xml.XmlHotbarEntry;
import pl.north93.northplatform.api.bukkit.gui.impl.xml.XmlHotbarLayout;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.Vars;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.utils.lang.ClassUtils;

@Slf4j
public class HotbarMenu implements IClickSource
{
    @Inject
    private static GuiTracker guiTracker;
    
    private final MessagesBox messages;
    
    private final HotbarEntry[] entries = new HotbarEntry[9];
    
    private Vars<Object> variables = Vars.empty();
    
    private boolean isDirty;
    
    public HotbarMenu(MessagesBox messages, String layout)
    {
        this.messages = messages;
        createEntries();
        
        if ( layout != null && !layout.isEmpty() )
        {
            final ClassLoader callerClassLoader = ClassUtils.getCallerClass().getClassLoader();
            applyLayout(XmlLayoutRegistry.getHotbarLayout(callerClassLoader, layout));
        }
        
        callOnInit();
    }

    private void createEntries()
    {
        for ( int i = 0; i < entries.length; i++ )
        {
            entries[i] = new HotbarEntry(this, i);
        }
    }
    
    private void applyLayout(XmlHotbarLayout layout)
    {
        XmlReaderContext ctx = new XmlReaderContext(this, messages, variables);
        
        for ( XmlHotbarEntry entry : layout.getEntries() )
        {
            HotbarEntry current = getEntry(entry.getPosition());
            
            current.setVisible(entry.isVisible());

            for (final String onClickString : entry.getOnClick())
            {
                final IClickHandler handler = ClickHandlerManager.getInstance().processClickHandler(this, onClickString);
                current.getClickHandlers().add(handler);
            }

            current.getMetadata().putAll(entry.getMetadataAsMap());
            current.setIcon(entry.createGuiIcon(ctx));
        }
    }
    
    public final void display(Player player)
    {
        guiTracker.displayHotbarMenu(player, this);
    }
    
    public final boolean close(Player player)
    {
        if ( guiTracker.getCurrentHotbarMenu(player) == this )
        {
            guiTracker.closeHotbarMenu(player);
            return true;
        }
        
        return false;
    }
    
    public final void closeAll()
    {
        guiTracker.getEntries(this).forEach(entry -> close(entry.getPlayer()));
    }
    
    public final Collection<Player> getViewers()
    {
        return guiTracker.getEntries(this).stream().map(entry -> entry.getPlayer())
                .collect(Collectors.toCollection(ArrayList::new));
    }
    
    public final void click(Player player, HotbarEntry entry, ClickType type)
    {
        HotbarClickEvent event = new HotbarClickEvent(player, type, entry);
        for (final IClickHandler handler : entry.getClickHandlers())
        {
            handler.handle(this, event);
        }
    }
    
    public final MessagesBox getMessages()
    {
        return messages;
    }
    
    public final HotbarEntry getEntry(int slot)
    {
        return entries[slot];
    }
    
    public final HotbarEntry[] getEntries()
    {
        HotbarEntry[] result = new HotbarEntry[entries.length];
        System.arraycopy(entries, 0, result, 0, entries.length);
        return result;
    }
    
    public final Vars<Object> getVariables()
    {
        return variables;
    }
    
    public final boolean isDirty()
    {
        return isDirty;
    }
    
    public final void markDirty()
    {
        isDirty = true;
    }
    
    public final void resetDirty()
    {
        isDirty = false;
    }
    
    public void callOnInit()
    {
        catchException("onInit", this::onInit);
    }
    
    public void callOnOpen(Player player)
    {
        catchException("onOpen", () -> onOpen(player));
    }
    
    public void callOnClose(Player player)
    {
        catchException("onOpen", () -> onClose(player));
    }
    
    private void catchException(String method, Runnable runnable)
    {
        try
        {
            runnable.run();
        }
        catch ( Throwable e )
        {
            log.error("{}() in {} throws an exception", method, getClass().getName(), e);
        }
    }
    
    protected void onInit()
    {
    }
    
    protected void onOpen(Player player)
    {
    }
    
    protected void onClose(Player player)
    {
    }
}
