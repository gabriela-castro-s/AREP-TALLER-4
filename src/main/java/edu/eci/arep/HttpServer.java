package edu.eci.arep;

import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.eci.arep.Spark.*;
import edu.eci.arep.Services.*;
import edu.eci.arep.Services.FileReader;

public final class HttpServer {
    private static HttpServer instance;

    /**
     * Instanciamiento del servidor web
     * @return instancia del servidor
     */
    public static HttpServer getInstance() {
        if (instance == null) {
            instance = new HttpServer();
        }
        return instance;
    }

    /**
     * Inicia un servidor socket http, junto a unos servicios determinados
     * @param args
     * @param services mapa de servicios que vamos a utilizar
     * @throws IOException
     */
    public void run(String[] args, Map<String, Service> services) throws IOException, Exception {
        System.out.println("Server running ...");
        ServerSocket serverSocket = null;
        Class<?> c = Class.forName(args[0]);
        Class[] argTypes = new Class[]{String[].class};
        Method main = c.getDeclaredMethod("main", argTypes);
        String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
        System.out.format("invoking %s.main()%n", c.getName());
        main.invoke(null, (Object) mainArgs);
        HashMap<String, Method> service = new HashMap<>();
        for (String a : args){
            for (Method m : Class.forName(a).getMethods()) {
                if (m.isAnnotationPresent(Run.class)) {
                    m.invoke(null);
                    //service.put(m.getAnnotation().getValue());
                }
            }
        }
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

            String headerLine = "";
            while ((inputLine = in.readLine())!= null){
                if (inputLine.length() == 0){
                    break;
                }
                headerLine += inputLine + "\r\n";
            }

            StringBuilder body = new StringBuilder();
            while(in.ready()){
                body.append((char) in.read());
            }

            Request request = new Request(headerLine, body.toString());
            String uri = request.getUri();
            Service servicio = new NotFoundService();
            if (Spark.isMapped(uri)) {
                Object response = Spark.getGet(uri, request, null);
                if (response instanceof Service) {
                    servicio = (Service) response;
                }
            }
            if (uri.contains("/file/")) {
                servicio = new FileReader(uri);
            }
            outputLine = servicio.getHeader() + servicio.getBody();
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
        System.out.println("Server off");
    }
}