import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Echoclient {
    public static void main(String[] args) {
        Scanner userInputScanner = new Scanner(System.in);
        System.out.println("Enter the server's IP address: ");
        String serverIP = userInputScanner.nextLine();
        System.out.println("Enter the server's port number: ");
        int serverPort = userInputScanner.nextInt();
        //Tries connecting to the server using user inputted IP and port
        try (
            Socket socket = new Socket(serverIP, serverPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            //On success
            System.out.println("Connected to the server.");
            String userInput;
            //While loop starts the connection
            while (true) {
                System.out.println("Enter a message (type 'quit' to exit): ");
                //allows user to text to server
                userInput = userInputReader.readLine();
                out.println(userInput);
                //command to quit
                if ("quit".equalsIgnoreCase(userInput)) {
                    break;
                }
                //reads from the BufferedReader in, to see the server's response
                String serverResponse = in.readLine();
                System.out.println("Server response: " + serverResponse);
            }
            //Catch errors relating to connection failures/etc.
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
