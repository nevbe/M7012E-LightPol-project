package com.example.drone_interactor;

import android.os.Environment;
import android.widget.TextView;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;




public class ConnectionToServer {
    private static DataOutputStream dataOutputStream = null;
    private static Socket socket;
    public static boolean connection= true;
    public static ConnectionToServer INSTANCE;

    public ConnectionToServer(){

    }

    public static ConnectionToServer getInstance(){
        if(INSTANCE != null){
            return INSTANCE;
        } else {
            return new ConnectionToServer();
        }
    }


    public static void establishConnection() throws Exception {

        MainActivity.getInstance().showToast("Establishing connection...");

        try {
            // Create input/output streams for communication with the server
            // Send the verification code to the server
            socket = new Socket("192.168.119.141", 20276);
            new SocketThread(socket).start();

            try {

                MainActivity.getInstance().showToast("Connection established");

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                connection = true;


            } catch (Exception e) {
                System.out.println("OOOOOOOhhhhhh I know what is wrong!!");
            }

        } catch (Exception e){
            MainActivity.getInstance().showToast("Error establishing connection: " + e);
        }
        
    }

    private static class SocketThread extends Thread{
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public SocketThread(Socket socket){
            this.clientSocket = socket;
        }

        public void run(){
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;

                while (clientSocket.isConnected()) {
                    if ((inputLine = in.readLine()) != null) {
                        out.println(generateResponse(inputLine));
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e){
                MainActivity.getInstance().showToast("SocketThread error: " + e);
            }
        }

        private static String generateResponse(String command){
            switch(command){
                case "send help" : {
                    return "no help\n";
                }
                case "wtf" : {
                    return "phone friend\n";
                }
                case "hello" : {
                    return "hello";
                }
                default : return null;
            }

        }
    }




    public static void sendFile(String path) throws Exception{
        path = Environment.getExternalStorageDirectory().getPath() + "/LightPolDemo/snap_img.jpg"; //TODO: Lazy hack here :)

        if(!connection || dataOutputStream == null){
            throw new Exception("No active connection");
        }


        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        String str = "img Fab.jpg;" + file.length();
       
        dataOutputStream.write(str.getBytes());
        TimeUnit.SECONDS.sleep(1);


        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);

        int count;
        int tot = 0;
        while ((count = in.read(bytes)) > 0) {
            tot += count;
            dataOutputStream.write(bytes, 0, count);
        }

        MainActivity.getInstance().showToast("Tot:" + tot + ", Len: " + file.length());

        fileInputStream.close();

}}
