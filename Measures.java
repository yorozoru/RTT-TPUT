import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Arrays;

public class Measures{
    public static void main(String[] args) throws IOException, InterruptedException{
        //Establishing Server configuations from user
        System.out.println("What port number?");
        Scanner userData = new Scanner(System.in);
        int userInput = userData.nextInt();
        ServerSocket original = new ServerSocket(userInput);
        System.out.println("Currently Listening...");
        Socket clientAccept = original.accept();
        System.out.println("Connection Established");
        PrintWriter out = new PrintWriter(clientAccept.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientAccept.getInputStream()));
        String inputLine;
        //Flag if server is in setup mode or measure mode
        boolean setup_mode = false;
        boolean measure_mode = false;

        //Declaring variables for data processing
        int probeSize = 0;
        int prev_probe = 0;
        int msgSize = 0;
        int delay = 0;
        //While loop reads each input from the client
        while ((inputLine = in.readLine()) != null) {
        String[] current_process = inputLine.split(" ");
        System.out.println(Arrays.toString(current_process));
        //Determines if received data is valid for processing
        //If valid setup
        if (current_process[0].equals("s") && setup_mode == false){
            //Checks if all fields are valid according to the protocol
            if ((current_process[1].equals("rtt") || current_process[1].equals("tput"))&&(Integer.parseInt(current_process[2]) >= 10) && (Integer.parseInt(current_process[3])>=1 && Integer.parseInt(current_process[3])<=32000) && (Integer.parseInt(current_process[4])>=0)){
                setup_mode = true;
                probeSize = Integer.parseInt(current_process[2]);
                msgSize = Integer.parseInt(current_process[3]);
                delay = Integer.parseInt(current_process[4]);
                out.println("200 OK: Ready");
            } else {
                out.println("404 ERROR: Invalid Connection Setup Message");
                in.close();
            }
        //If valid measurment structure
        }else if (current_process[0].equals("m") && setup_mode == true && (current_process[2].length()<= msgSize) && (current_process[2].length()>= 1)){
            int current_probe = Integer.parseInt(current_process[1]);
            measure_mode = true;
            //Keeps tracks of probes to see if they are going in sequential order
            if ((current_probe > prev_probe) && (current_probe <= probeSize) && (Integer.parseInt(current_process[1])==prev_probe+1)){
                Thread.sleep(delay);
                out.println(inputLine);
                prev_probe = current_probe;
            }else{
                out.println("404 ERROR: Invalid Measurement Message");
                in.close();
            }
        //If valid termination process
        }else if (current_process[0].equals("t")){
            out.println("200 OK: Closing Connection");
            in.close();
        //More error handling if the conditionals above did not catch
        } else {
            if (setup_mode == false){
                out.println("404 ERROR: Invalid Connection Setup Message");
                in.close();
            } else if ((current_process[0].equals("m")) && (current_process[2].length() != msgSize) || measure_mode == true) {
                out.println("404 ERROR: Invalid Measurement Message");
                in.close();
            } else {
                out.println("404 ERROR: Invalid Connection Termination Message");
                in.close();
            }
        
        }
        //Reset system for next requests
        if ((clientAccept.isClosed() == true)||prev_probe == probeSize){
        measure_mode = false;
        setup_mode = false;
        probeSize = 0;
        prev_probe = 0;
        msgSize = 0;
        delay = 0;
        }
    }
    //Shutting down system
    clientAccept.close();
    userData.close();
    original.close();
 }
}