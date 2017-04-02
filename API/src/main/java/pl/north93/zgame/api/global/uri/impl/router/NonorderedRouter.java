package pl.north93.zgame.api.global.uri.impl.router;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.uri.IUriCallHandler;

public class NonorderedRouter
{
    protected final List<Pattern> patterns = new ArrayList<>();

    // Reverse index to create reverse routes fast
    protected final Map<IUriCallHandler, List<Pattern>> reverse = new HashMap<>();

    protected IUriCallHandler notFound;

    //----------------------------------------------------------------------------

    public NonorderedRouter pattern(String path, IUriCallHandler target)
    {
        final Pattern pattern = new Pattern(path, target);
        this.patterns.add(pattern);

        List<Pattern> patterns = this.reverse.get(target);
        if (patterns == null)
        {
            patterns = new ArrayList<>();
            patterns.add(pattern);
            this.reverse.put(target, patterns);
        }
        else
        {
            patterns.add(pattern);
        }

        return this;
    }

    public NonorderedRouter notFound(IUriCallHandler target)
    {
        this.notFound = target;
        return this;
    }

    //----------------------------------------------------------------------------

    public void removeTarget(IUriCallHandler target)
    {
        this.patterns.removeIf(pattern -> pattern.handler().equals(target));
        this.reverse.remove(target);
    }

    public void removePath(String path)
    {
        String normalizedPath = Pattern.removeSlashAtBothEnds(path);

        this.removePatternByPath(this.patterns, normalizedPath);

        for (Map.Entry<IUriCallHandler, List<Pattern>> entry : this.reverse.entrySet())
        {
            List<Pattern> patterns = entry.getValue();
            this.removePatternByPath(patterns, normalizedPath);
        }
    }

    private void removePatternByPath(List<Pattern> patterns, String path)
    {
        patterns.removeIf(pattern -> pattern.path().equals(path));
    }

    //----------------------------------------------------------------------------

    public Routed route(final String path)
    {
        final String[] tokens = Pattern.removeSlashAtBothEnds(path).split("/");
        final Map<String, String> params = new HashMap<>();
        boolean matched = true;

        for (final Pattern pattern : this.patterns)
        {
            final String[] currTokens = pattern.tokens();
            final IUriCallHandler handler = pattern.handler();

            matched = true;
            params.clear();

            if (tokens.length == currTokens.length)
            {
                for (int i = 0; i < currTokens.length; i++)
                {
                    final String token = tokens[i];
                    final String currToken = currTokens[i];

                    if (currToken.length() > 0 && currToken.charAt(0) == ':')
                    {
                        params.put(currToken.substring(1), token);
                    }
                    else if (! currToken.equals(token))
                    {
                        matched = false;
                        break;
                    }
                }
            }
            else if (currTokens.length > 0 && currTokens[currTokens.length - 1].equals(":*") && tokens.length >= currTokens.length)
            {
                for (int i = 0; i < currTokens.length - 1; i++)
                {
                    final String token = tokens[i];
                    final String currToken = currTokens[i];

                    if (currToken.length() > 0 && currToken.charAt(0) == ':')
                    {
                        params.put(currToken.substring(1), token);
                    }
                    else if (! token.equals(token))
                    {
                        matched = false;
                        break;
                    }
                }

                if (matched)
                {
                    final StringBuilder b = new StringBuilder(tokens[currTokens.length - 1]);
                    for (int i = currTokens.length; i < tokens.length; i++)
                    {
                        b.append('/');
                        b.append(tokens[i]);
                    }
                    params.put("*", b.toString());
                }
            }
            else
            {
                matched = false;
            }

            if (matched)
            {
                return new Routed(handler, false, params);
            }
        }

        if (this.notFound != null)
        {
            params.clear();
            return new Routed(this.notFound, true, params);
        }

        return null;
    }

    //----------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public String path(IUriCallHandler target, Object... params)
    {
        if (params.length == 0)
        {
            return this.path(target, Collections.emptyMap());
        }

        if (params.length == 1 && params[0] instanceof Map<?, ?>)
        {
            return this.pathMap(target, (Map<Object, Object>) params[0]);
        }

        if (params.length % 2 == 1)
        {
            throw new RuntimeException("Missing value for param: " + params[params.length - 1]);
        }

        final Map<Object, Object> map = new HashMap<Object, Object>();
        for (int i = 0; i < params.length; i += 2)
        {
            final String key = params[i].toString();
            final String value = params[i + 1].toString();
            map.put(key, value);
        }
        return this.pathMap(target, map);
    }

    private String pathMap(final IUriCallHandler target, final Map<Object, Object> params)
    {
        final List<Pattern> patterns = this.reverse.get(target);
        if (patterns == null)
        {
            return null;
        }

        try
        {
            // The best one is the one with minimum number of params in the query
            String bestCandidate = null;
            int minQueryParams = Integer.MAX_VALUE;

            boolean matched = true;
            final Set<String> usedKeys = new HashSet<String>();

            for (final Pattern pattern : patterns)
            {
                matched = true;
                usedKeys.clear();

                final StringBuilder b = new StringBuilder();

                for (final String token : pattern.tokens())
                {
                    b.append('/');

                    if (token.length() > 0 && token.charAt(0) == ':')
                    {
                        final String key = token.substring(1);
                        final Object value = params.get(key);
                        if (value == null)
                        {
                            matched = false;
                            break;
                        }

                        usedKeys.add(key);
                        b.append(value);
                    }
                    else
                    {
                        b.append(token);
                    }
                }

                if (matched)
                {
                    final int numQueryParams = params.size() - usedKeys.size();
                    if (numQueryParams < minQueryParams)
                    {
                        if (numQueryParams > 0)
                        {
                            boolean firstQueryParam = true;

                            for (final Map.Entry<Object, Object> entry : params.entrySet())
                            {
                                final String key = entry.getKey().toString();
                                if (! usedKeys.contains(key))
                                {
                                    if (firstQueryParam)
                                    {
                                        b.append('?');
                                        firstQueryParam = false;
                                    }
                                    else
                                    {
                                        b.append('&');
                                    }

                                    final String value = entry.getValue().toString();
                                    b.append(URLEncoder.encode(key, "UTF-8"));    // May throw UnsupportedEncodingException
                                    b.append('=');
                                    b.append(URLEncoder.encode(value, "UTF-8"));  // May throw UnsupportedEncodingException
                                }
                            }
                        }

                        bestCandidate = b.toString();
                        minQueryParams = numQueryParams;
                    }
                }
            }

            return bestCandidate;
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("patterns", this.patterns).append("reverse", this.reverse).append("notFound", this.notFound).toString();
    }
}
