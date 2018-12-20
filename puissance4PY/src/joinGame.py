from src.slave import Slave


class Join(object):
	"""Join an already created room."""

	def __init__(self, master):
		"""Join a playroom.
		Connect client to server
		Args:
	    	master: (TCP_IP, port)
		"""

		self.player2 = Slave(master[0], master[1])

obj = Join(('81.65.45.168', 8023))