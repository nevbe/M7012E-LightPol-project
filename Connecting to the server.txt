HOW TO CONNECT TO THE SERVER:

connect to the IP of the server, the server is hosted on port 20276.

The server will send a message to greet you:
>	send help

Authenticate by answering accordingly: (the message must at least start with this phrase)
>	no help

If you fail to authenticate, the connection will be terminated by the server.
If you are successfully authenticated, the server will send you the following message:
>	wtf

You must now identify yourself. There are 2 options here:
1. You are a sensor application that will send measurement data. Answer with the following:
>	sensor friend
2. You are a phone application that will send images and receive information. Answer with the following:
>	phone friend

After you have authenticated and identified yourself, you can freely communicate with the server.
There are a few command you can use here.

meas (sensor)
This sends measurements to the server.
This command follows the following structure:
>	meas x y z
x, y and z are integer numbers written as ascii characters.
Example usage:
>	meas 98 592 9

img (phone)
This sends an image to the server.
It follows the following structure:
>	img name.jpg;bytes
>	(file contents)
name can be any name containing letters and numbers, bytes is the number of bytes written as ascii characters.
file contents is just raw file contents as bytes. The number of bytes is defined above.
The server will wait for all contents to be received before reconstructing the image.
Maximum size is 25 megabytes (25000000 bytes).
Example usage:
>	img abc.jpg;90561
>	d0hasn0daKs]uidhoa089i<udhasDohaÅOJud (...) (90561 bytes in total, can be sent over multiple messages)

sensor friend
in order to re-indentify as a sensor, send message:
>	sensor friend

phone friend
in order to re-indentify as a phone, send message:
>	phone friend

IMPORTANT NOTE:
After the client has been inactive for a period of time, the server will send a message to check on you:
>	hello
You must send a message (any message) to the server ASAP to avoid having the connection terminated.
For example:
>	hello
The server waits the same amount of time before kicking you after checking on you as it waits before checking on you.
Any message sent to the server will make the timeout counter 0, regardless if it is a valid command or not.









