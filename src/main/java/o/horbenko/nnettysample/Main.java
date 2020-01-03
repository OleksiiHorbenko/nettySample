package o.horbenko.nnettysample;

import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.config.YmlApplicationProperties;
import o.horbenko.nnettysample.repository.jdbcw.DbConnectionPool;
import o.horbenko.nnettysample.repository.jdbcw.connpool.H2ConnectionPool;

@Log4j2
public class Main {

    public static void main(String[] args) {

        // 1. Parse configuration file (via getInstance call)
        YmlApplicationProperties ymlProps = YmlApplicationProperties.getInstance();

        // 2. Initialize DB connection pool (via getInstance call)
        DbConnectionPool connectionPool = H2ConnectionPool.getInstance();

        long maxJvmMemory = Runtime.getRuntime().maxMemory();
        long availableMemory = Runtime.getRuntime().freeMemory();

        log.debug("Max JVM memory space = {}", maxJvmMemory);
        log.debug("Available Memory = {}", availableMemory);

        // 3. Start server
        new NettyServerBootstrap(
                ymlProps.getServer().getPort(),
                ymlProps.getServer().getBlockingThreadsCount()
        ).runServer();
    }
}
