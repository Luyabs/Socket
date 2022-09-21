package console_menu;

public class Entrance {
    public static void main(String[] args) {
        Thread server = new SocketServerCopy(6789);
        Thread client = new SocketClientCopy("localhost", 6789);
        server.start();
        client.start();
    }
}
