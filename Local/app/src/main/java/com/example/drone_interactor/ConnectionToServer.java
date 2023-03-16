import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.print.attribute.standard.PrinterMakeAndModel;


public class ConnectionToServer {
    private static DataOutputStream dataOutputStream = null;
    public static boolean connection= true;
    //private static DataInputStream dataInputStream = null;
    public static void main(String[] args) throws Exception {
        
     
        try (// Establish a connection to the server 192.168.101.141 20276
        Socket socket = new Socket("localhost", 20276)) {
            // Create input/output streams for communication with the server
            // Send the verification code to the server
            //String[] command = {"no help", "sensor friend", "phone friend"};

            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));       
            out.println("no help");
            TimeUnit.SECONDS.sleep(2);
            PrintWriter out1 = new PrintWriter(socket.getOutputStream(), true);
            //BufferedReader in1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //out1.println("sensor friend");
            //TimeUnit.SECONDS.sleep(5);
            TimeUnit.SECONDS.sleep(2);
            PrintWriter out2 = new PrintWriter(socket.getOutputStream(), true);
            //BufferedReader in2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));       
            out2.println("phone friend");
            TimeUnit.SECONDS.sleep(2);

            try{
                    //dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    sendFile("/Users/Hamid Ehsani/Desktop/2.jpeg");
                    
                    System.out.printf("The file has been sent to the server ");
                   
                
                }
            catch (Exception e){
                    System.out.println("OOOOOOOhhhhhh!!");
                }
        }
        
    }


    private static void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        String str = "img aaa.jpg;" + file;
        System.out.println(file.length());
        //dataOutputStream.write(str.getBytes());
        TimeUnit.SECONDS.sleep(1);

        // break file into chunks
        //byte[] buffer = new byte[1024];
        
        for(int a=0; a<file.length(); a++) {
            if(file.length()==1024){
                TimeUnit.MILLISECONDS.sleep(5);
                dataOutputStream.write('a');
            }
            dataOutputStream.write('a');
            
        }
        /*
        for(int a=0; a<=file.length(); a++) {
            dataOutputStream.write('b');
        }

        // send file size
        dataOutputStream.writeLong(file.length());  
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }

        fileInputStream.close();
    }
     */
}}
