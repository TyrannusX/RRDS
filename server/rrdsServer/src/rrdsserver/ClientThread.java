//PROGRAMMERS: Robert Reyes and Darius Stephen
//Source: http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.util.Arrays;
import java.util.Collections;
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
    private PrintWriter writeClientMessage; //used to write to a file
    private StringTokenizer splitter; //tokenizer
    private FileReader myFile; //file reader to read in credentials, inbox, outbox, etc
    private DocumentBuilderFactory xmlFactory; //xml factory
    private DocumentBuilder xmlBuilder; //xml builder
    private Document xmlDoc; //xml document
    private String[] userArr;
    private String[] passwordArr;

    public ClientThread(Socket socketIn){
        //get socket
        thrdSocket = socketIn;
    }

    @Override
    public void run(){
        //declare variables
        int credentialCounter = 0; //counter for separating username and password checks
        boolean usernameVerified = false; //username flag
        boolean passwordVerified = false; //password flag
        String credentialStr = ""; //credential string from client
        String tempStr = ""; //temp string for quick stuff
        String username; //client username
        String password; //client password
        String sPath = ""; //formatted path string
        File inbox; //inbox path
        File[] inboxFiles; //inbox files
        File sent; //sent path
        File[] sentFiles; //sent files
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
           int passIndex = 0;

           //loop through the file to get credentials and verify
           while((credentialStr = bReader.readLine()) != null)
           {
               //check the user name
               if(credentialCounter == 0)
               {
                   userArr = credentialStr.split(" ");
                   for(int i = 0; i < userArr.length; i++){
                        if(username.equals(userArr[i]))
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
                   passwordArr = credentialStr.split(" ");
                   if(password.equals(passwordArr[passIndex]))
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
               
               //loop and serve client until they exit
               tempStr = incomingMessage.readLine(); 
               while(!tempStr.equals("<exitloop>")){
                   //if request is for inbox files
                   if(tempStr.equals("getinbox")){                     
                       //send all inbox files to client
                       inboxFiles = inbox.listFiles();
                       Arrays.sort(inboxFiles, Collections.reverseOrder());
		       outgoingMessage.printf("%d\n", inboxFiles.length);
                       for(int i = 0; i < inboxFiles.length; i++){
                           outgoingMessage.printf("%s", getRequestedFile(inboxFiles[i]));
                       }
                   }
                   //if request is for sent files
                   else if(tempStr.equals("getsent")){
                       //send all sent files to client
                       sentFiles = sent.listFiles();
                       Arrays.sort(sentFiles, Collections.reverseOrder());
                       outgoingMessage.printf("%d\n", sentFiles.length);
                       for(int i = 0; i < sentFiles.length; i++){
                           outgoingMessage.printf("%s", getRequestedFile(sentFiles[i]));
                       }
                   }
                   //push message to server
                   else if(tempStr.contains("<pushfile>")){
                       //pull <pushfile> from the message string
                       String[] clientArray = tempStr.split("<pushfile>");
                       sClientMessage = clientArray[0];
                       //save message to server
                       pushMessage(sClientMessage, username);
                   }
                   //delete file from server
                   else if(tempStr.contains("<deletefileinbox>")){
                       //pull <deletefileinbox> from the message string
                       String[] clientArray = tempStr.split("<deletefileinbox>");
                       sClientMessage = clientArray[0];
                       //delete message from server
                       deleteFileFromInbox(sClientMessage, username);
                   }
                   else if(tempStr.contains("<deletefilesent>")){
                       //pull <deletefilesent> from the message string
                       String[] clientArray = tempStr.split("<deletefilesent>");
                       sClientMessage = clientArray[0];
                       //delete message from server
                       deleteFileFromSent(sClientMessage, username);
                   }
                   
                   //get request message from client
                   tempStr = incomingMessage.readLine();
               }
           }
           else
           {
               outgoingMessage.println("INTRUDER ALERT");
           }
           //close socket
           thrdSocket.close();
        }
        catch(Exception e){
            System.out.println("Thread failed");
        }
    }
    
    //method to parse xml file from server
    public String getRequestedFile(File fileName){
        String sFormat = "";
        
        try{
            //parse xml file and send parsed string to client                 
            xmlFactory = DocumentBuilderFactory.newInstance();
            xmlBuilder = xmlFactory.newDocumentBuilder();
            xmlDoc = xmlBuilder.parse(fileName);

            //parse the email from xml
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
    
    //method to save message to server
    public void pushMessage(String clientMessageIn, String username){
        //string to hold xml
        String sXml = ""; 
        try {
            //remove <separator> tags from message
            String[] splitMessage = clientMessageIn.split("<separator>");
            
            //add xml tags between email fields
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
            //add closing xml tag
            sXml += "</xml>\n";
            
            //save message to client's sent directory
            String sPath = String.format("users/%s/sent/%s.xml", username, splitMessage[0]);
            File tempFile = new File(sPath);
            writeClientMessage = new PrintWriter(tempFile);
            writeClientMessage.printf("%s", sXml);
            writeClientMessage.close();
            
            //check if receiving client exists
            boolean userDoesExist = false;
            for(int i = 0; i < userArr.length; i++){
                if(userArr[i].equals(splitMessage[1])){
                    userDoesExist = true;
                }
            }
            if(userDoesExist){
                //save message to receiving clients inbox directory
                sPath = String.format("users/%s/inbox/%s.xml", splitMessage[1], splitMessage[0]);
                tempFile = new File(sPath);
                writeClientMessage = new PrintWriter(tempFile);
                writeClientMessage.printf("%s", sXml);
                writeClientMessage.close();
            }
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //method to delete file from inbox
    public void deleteFileFromInbox(String clientMessageIn, String username){
        //delete requested file
        String sPath = String.format("users/%s/inbox/%s", username, clientMessageIn);
        File tempFile = new File(sPath);
        tempFile.delete();
    }
    
    //method to delete file from sent
    public void deleteFileFromSent(String clientMessageIn, String username){
        //delete requested file
        String sPath = String.format("users/%s/sent/%s", username, clientMessageIn);
        File tempFile = new File(sPath);
        tempFile.delete();
    }
}
