package o.horbenko.nnettysample.repository.jdbcw;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Used for {@link ResultSet} to {@link Domain} java type.
 * Also used in
 * */
public interface ResultSetMapper<Domain> {

    Domain map(ResultSet rs) throws SQLException;

}
