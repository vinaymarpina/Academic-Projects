
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args == null || (args.length != 2)) {
                System.out.println("Run as : java Server full_input_file_path port");
                System.exit(-1);
            }
            // Create a box office
            System.out.println("Creating server: loading movies... ");
            BoxOffice boxOffice = new BoxOffice(args[0]);
            // Port number
            int port = Integer.parseInt(args[1]);
            //Create and start server
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started: listening on port " + port);
            int count = 0;
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection " + ++count + " accepted");
                new Thread(new Agent(count, clientSocket, boxOffice)).start();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
