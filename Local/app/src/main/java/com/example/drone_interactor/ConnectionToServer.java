import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;




public class ConnectionToServer {
    private static DataOutputStream dataOutputStream = null;
    public static boolean connection= true;
    public static void main(String[] args) throws Exception {
        
     
        try (// Establish a connection to the server 192.168.101.141 20276
        Socket socket = new Socket("192.168.101.141", 20276)) {
            // Create input/output streams for communication with the server
            // Send the verification code to the server
                       
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);                  
            out.println("no help");

            TimeUnit.SECONDS.sleep(2);
            PrintWriter out1 = new PrintWriter(socket.getOutputStream(), true);

          
            TimeUnit.SECONDS.sleep(2);
            PrintWriter out2 = new PrintWriter(socket.getOutputStream(), true);

                 
            out2.println("phone friend");
            TimeUnit.SECONDS.sleep(2);

            try{
                    
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    sendFile("/Users/Hamid Ehsani/Desktop/2.jpeg");
                    
                    System.out.printf("The file has been sent to the server ");
                    //TimeUnit.SECONDS.sleep(100);
                
                }
            catch (Exception e){
                    System.out.println("OOOOOOOhhhhhh I know what is fel!!");
                }
        }
        
    }


    private static void sendFile(String path) throws Exception{
        
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        String str = "img Fabian.jpg;" + file.length();
       
        dataOutputStream.write(str.getBytes());
        TimeUnit.SECONDS.sleep(1);

   
        for(int a=0; a<file.length(); a++) {
        
            
            if(file.length()==1024){
                TimeUnit.MILLISECONDS.sleep(5);
                dataOutputStream.write('a');
            }
            dataOutputStream.write('a');
            
        }
        fileInputStream.close();

}}
