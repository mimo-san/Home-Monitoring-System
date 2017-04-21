package JavaApplication1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;
import java.net.*;

/**
 * This is a simple server application. This server receive a sensor values
 * from the Android mobile phone and show it on the console.
 */
public class SensorServer {

    private static ServerSocket serverSocket;
    private static Socket socket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String message;
//    
    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(8080); // Server socket
           
        } catch (IOException e) {
            System.out.println("Could not listen on port: 8080");
        }

        System.out.println("Server started. Listening to the port 8080");

        while (true) {
            try {
                
                socket = serverSocket.accept(); // accept the client connection
               
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader); // get the client message
                message = bufferedReader.readLine();
                System.out.println(message);
                socket.close();
                
                
               
                //inputStreamReader.close();
                //serverSocketAcc.close();
                //serverSocketLight.close();
                //serverSocketTemp.close();
            } catch (IOException ex) {
                System.out.println("Problem in message reading");
            }
        

        }
    }
}



