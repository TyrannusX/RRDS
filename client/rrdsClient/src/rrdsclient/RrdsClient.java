package rrdsclient;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;

public class RrdsClient {
    public static final int REMOTEFOLDER = 3;
    
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static final int[] fileNum = new int[REMOTEFOLDER];
    private static String incomingMessage;
    private static String serverResponse;
    private static LoginDialog ldialog;
    private static HomeFrame hframe;
    
    public static void main(String[] args) throws IOException {
        ldialog = new LoginDialog(null, true);
        ldialog.setDefaultCloseOperation(LoginDialog.DISPOSE_ON_CLOSE);
        ldialog.setVisible(true);
        
        clientSocket = ldialog.getSocket();
        in = ldialog.getBufferedReader();
        out = ldialog.getPrintWriter();
        
        if(clientSocket != null && in != null && out != null) {
            JOptionPane.showMessageDialog(null,
                "You have successfully logged in!");
            
            // Try getting the number of messages inside
            // 1. Inbox
            // 2. Sent
            // 3. Trash
            for(int i = 0; i < fileNum.length; i++) {
                fileNum[i] = Integer.parseInt(in.readLine());
            }
            
            hframe = new HomeFrame();
            hframe.setDefaultCloseOperation(HomeFrame.DISPOSE_ON_CLOSE);
            
            try {
                for(int x = 0; x < fileNum.length; x++) {
                    for(int y = 0; y < fileNum[x]; y++) {
                        serverResponse = in.readLine();
                        JOptionPane.showMessageDialog(null,
                            serverResponse);
                    }
                }
            }
            catch(IOException e) {
                System.out.println(e);
            }
            
            hframe.setVisible(true);
        }
    } // end main
}
