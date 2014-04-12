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
public class RrdsServer {
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
        String sFormat = "";
        String xmlStr = "";
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
                    
                    //get inbox file and send to client
                    sFormat = String.format("users/%s/inbox/2014-04-12T14-32-00Z.xml", username);
                    myFile = new FileReader(sFormat);
                    bReader = new BufferedReader(myFile);
                    
                    sFormat = "";
                    while((xmlStr = bReader.readLine()) != null)
                    {
                        sFormat += String.format("%s", xmlStr);
                    }
                    outgoingMessage.printf("%s\n", sFormat);
                    
                    bReader.close();
                    
                    sFormat = "";
                    
                    //parse xml file and send parsed string to client                 
                    sFormat = String.format("users/%s/inbox/2014-04-12T14-32-00Z.xml", username);
                    File xmlFile = new File(sFormat);
                    xmlFactory = DocumentBuilderFactory.newInstance();
                    xmlBuilder = xmlFactory.newDocumentBuilder();
                    xmlDoc = xmlBuilder.parse(xmlFile);
                    
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
                    
                    System.out.printf("%s\n", sFormat);
                    
                    outgoingMessage.printf("%s", sFormat);
                }
                else
                {
                    outgoingMessage.println("INTRUDER ALERT");
                }
                
                credentialCounter = 0;
                usernameVerified = false;
                passwordVerified = false;
                credentialStr = "";
                sFormat = "";
                
                //close server socket
                clientSocket.close();
            }
        }
        catch(Exception e){
            System.out.println("Server crashed");
        }
    }
    
}
