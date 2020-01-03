package o.horbenko.nnettysample.utils;

import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

@Log4j2
public class YmlParserUtils {


    public static <T>
    T parseYml(Class<T> targetClass,
               String fileName) {

        try (InputStream inputStream = YmlParserUtils.class
                .getClassLoader()
                .getResourceAsStream(fileName)) {

            Constructor constructor = new Constructor(targetClass);
            Yaml yaml = new Yaml(constructor);

            return yaml.loadAs(inputStream, targetClass);

        } catch (IOException e) {
            log.error("Unable to parse '{}' YML file. Error.message={}", fileName, e.getMessage());
            log.trace(e);
            throw new RuntimeException(e);
        }
    }

}
