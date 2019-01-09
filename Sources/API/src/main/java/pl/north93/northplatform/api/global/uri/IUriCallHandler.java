package pl.north93.northplatform.api.global.uri;

@FunctionalInterface
public interface IUriCallHandler
{
    Object handle(UriInvocationContext context);
}
