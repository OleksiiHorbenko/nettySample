package o.horbenko.nnettysample.repository.impl.foodomain;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.h2.H2Result;
import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.config.YmlApplicationProperties;
import o.horbenko.nnettysample.config.yml.YmlDbProperties;
import o.horbenko.nnettysample.domain.FooDomain;
import o.horbenko.nnettysample.repository.FooDomainRepository;
import o.horbenko.nnettysample.repository.impl.NonExistingTableCreator;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Log4j2
public class R2dbcFooDomainRepository implements FooDomainRepository, NonExistingTableCreator {

    public static void main(String[] args) {
        FooDomainRepository impl = new R2dbcFooDomainRepository();
    }


    private static final String DB_FILE_PATH = "~/Downloads/test";

    private final H2ConnectionFactory connectionFactory;
    private final YmlDbProperties dbProperties;

    public R2dbcFooDomainRepository() {
        this.dbProperties = YmlApplicationProperties.getInstance().getDb();

        this.connectionFactory = new H2ConnectionFactory(
                H2ConnectionConfiguration.builder()
                        .file(DB_FILE_PATH)
                        .username(dbProperties.getUserName())
                        .password(dbProperties.getPassword())
                        .build()
        );

        createTableIfNotExists();
        log.debug("Initialized and ready to work.");
    }


    @Override
    public void createTableIfNotExists() {
        connectionFactory
                .create()
                .flatMap(dbconn -> dbconn.beginTransaction()
                        .then(Mono.from(dbconn
                                .createStatement(JdbcwFooDomainRepositoryImpl.SQL_CREATE_TABLE_INF_NOT_EXIST)
                                .execute()))
                        .delayUntil(r -> dbconn.commitTransaction())
                        .doFinally((st) -> dbconn.close()))
                .subscribe();
    }

    @Override
    public List<FooDomain> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<FooDomain> findById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FooDomain save(FooDomain fooDomainToSave) {
        throw new UnsupportedOperationException();
    }
}
