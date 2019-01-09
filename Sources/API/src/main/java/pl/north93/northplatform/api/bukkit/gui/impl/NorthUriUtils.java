package pl.north93.northplatform.api.bukkit.gui.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.uri.IUriManager;
import pl.north93.northplatform.api.global.utils.Vars;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

@Slf4j
public final class NorthUriUtils
{
    private static NorthUriUtils instance;
    @Inject
    private        IUriManager   uriManager;

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
            log.error("Failed to create a uri {}", finalUri, e);
            return null;
        }
    }
}
