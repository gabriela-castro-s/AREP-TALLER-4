package edu.eci.arep;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.eci.arep.Spark.*;
import edu.eci.arep.Services.*;
import edu.eci.arep.SpringBoot.Component;


public class App {
    static HttpServer httpServer = HttpServer.getInstance();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        httpServer.run();
    }
}