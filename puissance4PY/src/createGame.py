from src.master import Master


class Game(object):
	"""Playroom with 2 players."""

	def __init__(self, master):
		"""Create a playroom.
		Start the server and connect client to it
		Args:
	    	master: (TCP_IP, port)
		"""

		self.player1 = Master(master[0], master[1])

obj = Game(('192.168.43.125', 8023))