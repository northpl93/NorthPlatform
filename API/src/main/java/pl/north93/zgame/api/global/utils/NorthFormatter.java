package pl.north93.zgame.api.global.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
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

        final StringBuilder builder = new StringBuilder(512);
        builder.append('[');
        builder.append(df.format(new Date(record.getMillis()))).append(' ');
        builder.append(record.getLevel()).append("]: ");
        builder.append(this.formatMessage(record));
        builder.append(" (").append(className).append('#');
        builder.append(record.getSourceMethodName()).append(')');
        builder.append("\n");
        return builder.toString();
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
