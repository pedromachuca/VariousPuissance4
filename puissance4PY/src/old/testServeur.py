import socket
import sys
import _thread

HOST = ''   # Symbolic name meaning all available interfaces
PORT = 5000 # Arbitrary port

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("Socket created")

#bind the socket to a network interface and port number
try:
	serversocket.bind((HOST, PORT))
except socket.error as msg:
	print("Bind failed. Error code : " + str(msg[0]) + "Message" + msg[1])
	sys.exit()
print("Socket bind complete")

#Start waiting for up to 2 connection
serversocket.listen(2)
print("Socket now listening")
def menu():
	#Sending message to connected client
	conn.send(b'***********************************\n')
	conn.send(b'*				  *\n')
	conn.send(b'*  Welcome to the 4 connect game  *\n')
	conn.send(b'*				  *\n')
	conn.send(b'***********************************\n')
	conn.send(b'To continue, choose an option from the menu (1 or 2):\n')
	conn.send(b'1- Local game\n')
	conn.send(b'2- Online game')
#Function for handling connections. This will be used to create threads
def clientthread(conn):
	menu()
	#infinite loop so that function do not terminate and thread do not end
	while True:		
		#Receiving from client
		data = conn.recv(1024)
		if not data:
			break
		print ('The answer :', data)
	#out of the loop
	conn.close()
#Loop do to something wit the clientsocket
while 1:
    #wait to accept a connection - blocking call
    conn, addr = serversocket.accept()
    print ('Connected with '+ addr[0] + ':' + str(addr[1]))
    menu()
    #start new thread takes 1st1221 argument as a function name to be run, second is the tuple of arguments to the function.
    try:
    	_thread.start_new_thread(clientthread, (conn, ))
    except:
    	print('Error : unable to start thread')
serversocket.close()


