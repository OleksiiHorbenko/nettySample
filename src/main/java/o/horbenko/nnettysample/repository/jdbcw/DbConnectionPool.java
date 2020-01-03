package o.horbenko.nnettysample.repository.jdbcw;

import java.sql.Connection;
import java.sql.SQLException;

public interface DbConnectionPool {

    /**
     * Before Java 9 a resource that is to be automatically closed MUST be created
     * inside the parentheses of the try block of a try-with-resources construct.
     * From Java 9, this is no longer necessary.
     * If the variable referencing the resource is effectively final, you can simply
     * enter a reference to the variable inside the try block parentheses.
     *
     * <p>
     * ATTENTION!
     * ALWAYS use
     * <code>
     * try (Connection conn = getPooledConnection()) {
     * ...
     * }
     * </code>
     * </p>
     */
    Connection getPooledConnection() throws SQLException;

    void shutdown();
}
