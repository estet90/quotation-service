package ru.kononov.quotationservice.server;

import com.sun.net.httpserver.HttpServer;
import lombok.extern.log4j.Log4j2;
import ru.kononov.quotationservice.controller.ElvlsController;
import ru.kononov.quotationservice.controller.HttpController;
import ru.kononov.quotationservice.util.PropertyResolver;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;

@Singleton
@Log4j2
public class Server {

    private final ElvlsController elvlsController;
    private final PropertyResolver propertyResolver;

    @Inject
    public Server(ElvlsController elvlsController,
                  PropertyResolver propertyResolver) {
        this.elvlsController = elvlsController;
        this.propertyResolver = propertyResolver;
    }

    public void start() throws IOException {
        var start = LocalDateTime.now();
        var inetSocketAddress = new InetSocketAddress(propertyResolver.getIntProperty("server.port"));
        var server = HttpServer.create(inetSocketAddress, 0);
        var executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
        addHandlers(server);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Server is shutting down...");
            server.stop(0);
        }));
        server.start();
        log.info("Server started at {} millis", start.until(LocalDateTime.now(), ChronoUnit.MILLIS));
    }

    private void addHandlers(HttpServer server) {
        createContextWithFilter(server, elvlsController);
//        server.createContext(contextPath + "/swagger", httpController::swagger);
    }

    private void createContextWithFilter(HttpServer server, HttpController httpController) {
        var context = server.createContext(httpController.getUri(), httpController);
        context.getFilters().add(new LoggingFilter());
    }

}
