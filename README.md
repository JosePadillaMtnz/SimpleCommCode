## Simple Communication Code (Java)
Programming of different simple codes in Java related to communication between different devices. We are going to show different codes that deal with different functionalities related to communications.

### Case 1: HTTP server using cookies (and HTTP client option) 
First, we have a Server class that simulates an HTTP server. We can make requests to this server through a browser client. Every time we access this server, it will show us all the accesses that we have made within the server as a list. Apart from the browser client, we also have a simulated client in the Client class, which allows us to simulate queries to the server at the url that we indicate in the terminal.

The main idea of ​​this case is the use of cookies to store information that is exchanged and used on the client and on the server. In the case of using a web browser client, it is responsible for storing and sending cookies. In the case of using the Client class that the browser simulates, what we do is store the cookie in a file and treat it in the same way as the browser. Therefore, the client to implement persistence makes use of a file, and the server has no state, but acts based on the information received.

To get the scenario up, you simply have to compile and run the Server class, and then access it from the browser (localhost:9999) or by compiling and running the Client class. In case of using the client, it will ask you for the url you want to consult and it will make the query, executing the code every time you want to make a new query.

### Case 2: Communication between client and weather servers (unicast and broadcast messages)
We have three main classes, being firstly a client (Client) and secondly two servers that simulate the reading of different climatic parameters (ServerT and ServerH). We also have a class to initialize the previously mentioned classes (Main).

The servers will be in charge of reading, processing and sending the data read (the reading of data is simulated by generating them randomly), while also listening for unicast control messages sent by the client. On the part of the client, it is responsible for receiving the data from the servers periodically and displaying them on the screen, also allowing the possibility of changing different parameters of the servers. Specifically, we can make the different changes on each server:

- Close the server.
- Request a "read" of data at that time and receive the data.
- Change the frequency of sending data.
- Change the range of a specific parameter.

To get the scenario up, simply compile and run the Main class. Once executed, the data appears on the screen once received (broadcast), also allowing the possibility of sending control messages to the servers (unicast).