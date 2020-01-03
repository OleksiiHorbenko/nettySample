package o.horbenko.nnettysample.repository.jdbcw;

import java.util.List;

public interface JdbcTemplate {


    /**
     * For SQL queries without return types.
     * OPERATIONS: insert/update/delete
     */
    void executeUpdate(PreparedStatementSpec spec);

    /**
     * For save operations with generated keys.
     * OPERATIONS: select/insert/update/delete
     */
    <Domain>
    Domain executeAndMapGeneratedKeys(PreparedStatementSpec spec,
                                      ResultSetMapper<Domain> mapper);

    /**
     * For select operations only.
     * OPERATIONS: select
     */
    <Domain>
    List<Domain> executeQueryAndMapResultSet(PreparedStatementSpec spec,
                                             ResultSetMapper<Domain> mapper);
}
