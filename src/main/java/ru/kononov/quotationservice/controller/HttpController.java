package ru.kononov.quotationservice.controller;

import com.sun.net.httpserver.HttpHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class HttpController implements HttpHandler {

    @Getter
    protected final String uri;

}
