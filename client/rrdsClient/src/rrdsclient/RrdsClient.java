//PROGRAMMERS: Robert Reyes and Darius Stephen

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;

public class RrdsClient {
    public static final int REMOTEFOLDER = 3;
    
    private static Socket clientSocket; //client socket
    private static BufferedReader in; //input stream for socket
    private static PrintWriter out; //output stream for socket
    private static String incomingMessage; //message from input
    private static String serverResponse; //message from output
    private static LoginDialog ldialog; //login dialog window
    private static HomeFrame hframe; //home frame window
    private static String username; //client username
    
    public static void main(String[] args) throws IOException {
        //setup and show login dialog
        ldialog = new LoginDialog(null, true);
        ldialog.setDefaultCloseOperation(LoginDialog.DISPOSE_ON_CLOSE);
        ldialog.setVisible(true);
        
        //grab socket from login
        clientSocket = ldialog.getSocket();
        in = ldialog.getBufferedReader();
        out = ldialog.getPrintWriter();
        username = ldialog.getUserName();
        
        //if the login was successful show the home frame window
        if(clientSocket != null && in != null && out != null) {
            JOptionPane.showMessageDialog(null,
                "You have successfully logged in!");
            
            hframe = new HomeFrame(clientSocket, username);
            hframe.setDefaultCloseOperation(HomeFrame.DISPOSE_ON_CLOSE);
            hframe.setVisible(true);
        }
    } // end main
}

