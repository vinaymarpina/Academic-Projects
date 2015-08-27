
Assumption : movie name and ticket number in the file is seperated by tab.
-------------------------------------------------------------------

Environment : Minimum Java 1.7 is required for the program
-------------------------------------------------------------------

Files : Server.java  for the server process.
        Client.java for the client process.
        BoxOffice.java to represent tickets database file
        Agent.java for the runnable to server client request
        PurchaseProtocol.java for Messages,Constants

------------------------------------------------------------------

Compilation:    javac Server.java
                javac Client.java
--------------------------------------------------------------------

Run:
                java Server full_input_file_path port_number
				java Client server_ip port_number
			
Example:			
				java Server movies1.txt 2002
                java Client localhost 2002

--------------------------------------------------------------------
			
