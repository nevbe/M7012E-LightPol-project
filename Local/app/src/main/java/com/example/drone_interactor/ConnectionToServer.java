import java.io.*;
import java.net.*;


public class ConnectionToServer {
    private static DataOutputStream dataOutputStream = null;
    public static boolean connection= true;
    //private static DataInputStream dataInputStream = null;
    public static void main(String[] args) throws Exception {
        
     
        try (// Establish a connection to the server 192.168.1.110 20276
        Socket socket = new Socket("localhost", 20276)) {
            // Create input/output streams for communication with the server
            // Send the verification code to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      
            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));       
            out.println("no help");

            PrintWriter out1 = new PrintWriter(socket.getOutputStream(), true);
            //BufferedReader in1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out1.println("sensor friend");

            PrintWriter out2 = new PrintWriter(socket.getOutputStream(), true);
            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));       
            out2.println("phone friend");
            try{
                    //dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    sendFile("/Users/Hamid Ehsani/Desktop/pexels-photo-1421903.jpeg");
                    System.out.println("The file has been sent");
                   
                
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
        dataOutputStream.writeLong(file.length());  
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
}
