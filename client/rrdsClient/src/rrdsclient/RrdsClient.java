package rrdsclient;

import java.net.Socket;
import java.io.*;
import javax.swing.JFrame;

public class RrdsClient {
    private static Socket clientSocket;
    private static final String domainName = "penguin.tamucc.edu";
    private static final int portNumber = 9090;
    private static BufferedReader in;
    private static PrintWriter out;
    private static String incomingMessage;
    private static String username;
    private static String password;
    
    public static void main(String[] args) {
        LoginFrame lframe = new LoginFrame();
        lframe.setVisible(true);
        
//        HomeFrame frame = new HomeFrame();
//        frame.setVisible(true);

    } // end main
}
