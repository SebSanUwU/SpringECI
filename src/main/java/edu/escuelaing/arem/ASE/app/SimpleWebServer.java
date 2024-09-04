package edu.escuelaing.arem.ASE.app;


import edu.escuelaing.arem.ASE.app.controller.UserController;
import edu.escuelaing.arem.ASE.app.controller.WebController;
import edu.escuelaing.arem.ASE.app.service.StaticService;
import edu.escuelaing.arem.ASE.app.tools.*;
import edu.escuelaing.arem.ASE.app.service.UserService;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;



public class SimpleWebServer {
    private static final int PORT = 8080; //corre por este puerto
    public static UserService userService = new UserService();
    public static StaticService staticService = new StaticService();
    public static Map<String, RESTService> staticServiceTable = StaticService.getServices();

    // Crear un mapa para almacenar todos los controladores disponibles
    public static Map<String, Method> controllersREST = new HashMap<>();

    public static void main(String[] args) throws IOException {
        //PATH DEV
        staticService.staticfiles("webroot");

        //Definir las clase de los controladores
        Class<?>[] controllers = {UserController.class, WebController.class};

        //Cargar Componetnes
        for (Class<?> c : controllers) {
            if (c.isAnnotationPresent(RestController.class)) {
                Method[] methods = c.getDeclaredMethods();
                System.out.println("Controller "+c.getSimpleName()+" enable.");
                for (Method method : methods) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        String key = method.getAnnotation(GetMapping.class).value();
                        controllersREST.put(key, method);
                    }
                }
            }
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(10); // Crea un "Pool de hilos" de hilos a procesor
        ServerSocket serverSocket = new ServerSocket(PORT); // crea un SererSocket con el puerto
        while (true) {
            Socket clientSocket = serverSocket.accept(); // Espera el llamado de alguna peticion cliente
            threadPool.submit(new ClientHandler(clientSocket)); // AL cliente le asigna el socketCLiente y lo pone a correr
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // Saca caracteres
             BufferedOutputStream dataOut = new BufferedOutputStream(clientSocket.getOutputStream())) {

            String requestLine = in.readLine(); // Lee la primera línea de la solicitud.
            if (requestLine == null) return;

            String[] tokens = requestLine.split(" ");
            if (tokens.length < 3) return;

            String method = tokens[0];
            String requestedResource = tokens[1]; // Obtiene el recurso solicitado.
            String basePath = requestedResource.split("\\?")[0]; // Extrae el camino base.

            printRequestHeader(requestLine,in);

            System.out.println(basePath);
            System.out.println(requestedResource);

            if (method.equals("GET")) {
                if (basePath.startsWith("/api")){
                    basePath = basePath.replaceAll("/api","");
                    handleAPIGet(basePath,requestedResource,out,dataOut);
                }else {
                    handleGetRequest(requestedResource,out,dataOut);
                }
            }  else {
                sendErrorResponse(out,new Response<>("text/html","501","Not Implemented",null,null));
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("BOOM");
            e.printStackTrace();
        }
    }

    private void printRequestHeader(String requestLine, BufferedReader in) throws IOException {
        System.out.println("Request Line: " + requestLine);
        String inputLine = "";
        while ((inputLine = in.readLine()) != null) {
            if( !in.ready()) {
                break;
            }
            System.out.println("Header: " + inputLine);
        }
    }

    private void handleGetRequest(String basePath, PrintWriter out, BufferedOutputStream dataOut) throws IOException, InvocationTargetException, IllegalAccessException {
        Response<?> response;
        if (basePath.endsWith(".css") || basePath.endsWith(".js") || basePath.endsWith(".png")) {
            response = SimpleWebServer.staticService.getResource(basePath);
        }else {
            Method methodService = SimpleWebServer.controllersREST.get(basePath);
            if (methodService == null){
                sendErrorResponse(out,new Response<>("text/html","404","Not Found",null,null));
            }
            response = (Response<?>) methodService.invoke(null);
        }
        System.out.println(response);
        if (response.getData() != null) {
            sendOkResponse(out,response,Integer.parseInt((String) response.getData()));
            byte[] fileData = response.getFileData();
            dataOut.write(fileData, 0, Integer.parseInt((String) response.getData()));
            dataOut.flush();
        } else {
            sendErrorResponse(out,response);
        }
    }

    private void handleAPIGet(String basePath, String query, PrintWriter out, BufferedOutputStream dataOut) throws IOException, InvocationTargetException, IllegalAccessException {
        Method methodService = SimpleWebServer.controllersREST.get(basePath);
        if (methodService == null){
            sendErrorResponse(out,new Response<>("text/html","404","Not Found",null,null));
        }
        Request request = new Request(query);
        Parameter[] parameter = methodService.getParameters();
        Object[] args =resolveArguments(parameter,request.parseQuery());
        Response<?> response = (Response<?>) methodService.invoke(null,args);
        if (response.getData() != null){
            String data = response.getData().toString();
            sendOkResponse(out,response,data.length());
            dataOut.write(data.getBytes());
            dataOut.flush();
        }else {
            sendErrorResponse(out,response);
        }
    }

    private void sendOkResponse(PrintWriter out,Response<?> response,int fileLength){
        out.println("HTTP/1.1 "+response.getCodeResponse()+" "+response.getStatusText());
        out.println("Content-type: " + response.getContentType());
        out.println("Content-length: " + fileLength);
        out.println();
        out.flush();
    }

    private void sendErrorResponse(PrintWriter out,Response<?> response){
        out.println("HTTP/1.1 "+response.getCodeResponse()+" "+response.getStatusText());
        out.println("Content-type: " + response.getContentType());
        out.println();
        out.flush();
        out.println("<html><body><h1>"+response.getCodeResponse()+" "+response.getStatusText()+"</h1></body></html>");
        out.flush();
    }

    private Object[] resolveArguments(Parameter[] parameters, Map<String, String> queryParams) {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                String paramValue = queryParams.get(requestParam.value());
                Class<?> paramType = parameter.getType();
                if (paramType == int.class) {
                    args[i] = Integer.parseInt(paramValue);
                } else if (paramType == double.class) {
                    args[i] = Double.parseDouble(paramValue);
                } else {
                    args[i] = paramValue;
                }
            }
        }
        return args;
    }
}