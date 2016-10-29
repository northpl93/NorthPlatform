package pl.north93.zgame.api.global.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaArguments
{
    private String                  jar;
    private int                     startHeapSize;
    private int                     maxHeapSize;
    private List<String>            javaArgs;
    private HashMap<String, String> envVars;

    public JavaArguments()
    {
        this.javaArgs = new ArrayList<>();
        this.envVars = new HashMap<>();
    }

    public JavaArguments setJar(final String jar)
    {
        this.jar = jar;
        return this;
    }

    public JavaArguments setStartHeapSize(final int startHeapSize)
    {
        this.startHeapSize = startHeapSize;
        return this;
    }

    public JavaArguments setMaxHeapSize(final int maxHeapSize)
    {
        this.maxHeapSize = maxHeapSize;
        return this;
    }

    public JavaArguments addJavaArg(final String argument)
    {
        this.javaArgs.add(argument);
        return this;
    }

    public JavaArguments addEnvVar(final String key, final String value)
    {
        this.envVars.put(key, value);
        return this;
    }

    public String buildStartLine()
    {
        final StringBuilder line = new StringBuilder(256);

        line.append("java ");
        for (final String javaArg : this.javaArgs)
        {
            line.append('-');
            line.append(javaArg);
            line.append(" ");
        }

        line.append("-Xmx");
        line.append(this.maxHeapSize);
        line.append("m ");

        line.append("-Xms");
        line.append(this.startHeapSize);
        line.append("m ");

        for (final Map.Entry<String, String> envVar : this.envVars.entrySet())
        {
            line.append("-D");
            line.append(envVar.getKey());
            line.append("=");
            line.append(envVar.getValue());
            line.append(" ");
        }

        line.append("-jar ");
        line.append(this.jar);

        return line.toString();
    }

    @Override
    public String toString()
    {
        return this.buildStartLine();
    }
}
