
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoxOffice {

    private final Map<String, Integer> movieTickets = new HashMap<>();
    // Lock for movie tickets database
    private final Semaphore movieTicketsLock = new Semaphore(1);

    /**
     * Read the ticket information from the input file
     *
     * @param ticketsFile
     * @throws FileNotFoundException
     */
    public BoxOffice(String ticketsFile) throws FileNotFoundException {
        try (Scanner inputReader = new Scanner(new File(ticketsFile))) {
            while (inputReader.hasNextLine()) {
                String line = inputReader.nextLine();
                String[] tokens = line.split("\t");
                movieTickets.put(tokens[0], Integer.parseInt(tokens[1]));
                System.out.println(line);
            }
        }
    }

    /**
     * Gives currently running movies
     *
     * @return
     */
    public Set<String> getMovies() {
        SortedSet<String> movies = new TreeSet();
        try {
            movieTicketsLock.acquire();
            for (Entry<String, Integer> movie : movieTickets.entrySet()) {
                if(movie.getValue()>0){
                    movies.add(movie.getKey());
                }
            }
            movieTicketsLock.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(BoxOffice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return movies;
    }

    /**
     * If required number tickets available for the movie return true and
     * decrement tickets count and return true
     *
     * @param movie
     * @param tickets
     * @return
     */
    public boolean issueTicket(String movie, int tickets) {
        boolean result = true;
        try {
            movieTicketsLock.acquire();
            if (movieTickets.get(movie) < tickets) {
                result = false;
            } else {
                movieTickets.put(movie, movieTickets.get(movie) - tickets);
                System.out.println("Booked Tickets:" + movie + " Remaining:" + movieTickets.get(movie));
            }
            movieTicketsLock.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(BoxOffice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     *
     * @param movie
     * @param tickets
     * @return
     */
    public boolean isTicketsAvailable(String movie, int tickets) {
        boolean result = false;
        try {
            movieTicketsLock.acquire();
            if (movieTickets.get(movie) >= tickets) {
                result = true;
            }
            movieTicketsLock.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(BoxOffice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
