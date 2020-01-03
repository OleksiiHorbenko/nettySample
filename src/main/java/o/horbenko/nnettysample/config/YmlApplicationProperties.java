package o.horbenko.nnettysample.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import o.horbenko.nnettysample.config.yml.YmlDbProperties;
import o.horbenko.nnettysample.config.yml.YmlServerProperties;
import o.horbenko.nnettysample.utils.YmlParserUtils;

import static o.horbenko.nnettysample.config.Constants.APPLICATION_YML_RESOURCE_PATH;

@Data
@Log4j2
public class YmlApplicationProperties {

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static class SingletonHolder {
        private static final YmlApplicationProperties HOLDER_INSTANCE =
                YmlParserUtils.parseYml(YmlApplicationProperties.class, APPLICATION_YML_RESOURCE_PATH);
    }

    public static YmlApplicationProperties getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////


    private YmlServerProperties server;
    private YmlDbProperties db;

    public YmlApplicationProperties() {
        log.debug("Initialized.");
    }
}
