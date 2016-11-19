package pl.north93.zgame.api.global.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.commons.lang3.StringUtils;

public class NorthFormatter extends Formatter
{
    private static final DateFormat df = new SimpleDateFormat("hh:mm:ss");

    @Override
    public String format(final LogRecord record)
    {
        final String[] splittedClass = StringUtils.split(record.getSourceClassName(), '.');
        final String className = splittedClass[splittedClass.length - 1];

        final String message = this.formatMessage(record) + " (" + className + '#' + record.getSourceMethodName() + ')';

        final StringBuilder log = new StringBuilder(512);
        log.append(this.createLine(record.getMillis(), record.getLevel(), message));

        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        final Throwable throwable = record.getThrown();
        if (throwable != null)
        {
            final List<String> stacktrace = this.createThrowable(false, throwable);
            stacktrace.forEach(stm -> log.append(this.createLine(record.getMillis(), record.getLevel(), stm)));
        }

        return log.toString();
    }

    private String createLine(final long millis, final Level level, final String message)
    {
        final StringBuilder builder = new StringBuilder(512);
        builder.append('[');
        builder.append(df.format(new Date(millis))).append(' ');
        builder.append(level).append("]: ");
        builder.append(message);
        builder.append('\n');
        return builder.toString();
    }

    private List<String> createThrowable(final boolean isCausedBy, final Throwable throwable)
    {
        final ArrayList<String> lines = new ArrayList<>(16);

        if (isCausedBy)
        {
            lines.add("Caused by " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }
        else
        {
            lines.add("Exception has been thrown " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }

        for (final StackTraceElement element : throwable.getStackTrace())
        {
            lines.add("  " + element.getClassName() + "/" + element.getMethodName() + "@" + element.getLineNumber());
        }

        if (throwable.getCause() != null)
        {
            lines.addAll(this.createThrowable(true, throwable.getCause()));
        }

        return lines;
    }

    @Override
    public String getHead(Handler h)
    {
        return super.getHead(h);
    }

    @Override
    public String getTail(Handler h)
    {
        return super.getTail(h);
    }
}
