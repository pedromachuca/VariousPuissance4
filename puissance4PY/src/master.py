import socket
import pickle
import telnetlib
from struct import *

class Master():
	"""Master Player."""

	def __init__(self, HOST, PORT):
	
		self.chip1= '\033[94m' + "X" + '\033[0m'
		self.chip2 = '\033[91m' + "O" + '\033[0m'
		self.col = 7
		self.line = 6
		self.victory = 0
		self.turn = 0
		self.grid = []

		self.conn = None
		print ("***********************************")
		print ("*				  *")
		print ("*  Welcome to the 4 connect game  *")
		print ("*				  *")
		print ("***********************************\n")

		try:
			self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			print("Socket created")
			self.s.bind((HOST, PORT))
			print("Socket bind complete")
			self.s.listen(1)
			print("Socket now listening")
			self.connect()
			self.ask_to_retrieve()
			self.grid_initialisation()
			p1, p2 = 0, 0
			for c in range(7):
				for l in range(6):
					if self.grid[c][l] == self.chip1:
						p1 = p1+1
						
					elif self.grid[c][l] == self.chip2:
						p2 = p2+1

					else:
						pass
			print(p1)
			print(p2)
			self.display_grid()
			if p2<p1:
				self.turn =1
				print("Waiting for player 2")
			newGame = ' '
			newGame = self.play(self.turn)
			while newGame == 'y':
				self.grid = []
				try:
					self.grid_initialisation()
					self.display_grid()
					newGame = self.play(self.turn)
				except Exception:
					print("Game interrupted by the other player")
					break

				print("Bye !! Have a nice day :)")
				self.s.close()

		except Exception as e:
			print(e)
			self.s.close()

	def ask_to_save(self):
		while True:
		    try:
		    	tosave = input('Would you like to save the game ? \nIf a saved game exist it will be erased !!\n Press (y/n) to confirm:')
		    	print("By savin")
		    except ValueError:
		        print("Sorry, I didn't understand that.")
		        continue
		    else:
		        break
		while tosave != 'y' and tosave != 'n':
			print("You did not enter y or n")
			while True:
			    try:
			        tosave = input('Would you like to save the game ? \nIf a saved game exist it will be erased !!\n Press (y/n) to confirm:')
			    except ValueError:
			        print("Sorry, I didn't understand that.")
			        continue
			    else:
				    break
		if tosave == 'y':
			Fichier = open('data.txt','wb')
			pickle.dump(self.grid,Fichier)    # sérialisation
			Fichier.close()
		else:
			pass

	def ask_to_retrieve(self):
		while True:
		    try:
		    	toretrieve = input('Would you like to retrieve a saved game ?(y/n) :')
		    except ValueError:
		        print("Sorry, I didn't understand that.")
		        continue
		    else:
		        break
		while toretrieve != 'y' and toretrieve != 'n':
			print("You did not enter y or n")
			while True:
			    try:
			        toretrieve = input('Would you like to retrieve a saved game ?(y/n) :')
			    except ValueError:
			        print("Sorry, I didn't understand that.")
			        continue
			    else:
				    break
		if toretrieve == 'y':
			try:
				Fichier =open('data.txt', 'rb')
				self.grid = pickle.load(Fichier) 
				Fichier.close()
				self.conn.sendall(b'R')
				self.conn.sendall(b'M')
			except IOError:
				print("No saved game were found")
				self.conn.sendall(b'N')
				self.conn.sendall(b'M')
		else:
			self.conn.sendall(b'N')
			self.conn.sendall(b'M')
					
	def connect(self):
		self.conn, self.addr = self.s.accept()
		print('Connection address:', self.addr)


	def play(self, turn):
		a, b = 0, 0
		self.victory = 0
		print('test')
		while True:
			if self.turn == 0:
				print('test1')
				a, b = self.ask_action()
				a +=1
				b +=1
				packed_int = pack("i", a)
				packed_int1 = pack("i", b)
				self.conn.sendall(packed_int)
				self.conn.sendall(packed_int1)
				self.conn.sendall(b'M')
				a -= 1
				b -= 1
				if a ==10 and b ==10:
					self.ask_to_save()
					break
				self.grid[a][b] = self.chip1
				self.display_grid()
				self.victory_conditions(a,b)
				if self.victory ==1:
					print("You won the match !!")
					return self.new_game()
				print ("Waiting for player 2 to play ...")
				self.turn=1
			else:
				data = b'\x00'
				data1 = [0,0]
				i = 0
				data = self.conn.recv(5)
				while data1[0]==0 or data1[1]==0:
					for x in range(len(data)):
						if data[x] != 0:
							data1[i] = data[x]
							i += 1
					if i < 2:
						data = self.conn.recv(5)
					else:
						break
				if data1[0] - 1 ==10 and data1[1] -1 ==10:
					print("Your opponent saved the game and left.")
					self.ask_to_save()
					break
				self.grid[data1[0]-1][data1[1]-1] = self.chip2
				self.display_grid()
				self.victory_conditions(data1[0]-1, data1[1]-1)
				if self.victory ==1:
					print("You lost the match !!")
					return self.new_game()
				self.turn=0
				
	def new_game(self):
			while True:
			    try:
			    	retry = input('Would you like to play another game ?(y/n) :')
			    	print("test:",retry)
			    except ValueError:
			        print("Sorry, I didn't understand that.")
			        continue
			    else:
			        break
			while retry != 'y' and retry != 'n':
				print("You did not enter y or n")
				while True:
				    try:
				        retry = input('Would you like to play another game ?(y/n) :')
				    except ValueError:
				        print("Sorry, I didn't understand that.")
				        continue
				    else:
					    break
			return retry
	def grid_initialisation(self):
		
		for c in range(7):
			self.grid.append([])
			for l in range(6):
				self.grid[c].append(' ')


	def display_grid(self):
		#Use of the for loop to go through the grid and display the grid it self
		#and the caracters contained in the grid
		print ("+---+---+---+---+---+---+---+")
		for l in range(6):
			print ('|', end='')
			for c in range(7):
				#Display the grid from the global grid
				print ('',self.grid[c][l],'|', end ="")
			print(" \n+", end='')
			for c in range(7):
				print ("---+", end ='')
			print("")
		#Displays the number of column the player can play 
		print ("  1   2   3   4   5   6   7")


	def ask_action(self):
		while True:
		    try:
		        action = int(input('Enter a column between 1 and 7, enter 0 to leave the game:'))
		    except ValueError:
		        print("Sorry, I didn't understand that.")
		        continue
		    else:
		        break
		while action <0 or action >7:
			print("You did not enter a column between 1 and 7 or 0 to leave")
			while True:
			    try:
			        action = int(input('Enter a column between 1 and 7, enter 0 to leave the game:'))
			    except ValueError:
			        print("Sorry, I didn't understand that.")
			        continue
			    else:
			        break
		
		if action ==0:
			return 10, 10
		else:
			col = action - 1
			line = 0
			for l in range(6):
				if self.grid[col][l] == ' ':
					line = l
			return col, line

	def victory_conditions(self, column, line):
		nb, nb1, nb2, nb3, nb4, nb5, nb6, nb7 = 1, 1, 1, 1, 1, 1, 1, 1
		for x in range(1,4):
			try:
				if self.grid[column][line]== self.grid[column + x ][line]:
					if column + x <7:
						nb+=1
			except IndexError:
				pass
			try:
				if self.grid[column][line]== self.grid[column - x ][line]:
					if column - x >=0:
						nb1+=1
			except IndexError:
				pass
			try:
				if self.grid[column][line]== self.grid[column][line + x]:
					if line + x <6:
						nb2+=1
			except IndexError:
				pass
			try:
				if self.grid[column][line]== self.grid[column][line - x]:
					if line - x >=0:
						nb3+=1
			except IndexError:
				pass
			try:
				if self.grid[column][line]== self.grid[column + x ][line + x]:
					if column + x <7 and line + x <6:
						nb4+=1
			except IndexError:
				pass
			try:
				if self.grid[column][line]== self.grid[column - x ][line - x]:
					if column - x >=0 and line - x >=0:
						nb5+=1
			except IndexError:
				pass
			try:
				if self.grid[column][line]== self.grid[column + x ][line - x]:
					if column + x <7 and line - x>=0:
						nb6+=1
			except IndexError:
				pass
			try:
				if self.grid[column][line]== self.grid[column - x][line + x]:
					if column - x >= 0 and line + x <6:
						nb7+=1
			except IndexError:
				pass
		if (nb >3 or nb1 >3 or nb2 >3 or nb3 >3 or nb4 >3 or nb5 >3 or nb6 >3 or nb7 >3):
			self.victory = 1
		else:
			pass
