/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package rrdsserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private PrintWriter writeClientMessage;
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
        String sClientMessage = "";
        
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
           String[] credentialArr;
           int passIndex = 0;

           //loop through the file to get credentials and verify
           while((credentialStr = bReader.readLine()) != null)
           {
               //check the user name
               if(credentialCounter == 0)
               {
                   credentialArr = credentialStr.split(" ");
                   for(int i = 0; i < credentialArr.length; i++){
                        if(username.equals(credentialArr[i]))
                        {
                            usernameVerified = true;
                            passIndex = i;
                            break;
                        }
                   }
               }
               //check the password
               else if(credentialCounter == 1)
               {
                   credentialArr = credentialStr.split(" ");
                   if(password.equals(credentialArr[passIndex]))
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
               
               tempStr = incomingMessage.readLine();
               while(!tempStr.equals("exit")){
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
                       outgoingMessage.printf("%d\n", sentFiles.length);
                       for(int i = 0; i < sentFiles.length; i++){
                           outgoingMessage.printf("%s", getRequestedFile(sentFiles[i]));
                       }
                   }
                   //if request is for trash files
                   else if(tempStr.equals("gettrash")){
                       //send all trash files to client
                       trashFiles = trash.listFiles();
                       outgoingMessage.printf("%d\n", trashFiles.length);
                       for(int i = 0; i < trashFiles.length; i++){
                           outgoingMessage.printf("%s", getRequestedFile(trashFiles[i]));
                       }
                   }
                   //push message to server
                   else if(tempStr.contains("<pushfile>")){
                       String[] clientArray = tempStr.split("<pushfile>");
                       sClientMessage = clientArray[0];
                       System.out.printf("message = %s\n", sClientMessage);
                       pushMessage(sClientMessage, username);
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
    
    public void pushMessage(String clientMessageIn, String username){
        String sXml = ""; 
        try {
            String[] splitMessage = clientMessageIn.split("<br>");
            
            sXml += "<xml>\n";
            for(int i = 0; i < 5; i++){
                switch(i){
                    case 0:
                        sXml += "\t<datetime>";
                        sXml += splitMessage[i];
                        sXml += "</datetime>\n";
                        break;
                    case 1:
                        sXml += "\t<to>";
                        sXml += splitMessage[i];
                        sXml += "</to>\n";
                        break;
                    case 2:
                        sXml += "\t<from>";
                        sXml += splitMessage[i];
                        sXml += "</from>\n";
                        break;    
                    case 3:
                        sXml += "\t<subject>";
                        sXml += splitMessage[i];
                        sXml += "</subject>\n";
                        break;
                    case 4:
                        sXml += "\t<body>";
                        sXml += splitMessage[i];
                        sXml += "</body>\n";
                        break;    
                }                                             
            }
            sXml += "</xml>\n";
            
            String sPath = String.format("users/%s/sent/%s.xml", username, splitMessage[0]);
            File tempFile = new File(sPath);
            writeClientMessage = new PrintWriter(tempFile);
            writeClientMessage.printf("%s", sXml);
            writeClientMessage.close();
            
            sPath = String.format("users/%s/inbox/%s.xml", splitMessage[1], splitMessage[0]);
            tempFile = new File(sPath);
            writeClientMessage = new PrintWriter(tempFile);
            writeClientMessage.printf("%s", sXml);
            writeClientMessage.close();
            
            System.out.println("done writing file");
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
