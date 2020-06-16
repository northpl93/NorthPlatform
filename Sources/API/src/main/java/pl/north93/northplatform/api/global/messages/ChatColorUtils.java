package pl.north93.northplatform.api.global.messages;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;

@UtilityClass
public class ChatColorUtils
{
    public String getLastColors(final String input)
    {
        String result = "";
        final int length = input.length();
        for ( int index = length - 1; index > -1; --index )
        {
            final char section = input.charAt(index);
            if ( section == '§' && index < length - 1 )
            {
                final char c = input.charAt(index + 1);
                
                if ( ChatColor.getByChar(c) != null )
                {
                    result = "§" + c + result;
                    if ( "0123456789abcdef".indexOf(c) != -1 )
                    {
                        break;
                    }
                    if ( c == 'r' )
                    {
                        break;
                    }
                }
            }
        }
        
        return result;
    }
}
