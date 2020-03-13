package ru.kononov.quotationservice.controller;

import com.sun.net.httpserver.HttpExchange;
import lombok.extern.log4j.Log4j2;
import ru.kononov.quotationservice.util.ResponseWriter;

import java.net.HttpURLConnection;

@Log4j2
public class SwaggerController extends HttpController {

    public SwaggerController(String uri) {
        super(uri);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String point = "SwaggerController.handle";
        String contentType = "text/yaml;charset=UTF-8";
        if ("GET".equals(exchange.getRequestMethod())) {
            byte[] resource;
            try {
                resource = getClass().getResourceAsStream("/openapi/self/quotation-service.yaml").readAllBytes();
            } catch (Exception e) {
                throw new RuntimeException("Не удалось получить файл 'openapi/self/quotation-service.yaml'", e);
            }
            ResponseWriter.writeResponse(log, point, exchange, () -> resource, HttpURLConnection.HTTP_OK, contentType);
        } else {
            ResponseWriter.writeResponse(log, point, exchange, () -> new byte[]{}, HttpURLConnection.HTTP_NOT_IMPLEMENTED, contentType);
        }
    }

}
