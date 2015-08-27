
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the Box office agent
 *
 * @author Marpina Vinay Kumar
 */
public class Agent implements Runnable {

    private final int index;
    private final Socket socket;
    private final BoxOffice boxoffice;

    /**
     *
     * @param index
     * @param socket
     * @param theater
     */
    public Agent(int index, Socket socket, BoxOffice theater) {
        this.index = index;
        this.socket = socket;
        this.boxoffice = theater;
    }

    /**
     * Convert movieList to string
     *
     * @param movies
     * @return
     */
    private String movieList(Set<String> movies) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (String movie : movies) {
            if (i != movies.size()) {
                sb.append(movie).append(",");
            } else {
                sb.append(movie);
            }
        }
        return sb.toString();

    }

    /**
     * Thread start execute from here
     */
    @Override
    public void run() {
        System.out.println("Processing connection " + index);
        PrintWriter out = null;
        Scanner in = null;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            //Wait for incoming requests
            ReadMessage:
            while (in.hasNextLine()) {
                // Request are of form  "Request,parameter1,parameter2"
                String[] requestTokens = in.nextLine().split(",");
                switch (Integer.parseInt(requestTokens[0])) {
                    //Send movie list
                    case PurchaseProtocol.GET_MOVIE_LIST: {
                        System.out.println("Connection " + index + " - Processing request: GetMovieList");
                        out.println(movieList(boxoffice.getMovies()));
                        break;
                    }
                    //Get movie name and tickets and check availability
                    case PurchaseProtocol.TICKETS_AVAILABLE: {
                        System.out.println("Connection " + index + " - Processing request: TicketsAvailable: " + requestTokens[1] + " " + requestTokens[2]);
                        if (boxoffice.isTicketsAvailable(requestTokens[1], Integer.parseInt(requestTokens[2]))) {
                            out.println(PurchaseProtocol.SUCCESS);
                        } else {
                            out.println(PurchaseProtocol.FAIL);
                        }
                        break;
                    }
                    //Get movie name and tickets and book tickets
                    case PurchaseProtocol.BOOK_TICKETS: {
                        System.out.println("Connection " + index + " - Processing request: BookTickets " + requestTokens[1] + " " + requestTokens[2]);
                        if (boxoffice.issueTicket(requestTokens[1], Integer.parseInt(requestTokens[2]))) {
                            out.println(PurchaseProtocol.SUCCESS);
                        } else {
                            out.println(PurchaseProtocol.FAIL);
                        }
                        break;
                    }
                    //Exit
                    case PurchaseProtocol.EXIT: {
                        System.out.println("Connection " + index + " - Exit");
                        break ReadMessage;
                    }
                }
                out.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }
}
