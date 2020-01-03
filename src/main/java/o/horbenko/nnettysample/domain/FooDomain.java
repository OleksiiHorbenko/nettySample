package o.horbenko.nnettysample.domain;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity(name = "key_pair")
public class FooDomain {

    @Id
    @GeneratedValue
    private Long id;

    @DatabaseField(columnName = "byte_arr_field", dataType = DataType.BYTE_ARRAY)
    private byte[] byteArrayField;

    @Column(name = "created_at")
    private Timestamp timestamp;

    private Instant createdAt;


}
