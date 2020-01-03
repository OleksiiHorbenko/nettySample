package o.horbenko.nnettysample.repository.impl.foodomain;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.config.YmlApplicationProperties;
import o.horbenko.nnettysample.config.yml.YmlDbProperties;
import o.horbenko.nnettysample.domain.FooDomain;
import o.horbenko.nnettysample.repository.FooDomainRepository;
import o.horbenko.nnettysample.repository.PersistanceDataException;
import o.horbenko.nnettysample.repository.impl.NonExistingTableCreator;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Log4j2
public class OrmLiteFooDomainRepository implements FooDomainRepository, NonExistingTableCreator {

    private final Dao<FooDomain, Long> fooDomainDao;
    private final JdbcPooledConnectionSource jdbcPooledConnectionSource;
    private final YmlDbProperties dbProperties;

    public OrmLiteFooDomainRepository() {

        this.dbProperties = YmlApplicationProperties.getInstance().getDb();
        this.jdbcPooledConnectionSource = initPooledConnectionSource();
        this.fooDomainDao = initDao();
        createTableIfNotExists();
    }

    protected JdbcPooledConnectionSource initPooledConnectionSource() {
        try  {

            JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource(
                    dbProperties.getUrl(),
                    dbProperties.getUserName(),
                    dbProperties.getPassword()
            );

            connectionSource.setMaxConnectionsFree(10);
            connectionSource.setCheckConnectionsEveryMillis(0);
            connectionSource.setTestBeforeGet(true);

            return connectionSource;

        } catch (SQLException e) {
            log.error("Unable to create OrmLiteFooDomainRepository. ", e);
            throw new PersistanceDataException(e);
        }
    }

    protected Dao<FooDomain, Long> initDao() {
        try {

            return DaoManager.createDao(jdbcPooledConnectionSource, FooDomain.class);

        } catch (SQLException e) {
            log.error("Unable to create OrmLiteFooDomainRepository. ", e);
            throw new PersistanceDataException(e);
        }
    }


    @Override
    public void createTableIfNotExists() {
        try {
            TableUtils.createTableIfNotExists(
                    jdbcPooledConnectionSource,
                    FooDomain.class
            );
        } catch (SQLException e) {
            log.error("Unable to create FooDomain correlated table. ", e);
            throw new PersistanceDataException(e);
        }
    }

    @Override
    public List<FooDomain> findAll() {
        try {

            return fooDomainDao.queryForAll();

        } catch (SQLException e) {
            log.error("Unable to fetch all FooDomain's. ", e);
            throw new PersistanceDataException(e);
        }
    }

    @Override
    public Optional<FooDomain> findById(Long id) {
        try {

            return Optional.ofNullable(
                    fooDomainDao.queryForId(id)
            );

        } catch (SQLException e) {
            log.error("Unable to fetch  FooDomain by ID. ", e);
            throw new PersistanceDataException(e);
        }
    }

    @Override
    public FooDomain save(FooDomain fooDomainToSave) {
        try {

            fooDomainDao.create(fooDomainToSave);
            return fooDomainToSave;

        } catch (SQLException e) {
            log.error("Unable to save FooDomain. ", e);
            throw new PersistanceDataException(e);
        }
    }


}
