#include <SPI.h>
#include <WiFi.h>

char ssid[] = "H2suP7h4Gh";      // your network SSID (name)
char pass[] = "12345678";   // your network password
int keyIndex = 0;                 // your network key Index number (needed only for WEP)

int status = WL_IDLE_STATUS;
int port = 20276;

IPAddress server(192,168,163,141);  // numeric IP for Google (no DNS)
//char server[] = "www.google.com";    // name address for Google (using DNS)
//74,125,232,128

// Initialize the Ethernet client library
// with the IP address and port of the server
// that you want to connect to (port 80 is default for HTTP):

WiFiClient client;

void printWifiStatus() {

  // print the SSID of the network you're attached to:

  Serial.print("SSID: ");

  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address:

  IPAddress ip = WiFi.localIP();

  Serial.print("IP Address: ");

  Serial.println(ip);

  // print the received signal strength:

  long rssi = WiFi.RSSI();

  Serial.print("signal strength (RSSI):");

  Serial.print(rssi);

  Serial.println(" dBm");
}

long int GetResistance(int measurement) {
  static int R1 = 10000;
  static int R2 = 680;
  static int sensitivity = 65536;
  if(measurement > 0) {
    return R1 * sensitivity / measurement - R1 - R2;
  }
  return 0;
}

void PrintMeasurements() {
  Serial.print("meas ");
  Serial.print(GetResistance(analogRead(A1)));
  Serial.print(" ");
  Serial.print(GetResistance(analogRead(A2)));
  Serial.print(" ");
  Serial.println(GetResistance(analogRead(A3)));
}

void ReadString(String& str_ref) {
  if (str_ref.indexOf("send help") == 0) {//authentication
    client.println("no help");
  }
  if (str_ref.indexOf("wtf") == 0) {//identification
    client.println("sensor friend");
  }
  if (str_ref.indexOf("hello") == 0) {//stay connected
    client.println("hello");
  }
  if (str_ref.indexOf("send") == 0) {//perform task
    client.print("meas ");
    client.print(GetResistance(analogRead(A1)));
    client.print(" ");
    client.print(GetResistance(analogRead(A2)));
    client.print(" ");
    client.println(GetResistance(analogRead(A3)));
  }
}

void setup() {
  
  //Initialize serial and wait for port to open:

  Serial.begin(9600);

  while (!Serial) {

    ; // wait for serial port to connect. Needed for native USB port only

  }

  // check for the presence of the shield:

  if (WiFi.status() == WL_NO_SHIELD) {

    Serial.println("WiFi shield not present");

    // don't continue:

    while (true);

  }

  // attempt to connect to Wifi network:

  while (status != WL_CONNECTED) {

    Serial.print("Attempting to connect to SSID: ");

    Serial.println(ssid);

    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:

    status = WiFi.begin(ssid, pass);

    // wait 5 seconds for connection:

    delay(5000);

  }

  Serial.println("Connected to wifi");

  printWifiStatus();

  Serial.println("\nStarting connection to server...");

  // if you get a connection, report back via serial:

  if (client.connect(server, port)) {

    Serial.println("connected to server");

  }
}

void loop() {
  client.println("friend");
  // if there are incoming bytes available

  // from the server, read them and print them:

  static String msg = "";

  while (client.available()) {

    char c = client.read();

    Serial.write(c);

    msg = msg + c;//into string
    int n = msg.indexOf('\n');
    if (n >= 0) {
      String com = msg.substring(0, n);
      msg = msg.substring(n);
      ReadString(com);
    }
    
  }

  // if the server's disconnected, stop the client:

  if (!client.connected()) {

    Serial.println();

    Serial.println("disconnecting from server.");

    client.stop();

    if (client.connect(server, port)) {//reconnect
  
      Serial.println("connected to server");
  
    }

  }
}
