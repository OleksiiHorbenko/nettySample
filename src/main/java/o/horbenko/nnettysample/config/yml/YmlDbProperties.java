package o.horbenko.nnettysample.config.yml;

import lombok.Data;

@Data
public class YmlDbProperties {

    private String url;
    private String userName;
    private String password;
    private int maxPooledConnectionsCount;

}
