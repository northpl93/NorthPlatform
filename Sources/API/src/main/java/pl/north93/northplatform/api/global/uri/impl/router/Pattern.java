package pl.north93.northplatform.api.global.uri.impl.router;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.uri.IUriCallHandler;

public class Pattern
{
    private final String          path;
    private final String[]        tokens;
    private final IUriCallHandler handler;

    public static String removeSlashAtBothEnds(final String path)
    {
        if (path.isEmpty())
        {
            return path;
        }

        int beginIndex = 0;
        while (beginIndex < path.length() && path.charAt(beginIndex) == '/')
        {
            beginIndex++;
        }
        if (beginIndex == path.length())
        {
            return "";
        }

        int endIndex = path.length() - 1;
        while (endIndex > beginIndex && path.charAt(endIndex) == '/')
        {
            endIndex--;
        }

        return path.substring(beginIndex, endIndex + 1);
    }

    public Pattern(final String path, final IUriCallHandler handler)
    {
        this.path = removeSlashAtBothEnds(path);
        this.tokens = this.path.split("/");
        this.handler = handler;
    }

    public String path()
    {
        return this.path;
    }

    public String[] tokens()
    {
        return this.tokens;
    }

    public IUriCallHandler handler()
    {
        return this.handler;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("path", this.path).append("tokens", this.tokens).append("handler", this.handler).toString();
    }
}