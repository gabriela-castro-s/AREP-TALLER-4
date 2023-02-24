package edu.eci.arep;

import java.net.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.eci.arep.Spark.*;
import edu.eci.arep.Services.*;
import edu.eci.arep.SpringBoot.*;


public final class HttpServer {
    private static HttpServer instance;


    public static HttpServer getInstance() {
        if (instance == null) {
            instance = new HttpServer();
        }
        return instance;
    }

    public void run() throws IOException, ClassNotFoundException {
        Map<String, Method> methods = new HashMap<>();
        MicroSpringBoot msb = new MicroSpringBoot();
        ArrayList<String> componentNames = msb.getComponents(new ArrayList<>(), ".");
        for (String componentName : componentNames) {

            Class c = Class.forName(componentName);
            for (Method method : c.getMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    String path = method.getAnnotationsByType(RequestMapping.class)[0].value();
                    methods.put(path, method);
                }
            }
        }
        System.out.println("Server running...");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);

        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {

            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;

            // Proceso de lectura de los datos header y body de una solicitud
            String headerLine = "";
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.length() == 0) {
                    break;
                }
                headerLine += inputLine + "\r\n";
            }
            StringBuilder body = new StringBuilder();
            while (in.ready()) {
                body.append((char) in.read());
            }

            // Proceso de procesamiento de las solicitudes get a los pojo component
            Request request = new Request(headerLine, body.toString());
            Service service = new NotFoundService();
            try {
                if (request.getUri().contains("file")){
                    service = (Service) methods.get("/file/").invoke(null, request.getUri());
                }
                else if (methods.containsKey(request.getUri())) {
                    service = (Service) methods.get(request.getUri()).invoke(null);
                }

            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
            outputLine = service.getHeader() + service.getBody();
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();

        }
        serverSocket.close();
        System.out.println("Server off.");
    }
}