package pl.north93.zgame.api.global.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import pl.north93.zgame.api.global.API;

/**
 * Klasa tworząca tabele SQL
 */
public final class SqlTables
{
    public static void createTables()
    {
        try (final Connection connection = API.getApiCore().getMysql().getConnection(); final Statement statement = connection.createStatement())
        {
            statement.execute("CREATE TABLE IF NOT EXISTS username_cache (`username` VARCHAR(16) NOT NULL, `premium` BIT(1) NOT NULL, `fetchtime` TIMESTAMP NOT NULL, UNIQUE INDEX `username_UNIQUE` (`username` ASC));");
        }
        catch (final SQLException e)
        {
            throw new RuntimeException("Failed to create tables in SQL", e);
        }
    }
}
