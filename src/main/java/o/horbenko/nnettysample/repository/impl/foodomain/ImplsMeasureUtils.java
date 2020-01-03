package o.horbenko.nnettysample.repository.impl.foodomain;

import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.domain.FooDomain;
import o.horbenko.nnettysample.repository.FooDomainRepository;
import o.horbenko.nnettysample.utils.SimpleMeasureUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Deprecated
@Log4j2
public class ImplsMeasureUtils {

    public static void main(String[] args) {

        measureImpl(new JdbcwFooDomainRepositoryImpl());
        long averageExecutionTime_customJdbc = measureImpl(new JdbcwFooDomainRepositoryImpl());

        measureImpl(new OrmLiteFooDomainRepository());
        long averageExecutionTime_ORMLite = measureImpl(new OrmLiteFooDomainRepository());

        log.debug(" --- RESULTS ---");
        log.debug("Custom JDBC average execution time is {} millis", averageExecutionTime_customJdbc);
        log.debug("ORMLite     average execution time is {} millis", averageExecutionTime_ORMLite);
    }


    /**
     * // act and measure
     * FooDomain saved = impl.save(toSave);
     * impl.findAll();
     * int threadsCount = 20;
     * int operationsPerThread = 500;
     * RESULTS
     * Custom JDBC average execution time is 70 millis
     * ORMLite     average execution time is 152 millis
     */
    private static long measureImpl(FooDomainRepository impl) {
        // for test
        int threadsCount = 20;
        int operationsPerThread = 500;

        long averageExecutionTime = SimpleMeasureUtils.measure(
                threadsCount,
                operationsPerThread,
                () -> {
                    int randomValue = UUID.randomUUID().hashCode();
                    return FooDomain.builder()
                            .createdAt(Instant.now().plus(randomValue, ChronoUnit.MILLIS))
                            .timestamp(Timestamp.from(Instant.now().plus(randomValue, ChronoUnit.MILLIS)))
                            .build();
                },
                (toSave) -> {
                    // act and measure
                    FooDomain saved = impl.save(toSave);
                    impl.findAll();
                }
        );

        return averageExecutionTime;
    }

}
