
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    /**
     * Print user command options
     */
    public static void printMenu() {
        System.out.println("A.  Display the list of movies");
        System.out.println("B.  Purchase tickets");
        System.out.println("C.  Exit");
        System.out.print("Enter choice: ");
    }

    /**
     * Validate inputs given by user
     *
     * @param input
     * @return
     */
    public static boolean isValidMenuOption(String input) {
        return input.length() == 1 && input.matches("A|B|C");
    }

    /**
     * Return valid movie number in the list. Return -1 if invalid
     *
     * @param input
     * @param list
     * @return
     */
    public static int getMovieNumber(String input, List<String> list) {
        if (input.length() == 1 && input.matches("\\d+")) {
            int n = Integer.parseInt(input);
            if (n > 0 && n <= list.size()) {
                return n;
            }
        }
        return -1;
    }

    /**
     * Print movie list
     *
     * @param movies
     */
    public static void printMovieList(List<String> movies) {
        System.out.println("Movie List:");
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + " " + movies.get(i));
        }
        System.out.println();
    }

    /**
     * Get movie list from the server
     *
     * @param out
     * @param in
     * @return
     */
    public static List<String> getMovieList(PrintWriter out, Scanner in) {
        out.append(Integer.toString(PurchaseProtocol.GET_MOVIE_LIST));
        out.println();
        out.flush();
        List<String> movieList = new ArrayList<>();
        while (in.hasNextLine()) {
            String[] movies = in.nextLine().split(",");
            movieList.addAll(Arrays.asList(movies));
            break;
        }
        return movieList;
    }

    /**
     * Check tickets availability
     *
     * @param out
     * @param in
     * @param movieName
     * @param tickets
     * @return
     */
    public static boolean isTicketsAvailable(PrintWriter out, Scanner in, String movieName, int tickets) {
        out.append(Integer.toString(PurchaseProtocol.TICKETS_AVAILABLE)).append(",").append(movieName).append(",").append(Integer.toString(tickets));
        out.println();
        out.flush();
        int response = 0;
        boolean result = false;
        while (in.hasNextLine()) {
            response = Integer.parseInt(in.nextLine());
            break;
        }
        if (response == PurchaseProtocol.SUCCESS) {
            result = true;
        } else if (response == PurchaseProtocol.FAIL) {
            result = false;
        } else if (response == PurchaseProtocol.ERROR) {
            result = false;
        }
        return result;
    }

    /**
     * Book ticket and notify status
     *
     * @param out
     * @param in
     * @param movieName
     * @param tickets
     * @return
     */
    public static boolean bookTickets(PrintWriter out, Scanner in, String movieName, int tickets) {
        out.append(Integer.toString(PurchaseProtocol.BOOK_TICKETS)).append(",").append(movieName).append(",").append(Integer.toString(tickets));
        out.println();
        out.flush();
        int response = 0;
        boolean result = false;
        while (in.hasNextLine()) {
            response = Integer.parseInt(in.nextLine());
            break;
        }
        if (response == PurchaseProtocol.SUCCESS) {
            result = true;
        } else if (response == PurchaseProtocol.FAIL) {
            result = false;
        } else if (response == PurchaseProtocol.ERROR) {
            result = false;
        }
        return result;
    }

    /**
     * Send exit message to server
     *
     * @param out
     * @param in
     */
    public static void exit(PrintWriter out, Scanner in) {
        out.append(Integer.toString(PurchaseProtocol.EXIT));
        out.println();
        out.flush();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args == null || (args.length != 2)) {
            System.out.println("Run as : java Client host port");
            System.exit(-1);
        }

        //Read server information
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        //Input,Output
        PrintWriter out = null;
        Scanner in = null;
        List<String> movieListCached = null;

        try {
            //Get socket connection to server
            Socket socket = new Socket(host, port);
            // Input,output to socket streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());

            while (true) {
                //Print options
                printMenu();
                //Check for input
                Scanner input = new Scanner(System.in);
                while (input.hasNextLine()) {
                    String inputLine = input.nextLine();
                    // Validate menu option
                    if (isValidMenuOption(inputLine)) {
                        switch (inputLine) {
                            //Checking movie list
                            case "A": {
                                //Get the movie list from the server
                                movieListCached = getMovieList(out, in);
                                //Print the list
                                printMovieList(movieListCached);
                                //Print menu
                                printMenu();
                                break;
                            }
                            // Select and buy movie tickets
                            case "B": {

                                String movieName = null;
                                int movieTickets = 0;

                                // Ask movie name
                                System.out.print("Enter the number of the movie in the list: ");
                                while (input.hasNextLine()) {
                                    int n = getMovieNumber(input.nextLine(), movieListCached);
                                    //Validate user input
                                    if (n != -1) {
                                        movieName = movieListCached.get(n - 1);
                                        break;
                                    } else {
                                        System.out.println("Invalid option selected");
                                        System.out.print("Enter the number of the movie in the list: ");
                                    }
                                }
                                //Ask number of tickets
                                System.out.print("Enter the number of tickets: ");
                                while (input.hasNextLine()) {
                                    String line = input.nextLine();
                                    //Validate user input
                                    if (line.matches("\\d+")) {
                                        movieTickets = Integer.parseInt(line);
                                        if (movieTickets < 0) {
                                            System.out.println("Invalid input given");
                                            System.out.print("Enter the number of tickets: ");
                                        } else {
                                            break;
                                        }
                                    } else {
                                        System.out.println("Invalid input given");
                                        System.out.print("Enter the number of tickets: ");
                                    }
                                }

                                //Check whether tickets are available
                                boolean available = isTicketsAvailable(out, in, movieName, movieTickets);
                                if (available) {
                                    System.out.print("Enter 1 to confirm or 2 to cancel: ");
                                    //Ask for confirmation
                                    while (input.hasNextLine()) {
                                        String line = input.nextLine();
                                        if (line.matches("\\d+")) {
                                            int option = Integer.parseInt(line);
                                            if ((option == 1 || option == 2)) {
                                                if (option == 1) {
                                                    // Book tickets if 1
                                                    if (bookTickets(out, in, movieName, movieTickets)) {
                                                        System.out.println(movieTickets + " Tickets booked for " + movieName);
                                                    } else {
                                                        System.out.println(movieTickets + " Tickets not available for " + movieName);
                                                    }
                                                } else {
                                                    // Cancel if 2
                                                    System.out.println(movieTickets + " Tickets cancelled for " + movieName);
                                                }
                                                System.out.println();
                                                printMenu();
                                                break;
                                            } else {
                                                System.out.println("Invalid input given");
                                                System.out.println("Enter 1 to confirm or 2 to cancel: ");
                                            }
                                        } else {
                                            System.out.println("Invalid input given");
                                            System.out.println("Enter 1 to confirm or 2 to cancel: ");
                                        }
                                    }
                                } else {
                                    System.out.print(movieTickets + " Tickets not available for " + movieName);
                                    System.out.println();
                                    printMenu();
                                }
                                break;
                            }
                            case "C": {
                                System.out.println("Thank you for using ticket booking application");
                                System.out.println("Press enter to exit....");
                                while (input.hasNextLine()) {
                                    exit(out, in);
                                    System.exit(0);
                                }
                                break;
                            }
                        }
                    } else {
                        System.out.println("Invalid option selected");
                        printMenu();
                    }
                }
                break;
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Error connecting to client", ex);
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
