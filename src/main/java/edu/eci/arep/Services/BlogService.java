package edu.eci.arep.Services;

public class BlogService implements Service{
    private static BlogService instance = null;

    String message = "Blog Service:";

    public static BlogService getInstance() {
        if (instance == null) {
            instance = new BlogService();
        }
        return instance;
    }

    @Override
    public String getBody() {
        return message;
    }

    @Override
    public String getHeader() {
        return "HTTP/1.1 200 OK\r\n"
                + "Access-Control-Allow-Origin: *\r\n"
                + "Content-Type:text/plain \r\n"
                + "\r\n";
    }


    public void addToMessage(String message){
        this.message += message;
    }

}