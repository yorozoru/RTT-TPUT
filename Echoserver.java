import java.io.*;
import java.net.*;
import java.util.*;

public class Echoserver{
    public static void main(String[] args) throws IOException{
        //Allows port number to be set
        System.out.println("What port number?");
        Scanner userData = new Scanner(System.in);
        int userInput = userData.nextInt();
        //Setting up connections
        ServerSocket original = new ServerSocket(userInput);
        System.out.println("Currently Listening...");
        Socket clientAccept = original.accept();
        System.out.println("Connection Established");
        //Allow for incoming and outgoing packets
        PrintWriter out = new PrintWriter(clientAccept.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientAccept.getInputStream()));
        String inputLine;
        //echo logic
        while ((inputLine = in.readLine()) != null) {
        if ("quit".equals(inputLine)) {
            out.println("Quitting");
            break;
         };
         out.println(inputLine);
         System.out.println(inputLine);
    }
    userData.close();
    original.close();
 }
}