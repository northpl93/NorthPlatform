package pl.north93.zgame.api.global.uri;

import java.util.Map;

@FunctionalInterface
public interface IUriCallHandler
{
    Object handle(String calledUri, Map<String, String> parameters);
}
