package pl.north93.northplatform.api.global.uri;

import java.net.URI;

public interface IUriManager
{
    void register(String pattern, IUriCallHandler handler);

    Object call(URI uri, Object... parameter);

    Object call(String uri, Object... parameter);
}
