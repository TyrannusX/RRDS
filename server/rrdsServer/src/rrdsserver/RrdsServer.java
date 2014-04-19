/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package rrdsserver;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author robertreyes
 */
public class RrdsServer{
    //class variables
    private static ServerSocket serverSocket; //socket used for accepting connections 
    private static Socket clientSocket; //socket used to hold client socket
    private static BufferedReader incomingMessage; //input stream to get client data
    private static PrintWriter outgoingMessage; //output stream to client
    private static String tempStr; //string used for communication
    private static StringTokenizer splitter; //tokenizer
    private static String username; //client username
    private static String password; //client password
    private static FileReader myFile; //file reader to read in credentials, inbox, outbox, etc
    private static Element xmlElement; //xml element
    private static DocumentBuilderFactory xmlFactory; //xml factory
    private static DocumentBuilder xmlBuilder; //xml builder
    private static InputSource xmlSource; //xml source
    private static StringReader xmlReader; //xml reader
    private static Document xmlDoc; //xml document
    
    public static void main(String[] args) {
        String credentialStr = "";
        String sPath = "";
        String sInbox = "";
        String xmlStr = "";
        File userPath;
        File[] userFiles;
        int credentialCounter = 0;
        boolean usernameVerified = false;
        boolean passwordVerified = false;
        
        try{
            //initialize the server socket
            serverSocket = new ServerSocket(8080);
            tempStr = "";

            //while loop to run server
            while(true){
                //accept incoming connections
                clientSocket = serverSocket.accept();
                
                //intialize streams
                incomingMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outgoingMessage = new PrintWriter(clientSocket.getOutputStream(), true);
                
                //read client credentials
                tempStr = incomingMessage.readLine();
                
                //initialize the tokenizer
                splitter = new StringTokenizer(tempStr, "/");
                
                //get the username and password
                username = splitter.nextToken();
                password = splitter.nextToken();
                
                //setup file stream to verify credentials
                myFile = new FileReader("credentials.txt");
                BufferedReader bReader = new BufferedReader(myFile);
                
                //loop through the file to get credentials and verify
                while((credentialStr = bReader.readLine()) != null)
                {
                    if(credentialCounter == 0)
                    {
                        if(username.equals(credentialStr))
                        {
                            usernameVerified = true;
                        }                       
                    }
                    else if(credentialCounter == 1)
                    {
                        if(password.equals(credentialStr))
                        {
                            passwordVerified = true;
                        }
                    }
                    credentialCounter++;
                }
                
                bReader.close();
                
                //final credential check and server sends stuff here
                if(usernameVerified && passwordVerified)
                {
                    outgoingMessage.printf("welcome %s!\n", username);
                    
                    //get the users inbox folder
                    sPath = String.format("users/%s/inbox", username);
                    userPath = new File(sPath);
                    userFiles = userPath.listFiles();
                    
                    //send client number of files in inbox directory
                    outgoingMessage.printf("%d", userFiles.length);
                    
                    
                    //send client all inbox files
                    for(int i = 0; i < userFiles.length; i++){
                        sInbox = getUserFiles(userFiles[i]);
                        outgoingMessage.printf("%s", sInbox);
                    }
                    
                    /*get the users sent folder
                    userPath = new File("users/%s/sent");
                    userFiles = userPath.listFiles();
                    for(int i = 0; i < userFiles.length; i++){
                        sInbox = getUserFiles(userFiles[i]);
                        outgoingMessage.printf("%s", sInbox);
                    }
                    
                    //get the users trash folder
                    userPath = new File("users/%s/trash");
                    userFiles = userPath.listFiles();
                    for(int i = 0; i < userFiles.length; i++){
                        sInbox = getUserFiles(userFiles[i]);
                        outgoingMessage.printf("%s", sInbox);
                    }*/
                }
                else
                {
                    outgoingMessage.println("INTRUDER ALERT");
                }
                
                credentialCounter = 0;
                usernameVerified = false;
                passwordVerified = false;
                credentialStr = "";
                
                //close server socket
                clientSocket.close();
            }
        }
        catch(Exception e){
            System.out.println("Server crashed");
        }
    }
    
    public static String getUserFiles(File fileName){
        String sFormat = "";
        
        try{
            //parse xml file and send parsed string to client                 
            xmlFactory = DocumentBuilderFactory.newInstance();
            xmlBuilder = xmlFactory.newDocumentBuilder();
            xmlDoc = xmlBuilder.parse(fileName);

            xmlDoc.getDocumentElement().normalize();
            sFormat = xmlDoc.getElementsByTagName("datetime").item(0).getTextContent();
            sFormat += "\n";
            sFormat += xmlDoc.getElementsByTagName("to").item(0).getTextContent();
            sFormat += "\n";
            sFormat += xmlDoc.getElementsByTagName("from").item(0).getTextContent();
            sFormat += "\n";
            sFormat += xmlDoc.getElementsByTagName("subject").item(0).getTextContent();
            sFormat += "\n";
            sFormat += xmlDoc.getElementsByTagName("body").item(0).getTextContent();
            sFormat += "\n";
        }
        catch(Exception e){
            System.out.println("Failed to retrieve specified folder");
        }
        return sFormat;
    }
}
