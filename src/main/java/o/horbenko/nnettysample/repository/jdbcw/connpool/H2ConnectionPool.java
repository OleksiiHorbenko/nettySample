package o.horbenko.nnettysample.repository.jdbcw.connpool;

import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.config.YmlApplicationProperties;
import o.horbenko.nnettysample.config.yml.YmlDbProperties;
import o.horbenko.nnettysample.repository.PersistanceDataException;
import o.horbenko.nnettysample.repository.jdbcw.DbConnectionPool;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Log4j2
public class H2ConnectionPool implements DbConnectionPool {

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static class SingletonHolder {
        private static final H2ConnectionPool HOLDER_INSTANCE = new H2ConnectionPool();
    }

    public static DbConnectionPool getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private final JdbcConnectionPool jdbcConnectionPool;
    private final YmlDbProperties dbProperties;

    /**
     * Uses {@link YmlApplicationProperties#getInstance()} for {@link JdbcConnectionPool} initializing.
     * Get attention with {@link #registerJVMShutdownHook()}
     */
    public H2ConnectionPool() {
        this.dbProperties = YmlApplicationProperties.getInstance().getDb();

        this.jdbcConnectionPool = JdbcConnectionPool.create(
                dbProperties.getUrl(),
                dbProperties.getUserName(),
                dbProperties.getPassword()
        );

        this.jdbcConnectionPool.setMaxConnections(dbProperties.getMaxPooledConnectionsCount());

        this.registerJVMShutdownHook();

        log.debug("Initialized and ready to work.");
    }

    @Override
    public Connection getPooledConnection() throws SQLException {
        return jdbcConnectionPool.getConnection();
    }

    @Override
    public void shutdown() {
        try (Connection connection = jdbcConnectionPool.getConnection();
             Statement stmt = connection.createStatement()
        ) {

            stmt.execute("SHUTDOWN");
            log.debug("Executed SHUTDOWN on H2 connection pool");

        } catch (SQLException e) {
            log.error("Unable to SHUTDOWN jdbc connection pool.", e);
            throw new PersistanceDataException(e);
        }
    }

    protected void registerJVMShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {

                shutdown();

            } catch (Throwable e) {
                // possibly never logged
                log.error("Tried to execute JVM Shutdown Hook. Failed because of ", e);
            }
        }));
    }

}
