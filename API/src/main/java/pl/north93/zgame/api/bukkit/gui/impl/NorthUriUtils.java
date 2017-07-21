package pl.north93.zgame.api.bukkit.gui.impl;

import static java.text.MessageFormat.format;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.uri.IUriManager;
import pl.north93.zgame.api.global.utils.Vars;

public final class NorthUriUtils
{
    private static NorthUriUtils instance;
    @Inject
    private IUriManager uriManager;

    private NorthUriUtils()
    {
    }

    public static NorthUriUtils getInstance()
    {
        if (instance == null)
        {
            instance = new NorthUriUtils();
        }
        return instance;
    }

    public Object call(final String uri, final Vars<Object> vars)
    {
        String finalUri = uri;
        for (final Map.Entry<String, Object> stringObjectEntry : vars.asMap().entrySet())
        {
            finalUri = StringUtils.replace(finalUri, stringObjectEntry.getKey(), stringObjectEntry.getValue().toString());
        }

        try
        {
            return this.uriManager.call(new URI(finalUri));
        }
        catch (final URISyntaxException e)
        {
            API.getLogger().log(Level.SEVERE, format("Failed to create a uri {0}", finalUri), e);
            return null;
        }
    }
}
