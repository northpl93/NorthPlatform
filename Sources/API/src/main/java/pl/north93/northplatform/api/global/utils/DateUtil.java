package pl.north93.northplatform.api.global.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.north93.northplatform.api.global.messages.UTF8Control;

public class DateUtil
{
    private static final Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
    private static final int[] types = new int[]
                          {
                                  Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND
                          };
    private static final ResourceBundle apiMessages = ResourceBundle.getBundle("Messages", new UTF8Control());
    private static final String[] names = new String[]
                             {
                                     apiMessages.getString("date.year"), apiMessages.getString("date.years"),
                                     apiMessages.getString("date.month"), apiMessages.getString("date.months"), 
                                     apiMessages.getString("date.day"), apiMessages.getString("date.days"), 
                                     apiMessages.getString("date.hour"), apiMessages.getString("date.hours"), 
                                     apiMessages.getString("date.minute"), apiMessages.getString("date.minutes"), 
                                     apiMessages.getString("date.second"), apiMessages.getString("date.seconds")
                             };

    public static String removeTimePattern(final String input)
    {
        return timePattern.matcher(input).replaceFirst("").trim();
    }

    public static long parseDateDiff(final String time, final boolean future) throws Exception
    {
        final Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find())
        {
            if (m.group() == null || m.group().isEmpty())
            {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++)
            {
                if (m.group(i) != null && !m.group(i).isEmpty())
                {
                    found = true;
                    break;
                }
            }
            if (found)
            {
                if (m.group(1) != null && !m.group(1).isEmpty())
                {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty())
                {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty())
                {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty())
                {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty())
                {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty())
                {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty())
                {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found)
        {
            throw new Exception("Illegal date");
        }
        final Calendar c = new GregorianCalendar();
        if (years > 0)
        {
            c.add(Calendar.YEAR, years * (future ? 1 : -1));
        }
        if (months > 0)
        {
            c.add(Calendar.MONTH, months * (future ? 1 : -1));
        }
        if (weeks > 0)
        {
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        }
        if (days > 0)
        {
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
        }
        if (hours > 0)
        {
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        }
        if (minutes > 0)
        {
            c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        }
        if (seconds > 0)
        {
            c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
        }
        final Calendar max = new GregorianCalendar();
        max.add(Calendar.YEAR, 10);
        if (c.after(max))
        {
            return max.getTimeInMillis();
        }
        return c.getTimeInMillis();
    }

    static int dateDiff(final int type, final Calendar fromDate, final Calendar toDate, final boolean future)
    {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate)))
        {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }

    public static String formatDateDiff(final long date)
    {
        final Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        final Calendar now = new GregorianCalendar();
        return DateUtil.formatDateDiff(now, c);
    }

    public static String formatDateDiff(final Calendar fromDate, final Calendar toDate)
    {
        boolean future = false;
        if (toDate.equals(fromDate))
        {
            return apiMessages.getString("date.now");
        }
        if (toDate.after(fromDate))
        {
            future = true;
        }
        final StringBuilder sb = new StringBuilder();
        int accuracy = 0;
        for (int i = 0; i < types.length; i++)
        {
            if (accuracy > 2)
            {
                break;
            }
            final int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0)
            {
                accuracy++;
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
            }
        }
        if (sb.length() == 0)
        {
            return apiMessages.getString("date.now");
        }
        return sb.toString().trim();
    }
}