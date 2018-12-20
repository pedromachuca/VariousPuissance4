import telnetlib

HOST = '127.0.0.1'
PORT = 8023
# from pudb import set_trace; set_trace()
tn = telnetlib.Telnet(HOST, PORT)
tn.set_debuglevel(100)
data = tn.read_until("custom server", timeout=1)
print "Data: " + data

tn.close()