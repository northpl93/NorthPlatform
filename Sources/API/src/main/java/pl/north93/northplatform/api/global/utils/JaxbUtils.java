package pl.north93.northplatform.api.global.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.xml.bind.v2.ContextFactory;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("unchecked")
public class JaxbUtils
{
    private static final Map<Class<?>, JAXBContext> CONTEXT_CACHE = new ConcurrentHashMap<>();

    public static <T> T unmarshal(final File xmlFile, final Class<T> type)
    {
        try
        {
            return (T) getContext(type).createUnmarshaller().unmarshal(xmlFile);
        }
        catch (final JAXBException e)
        {
            throw new RuntimeException("Failed to unmarshal xml", e);
        }
    }

    public static <T> T unmarshal(final URL xmlUrl, final Class<T> type)
    {
        try
        {
            return (T) getContext(type).createUnmarshaller().unmarshal(xmlUrl);
        }
        catch (final JAXBException e)
        {
            throw new RuntimeException("Failed to unmarshal xml", e);
        }
    }

    public static <T> T unmarshal(final InputStream xmlStream, final Class<T> type)
    {
        try
        {
            return (T) getContext(type).createUnmarshaller().unmarshal(xmlStream);
        }
        catch (final JAXBException e)
        {
            throw new RuntimeException("Failed to unmarshal xml", e);
        }
    }

    public static void marshal(final Object object, final File file)
    {
        try
        {
            getContext(object.getClass()).createMarshaller().marshal(object, file);
        }
        catch (final JAXBException e)
        {
            throw new RuntimeException("Failed to marshal xml", e);
        }
    }

    private JAXBContext getContext(final Class<?> type)
    {
        return CONTEXT_CACHE.computeIfAbsent(type, JaxbUtils::produceContext);
    }

    private JAXBContext produceContext(final Class<?> type)
    {
        try
        {
            // work-around; javax.xml.bind.JAXB enforces usage of contextClassLoader
            // it obviously doesn't work on bukkit and bungee where JAXB implementation is
            // inside PluginClassLoader.
            return ContextFactory.createContext(new Class[]{ type }, new HashMap<>());
        }
        catch (final JAXBException e)
        {
            throw new RuntimeException("Failed to create JAXB context", e);
        }
    }
}
