package o.horbenko.nnettysample.repository.jdbcw;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Used for initializing of concrete {@link PreparedStatement}
 * */
public interface PreparedStatementSpec {

    String getSql();

    void setSqlArgumentsIn(PreparedStatement ps) throws SQLException;

}
