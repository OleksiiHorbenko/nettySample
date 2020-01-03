package o.horbenko.nnettysample.repository.jdbcw.template;

import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.repository.PersistanceDataException;
import o.horbenko.nnettysample.repository.jdbcw.DbConnectionPool;
import o.horbenko.nnettysample.repository.jdbcw.JdbcTemplate;
import o.horbenko.nnettysample.repository.jdbcw.PreparedStatementSpec;
import o.horbenko.nnettysample.repository.jdbcw.ResultSetMapper;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Log4j2
public class JdbcTemplateImpl implements JdbcTemplate {

    private final DbConnectionPool connectionPool;

    public JdbcTemplateImpl(DbConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void executeUpdate(PreparedStatementSpec spec) {

        try (Connection connection = connectionPool.getPooledConnection();
             PreparedStatement ps = connection.prepareStatement(spec.getSql(), Statement.RETURN_GENERATED_KEYS)
        ) {

            // set PS arguments
            spec.setSqlArgumentsIn(ps);

            // execute
            ps.executeUpdate();

        } catch (SQLException e) {
            log.error("Unable to executeUpdate. ", e);
            throw new PersistanceDataException(e);
        }
    }

    @Override
    public <Domain>
    Domain executeAndMapGeneratedKeys(PreparedStatementSpec spec,
                                      ResultSetMapper<Domain> mapper) {

        try (Connection connection = connectionPool.getPooledConnection();
             PreparedStatement ps = connection.prepareStatement(spec.getSql(), Statement.RETURN_GENERATED_KEYS)
        ) {

            // set PS arguments
            spec.setSqlArgumentsIn(ps);

            // execute on DB
            ps.execute();

            // map result values
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Domain result = mapper.map(generatedKeys);
                    log.debug("Domain saved into DB.");
                    return result;
                }
            }

            throw new PersistanceDataException("Unable to map generated object ID");
        } catch (SQLException e) {
            log.error("Unable to save domain. ", e);
            throw new PersistanceDataException(e);
        }
    }

    @Override
    public <Domain>
    List<Domain> executeQueryAndMapResultSet(PreparedStatementSpec spec,
                                             ResultSetMapper<Domain> mapper) {

        try (Connection connection = connectionPool.getPooledConnection();
             PreparedStatement ps = connection.prepareStatement(spec.getSql())
        ) {

            // set SQL attributes
            spec.setSqlArgumentsIn(ps);

            // execute and get ResultSet
            try (ResultSet rs = ps.executeQuery()) {

                LinkedList<Domain> result = new LinkedList<>();

                // map each row
                while (rs.next()) {
                    result.add(mapper.map(rs));
                }

                return result;
            }

        } catch (SQLException e) {
            log.error("Unable to find items.", e);
            throw new PersistanceDataException(e);
        }
    }

}
