package o.horbenko.nnettysample.service;

import io.r2dbc.h2.H2Connection;
import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import reactor.core.publisher.Mono;

public class DbService {

    public static void main(String... args) {

        String h2url = "";
        DbService dbService = new DbService();


    }

    private final H2ConnectionFactory h2ConnectionFactory;

    public DbService() {
        this.h2ConnectionFactory = initH2ConnectionFactory();

        Mono<H2Connection> connectionMono = h2ConnectionFactory.create();
//        connectionMono.flatMap(c -> {
//            c
//                    .beginTransaction()
//                    .then()
//
//
//        });
    }

    private H2ConnectionFactory initH2ConnectionFactory() {
        return new H2ConnectionFactory(
                H2ConnectionConfiguration.builder()
//                        .file()
//                        .username()
//                        .password()
                        .build()
        );
    }


}
