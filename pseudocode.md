```
global scope / public:
	string buffer

function ReadCommand(string command):
	if (command.find("send help") == 0):
		SendTCP("no help\n")
	if (command.find("wtf") == 0):
		SendTCP("phone friend\n")
	if (command.find("hello") == 0):
		SendTCP("hello\n")

main loop:
	buffer += ReadTCP() //add contents from socket onto the buffer string
	if (buffer.find_first_of('\n') >= 0): //see if string contains a newline
		ReadCommand(buffer.substring(0, buffer.find_first_of('\n')) //read the buffer up to the newline in a command reader function
		buffer.substring(buffer.find_first_of('\n'), buffer.length()) //remove everything up to the newline
```
	
