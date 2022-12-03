/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

/**
 *
 * @author phamt
 */
// Java implementation for multithreaded chat client
// Save file as Client.java

import Momo.GUIlogin;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 1234;

    public static void main(String args[]) throws UnknownHostException, IOException
    {
        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // open the connection
        Socket s = new Socket(ip, ServerPort);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        GUIlogin loginPage = new GUIlogin(s,dis,dos);
        loginPage.setVisible(true);
    }
}

