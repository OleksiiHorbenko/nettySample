package o.horbenko.nnettysample.config.yml;

import lombok.Data;

@Data
public class YmlServerProperties {
    private int port;
    private int blockingThreadsCount;
}
