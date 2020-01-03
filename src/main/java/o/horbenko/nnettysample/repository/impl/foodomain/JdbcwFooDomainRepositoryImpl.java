package o.horbenko.nnettysample.repository.impl.foodomain;

import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.domain.FooDomain;
import o.horbenko.nnettysample.repository.FooDomainRepository;
import o.horbenko.nnettysample.repository.PersistanceDataException;
import o.horbenko.nnettysample.repository.impl.NonExistingTableCreator;
import o.horbenko.nnettysample.repository.jdbcw.DbConnectionPool;
import o.horbenko.nnettysample.repository.jdbcw.JdbcTemplate;
import o.horbenko.nnettysample.repository.jdbcw.PreparedStatementSpec;
import o.horbenko.nnettysample.repository.jdbcw.ResultSetMapper;
import o.horbenko.nnettysample.repository.jdbcw.connpool.H2ConnectionPool;
import o.horbenko.nnettysample.repository.jdbcw.template.JdbcTemplateImpl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Log4j2
public class JdbcwFooDomainRepositoryImpl implements FooDomainRepository, NonExistingTableCreator {

    private static final String TABLE = "key_pair";
    private static final String COL_ID = "id";
    private static final String COL_CREATED_AT = "created_at";

    private final JdbcTemplate jdbcTemplate;

    public JdbcwFooDomainRepositoryImpl() {

        DbConnectionPool connectionPool = H2ConnectionPool.getInstance();
        this.jdbcTemplate = new JdbcTemplateImpl(connectionPool);
        createTableIfNotExists();
    }

    @Override
    public void createTableIfNotExists() {
        jdbcTemplate.executeUpdate(createTableIfNotExists_preparedStatementSpec);
    }

    @Override
    public List<FooDomain> findAll() {
        return jdbcTemplate.executeQueryAndMapResultSet(
                findAll_preparedStatementSpec,
                keyPairResultSetMapper
        );
    }

    @Override
    public Optional<FooDomain> findById(Long id) {
        List<FooDomain> resultList = jdbcTemplate.executeQueryAndMapResultSet(
                new FindByIdPreparedStatementSpec(id),
                keyPairResultSetMapper
        );

        switch (resultList.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(resultList.get(0));
            default:
                throw new PersistanceDataException("Not unique result for " + SQL_FIND_BY_ID + ". Actual rows count = " + resultList.size());
        }
    }


    @Override
    public FooDomain save(FooDomain fooDomain) {

        Long generatedId = jdbcTemplate.executeAndMapGeneratedKeys(
                save_preparedStatementSpec(fooDomain),
                saveMapper
        );

        fooDomain.setId(generatedId);
        return fooDomain;
    }


    // ---------- FIND ALL -----------]
    private static final String SQL_FIND_ALL = "SELECT * FROM " + TABLE;

    private PreparedStatementSpec findAll_preparedStatementSpec = new PreparedStatementSpec() {
        @Override
        public String getSql() {
            return SQL_FIND_ALL;
        }

        @Override
        public void setSqlArgumentsIn(PreparedStatement ps) {
        }
    };

    // ---------- FIND_BY_ID ----------
    private static final String SQL_FIND_BY_ID =
            "SELECT * FROM " + TABLE + " WHERE " + COL_ID + " =?";

    private class FindByIdPreparedStatementSpec implements PreparedStatementSpec {

        private final long id;

        private FindByIdPreparedStatementSpec(long id) {
            this.id = id;
        }

        @Override
        public String getSql() {
            return SQL_FIND_BY_ID;
        }

        @Override
        public void setSqlArgumentsIn(PreparedStatement ps) throws SQLException {
            ps.setLong(1, id);
        }
    }

    private ResultSetMapper<FooDomain> keyPairResultSetMapper = rs ->
            FooDomain.builder()
                    .id(rs.getLong(COL_ID))
                    .createdAt(rs.getTimestamp(COL_CREATED_AT).toInstant())
                    .build();

    // --------- CREATE_TABLE ---------
    protected static final String SQL_CREATE_TABLE_INF_NOT_EXIST =
            "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                    COL_ID + " BIGINT IDENTITY PRIMARY KEY, " +
                    COL_CREATED_AT + " TIMESTAMP)";

    private PreparedStatementSpec createTableIfNotExists_preparedStatementSpec = new PreparedStatementSpec() {

        @Override
        public String getSql() {
            return SQL_CREATE_TABLE_INF_NOT_EXIST;
        }

        @Override
        public void setSqlArgumentsIn(PreparedStatement ps) {
        }
    };

    // --------- SAVE ---------
    private static final String SQL_SAVE = "INSERT INTO " + TABLE + " (" + COL_CREATED_AT + ") values (?) ";

    private PreparedStatementSpec save_preparedStatementSpec(FooDomain toSave) {
        return new PreparedStatementSpec() {

            @Override
            public String getSql() {
                return SQL_SAVE;
            }

            @Override
            public void setSqlArgumentsIn(PreparedStatement ps) throws SQLException {
                ps.setTimestamp(1,
                        Timestamp.from(toSave.getCreatedAt()));
            }
        };
    }

    private ResultSetMapper<Long> saveMapper = rs -> rs.getLong(COL_ID);


}