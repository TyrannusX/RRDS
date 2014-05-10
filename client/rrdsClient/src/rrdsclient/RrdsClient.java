package rrdsclient;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;

public class RrdsClient {
    public static final int REMOTEFOLDER = 3;
    
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static String incomingMessage;
    private static String serverResponse;
    private static LoginDialog ldialog;
    private static HomeFrame hframe;
    private static String username;
    
    public static void main(String[] args) throws IOException {
        ldialog = new LoginDialog(null, true);
        ldialog.setDefaultCloseOperation(LoginDialog.DISPOSE_ON_CLOSE);
        ldialog.setVisible(true);
        
        clientSocket = ldialog.getSocket();
        in = ldialog.getBufferedReader();
        out = ldialog.getPrintWriter();
        username = ldialog.getUserName();
        
        if(clientSocket != null && in != null && out != null) {
            JOptionPane.showMessageDialog(null,
                "You have successfully logged in!");
            
            hframe = new HomeFrame(clientSocket, username);
            hframe.setDefaultCloseOperation(HomeFrame.DISPOSE_ON_CLOSE);
            hframe.setVisible(true);
        }
    } // end main
}

