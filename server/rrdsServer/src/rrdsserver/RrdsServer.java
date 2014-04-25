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
    private static ClientThread acceptThread;
    
    public static void main(String[] args) {    
        try{
            //initialize the server socket
            serverSocket = new ServerSocket(8080);

            //while loop to run server
            while(true){
                //accept incoming connections
                clientSocket = serverSocket.accept();
                
                acceptThread = new ClientThread(clientSocket);
                acceptThread.run();
                
                //close server socket
                clientSocket.close();
            }
        }
        catch(Exception e){
            System.out.println("Server crashed");
        }
    }
}
