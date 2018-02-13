package pl.north93.zgame.api.global.uri;

@FunctionalInterface
public interface IUriCallHandler
{
    Object handle(UriInvocationContext context);
}
