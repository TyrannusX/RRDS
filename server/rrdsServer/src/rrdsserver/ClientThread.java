/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package rrdsserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 *
 * @author rob
 */
public class ClientThread extends Thread{
    private Socket thrdSocket;
    private BufferedReader incomingMessage; //input stream to get client data
    private PrintWriter outgoingMessage; //output stream to client
    private StringTokenizer splitter; //tokenizer
    private FileReader myFile; //file reader to read in credentials, inbox, outbox, etc
    private Element xmlElement; //xml element
    private DocumentBuilderFactory xmlFactory; //xml factory
    private DocumentBuilder xmlBuilder; //xml builder
    private InputSource xmlSource; //xml source
    private StringReader xmlReader; //xml reader
    private Document xmlDoc; //xml document
    private FileOutputStream serializeOutputStream;
    private FileInputStream serializeInputStream;
    private ObjectOutputStream serializeObjectOutputStream;
    private ObjectInputStream serializeObjectInputStream;

    public ClientThread(Socket socketIn){
        thrdSocket = socketIn;
    }

    @Override
    public void run(){
        //declare variables
        int credentialCounter = 0;
        boolean usernameVerified = false;
        boolean passwordVerified = false;
        String credentialStr = "";
        String tempStr = "";
        String username; //client username
        String password; //client password
        String sPath = "";
        String sInboxPath = "";
        String sSentPath = "";
        String sTrashPath = "";
        File inbox;
        File[] inboxFiles;
        File sent;
        File[] sentFiles;
        File trash;
        File[] trashFiles;
        String sInbox = "";
        String sSent = "";
        String sTrash = "";
        
        try{
        
            //intialize streams
           incomingMessage = new BufferedReader(new InputStreamReader(thrdSocket.getInputStream()));
           outgoingMessage = new PrintWriter(thrdSocket.getOutputStream(), true);

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
               //check the user name
               if(credentialCounter == 0)
               {
                   if(username.equals(credentialStr))
                   {
                       usernameVerified = true;
                   }                       
               }
               //check the password
               else if(credentialCounter == 1)
               {
                   if(password.equals(credentialStr))
                   {
                       passwordVerified = true;
                   }
               }
               credentialCounter++;
           }
           
           //close the file
           bReader.close();

           //if client was verified
           if(usernameVerified && passwordVerified)
           {
               //send message to the client letting them know they're good
               outgoingMessage.printf("welcome %s!\n", username);

               //get the inbox directory
               sPath = String.format("users/%s/inbox", username);
               inbox = new File(sPath);
               
               //get the sent directory
               sPath = String.format("users/%s/sent", username);
               sent = new File(sPath);
               
               //get the trash directory
               sPath = String.format("users/%s/trash", username);
               trash = new File(sPath);
               
               System.out.println("initial read thread");
               tempStr = incomingMessage.readLine();
               System.out.println("initial read thread complete");
               while(!tempStr.equals("exit")){
                   System.out.printf("request = %s", tempStr);
                   //if request is for inbox files
                   if(tempStr.equals("getinbox")){
                       //send all inbox files to client
                       inboxFiles = inbox.listFiles();
		       outgoingMessage.printf("%d\n", inboxFiles.length);
                       for(int i = 0; i < inboxFiles.length; i++){
                           outgoingMessage.printf("%s", getRequestedFile(inboxFiles[i]));
                       }
                   }
                   //if request is for sent files
                   else if(tempStr.equals("getsent")){
                       //send all sent files to client
                       sentFiles = sent.listFiles();
                       for(int i = 0; i < sentFiles.length; i++){
                           
                       }
                   }
                   //if request is for trash files
                   else if(tempStr.equals("gettrash")){
                       //send all trash files to client
                       trashFiles = trash.listFiles();
                       for(int i = 0; i < trashFiles.length; i++){
                           
                       }
                   }
                   tempStr = incomingMessage.readLine();
               }
           }
           else
           {
               outgoingMessage.println("INTRUDER ALERT");
           }
        }
        catch(Exception e){
            if(thrdSocket == null){
                System.out.println("the socket is null");
            }
            System.out.println("Thread failed");
        }
    }
    
    public String getRequestedFile(File fileName){
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
