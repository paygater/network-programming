/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import Momo.server.GUIserver;
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class Server
{

    static ArrayList<ServerThread> listClient = new ArrayList<>();
    static ArrayList<String> queueLine = new ArrayList<>();
    static ArrayList<String> acceptLine = new ArrayList<>();
    static ArrayList<String> inRoomLine = new ArrayList<>();
    static ArrayList<String> isWaiting = new ArrayList<>();

    public static void main(String[] args) throws IOException
    {
        // server is listening on port 1234
        Scanner scn = new Scanner(System.in);
        ServerSocket ss = new ServerSocket(1234);
        System.out.println("Server bắt đầu hoạt động ...");
        GUIserver guiserver = new GUIserver();
        guiserver.setVisible(true);
        Socket s;
        while (true)
        {
            s = ss.accept();

            System.out.println("New client request received : " + s);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            ServerThread mtch = new ServerThread(s, dis, dos);

            // Create a new Thread with this object.
            Thread t = new Thread(mtch);

            t.start();



        }
    }
    public static void showMessage(String s){
        System.out.println(s);
    }
}

// ClientHandler class
class ServerThread implements Runnable
{

    public String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;
    boolean run;
    ArrayList<String> declineLine;

    // constructor
    public ServerThread(Socket s,DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.isloggedin = false;
        run = true;
        declineLine = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void run() {

         while (run) {
            try {
                String received = dis.readUTF();
                System.out.println("REceived " + received );
                StringTokenizer str = new StringTokenizer(received, "#");//tach cu phap tu client
                String res = str.nextToken();
                switch (res) {
                    case "nickname":
                        boolean exist = false;
                        String nickname = "";
                        while (str.hasMoreTokens()) {
                            nickname = str.nextToken();
                        }
                        if (Server.listClient.isEmpty()) {
                            setName(nickname);
                            this.isloggedin = true;
                            System.out.println("Adding this client to active client list");
                            Server.listClient.add(this);
                            Server.queueLine.add(this.getName());
                            Server.showMessage(nickname + " not exist");
                            dos.writeUTF("notexist");
                        } else {
                            for (int i = 0; i < Server.listClient.size(); i++) {
                                String clName = Server.listClient.get(i).getName();
                                if (clName.equals(nickname)) {
                                    exist = true;
                                    break;
                                }
                            }
                            if (exist == false) {
                                setName(nickname);
                                this.isloggedin = true;
                                System.out.println("Adding this client to active client list");
                                Server.listClient.add(this);
                                Server.queueLine.add(this.getName());
                                Server.showMessage(nickname + " not exist");
                                try {
                                    dos.writeUTF("notexist");
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                            }
                            else {
                                try {
                                    dos.writeUTF("existed");
                                    Server.showMessage(nickname + " existed");
                                }
                                catch (Exception e){
                                    System.out.println(e);
                                }
                            }
                        }

                        break;
                    case "find" :
                        try {
                            if (Server.queueLine.size() == 0){
                                dos.writeUTF("notfound#");
                                Server.queueLine.add(this.getName());
                            }
                            else {
                                boolean isFound = false;
                                for (ServerThread st : Server.listClient){
                                    if (!st.getName().equals(this.getName())
                                            && !Server.isWaiting.contains(st.getName())
                                                && Server.queueLine.contains(st.getName())
                                                    && !declineLine.contains(st.getName())
                                                        && !st.declineLine.contains(this.getName())
                                                            && !Server.inRoomLine.contains(st.getName())) {
                                        Server.isWaiting.add(this.getName());
                                        Server.isWaiting.add(st.getName());
                                        dos.writeUTF("found#"+ st.getName());
                                        // System.out.println(this.getName() + " found " + st.getName());
//                                        st.dos.writeUTF("found#"+this.getName());
//                                        System.out.println(st.getName() + " found " + this.getName());
                                        isFound = true;
                                        break;
                                    }
                                }
                                if (!isFound){
                                    System.out.println(this.getName() + "notfound");
                                    dos.writeUTF("notfound#");
                                    //Server.queueLine.add(this.getName());
                                }
                            }

                        }catch (Exception e){
                            System.out.println(e);
                        }
                        break;
                    case "accept":
                        String guestNicknameAcpt = "";
                        Server.isWaiting.remove(this.getName());
                        while (str.hasMoreTokens()) {
                            guestNicknameAcpt = str.nextToken();
                        }
                        Server.acceptLine.add(this.getName() + "#" + guestNicknameAcpt);
                        if (Server.acceptLine.contains(guestNicknameAcpt + "#" + this.getName())){
                            dos.writeUTF("matched#");
                            Server.inRoomLine.add(this.getName());
                            break;
                        }
                        for (ServerThread st : Server.listClient){
                            if (st.getName().equals(guestNicknameAcpt)){
                                st.dos.writeUTF("found#" + this.getName());
                            }
                        }
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        }catch (Exception e){
                            System.out.println(e);
                        }
                        if (Server.acceptLine.contains(guestNicknameAcpt + "#" + this.getName())){
                            dos.writeUTF("matched#");
                            Server.inRoomLine.add(this.getName());
                        }
                        else {
                            dos.writeUTF("notmatched#");
                            declineLine.add(guestNicknameAcpt);
                        }
                        //Server.acceptLine.remove(this.getName() + "#" + guestNicknameAcpt);
                        break;
                    case "decline":
                        String guestNicknameDecl = "";
                        Server.isWaiting.remove(this.getName());
                        while (str.hasMoreTokens()) {
                               guestNicknameDecl = str.nextToken();
                        }
                        declineLine.add(guestNicknameDecl);
                        break;
                    case "outroom":
                        String opponent1 = "";
                        Server.inRoomLine.remove(this.getName());
                        Server.isWaiting.remove(this.getName());
                        for (String s : Server.acceptLine){
                            StringTokenizer st1 = new StringTokenizer(s,"#");
                            String me = st1.nextToken();
                            if (me.equals(this.getName())){
                                opponent1 = st1.nextToken();
                            }
                        }
                        for (ServerThread st : Server.listClient){
                            if (st.getName().equals(opponent1)){
                                declineLine.add(opponent1);
                                st.declineLine.add(this.getName());
                                st.dos.writeUTF("outchat#");
                                break;
                            }
                        }
                        Server.inRoomLine.remove(opponent1);
                        Server.acceptLine.remove(this.getName()+"#" + opponent1);
                        Server.acceptLine.remove(opponent1 + "#" + this.getName());
                        break;
                    case "logout":
                        if (Server.inRoomLine.contains(this.getName())){
                            String opponent = "";
                            Server.inRoomLine.remove(this.getName());
                            for (String s : Server.acceptLine){
                                StringTokenizer st1 = new StringTokenizer(s,"#");
                                String me = st1.nextToken();
                                if (me.equals(this.getName())){
                                    opponent = st1.nextToken();
                                }
                          }
                            for (ServerThread st : Server.listClient){
                                if (st.getName().equals(opponent)){
                                    st.dos.writeUTF("outroom#");
                                    break;
                                }
                            }
                            Server.inRoomLine.remove(opponent);
                            Server.acceptLine.remove(this.getName()+"#" + opponent);
                            Server.acceptLine.remove(opponent + "#" + this.getName());
                        }
                        Server.isWaiting.remove(this.getName());
                        Server.listClient.remove(this);
                        Server.queueLine.remove(this.getName());
                        declineLine.clear();
                        Server.showMessage(this.getName() + "out !");
                        run = false;
                        this.dos.close();
                        this.dis.close();

                        break;
                    case "message":
                        String msg = "";
                        String receiver = "";
                        receiver = str.nextToken();
                        msg = str.nextToken();
                        for (ServerThread st : Server.listClient){
                            if (st.getName().equals(receiver)){
                                st.dos.writeUTF("message#"+msg);
                                break;
                            }
                        }

                        break;
                    case "publicKey":
                        String key = str.nextToken();
                        String keyReceiver = str.nextToken();
                        for (ServerThread st : Server.listClient){
                            if (st.getName().equals(keyReceiver)){
                                st.dos.writeUTF("publicKey#" + key);
                                break;
                            }
                        }
                        Server.showMessage("receive key");
                        break;
                }
                System.out.println(this.getName()+" receive : " + received);
            } catch (IOException e) {

                e.printStackTrace();
            }
         }
    }
}
