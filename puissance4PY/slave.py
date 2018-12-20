"""
   Pierre Coiffey
   Connect 4 game v1 : slave.py
   Python 3
   30/08/2018
   Telnet console game.
   2 players

   Rules :
   To win Connect Four, all you have to do is connect four
   of your chip pieces in a row. This can be done 
   horizontally, vertically or diagonally. Each player 
   will drop in one chip piece at a time. This will give 
   you a chance to either build your row, or stop your 
   opponent from getting four in a row.

	Requirements :

	This game requires Python3, an internet connexion,
	telnet, an other player with the master.py program 
	and the same configuration. The game was developed
	under linux, the colored chips won't work under
	windows and cause trouble to the grid.

	How to launch :

	To be able to launch the program the master 
	player must have already started the master.py.

	To play this game enter the following command.

	python3 slave.py

	The game will start and you will be able to play.
	Follow the inscription in the program to leave the 
	game, save a game play.

	Careful only the master player can save a game
	and retrieve it. 
"""
#The pickle module implements a fundamental algorithm 
#for serializing and de-serializing a Python object 
#structure. Used to format the grid in order to write 
#it inside the grid
import pickle 
#The telnetlib module provides a Telnet class that 
#implements the Telnet protocol. It is used to communicate
#with the master player (send and receive data)
import telnetlib
#This module performs conversions between Python values 
#and C structs represented as Python strings. 
#This can be used in handling binary data from network 
#connections. Here it is use to format data to send. 
from struct import *
#Funtion called when the program  receive a sequence of data
#Has the grid as an argument
#Return the grid
def ask_to_retrieve(grid):
		try:	
			#try to open the file data.txt mode reading binary
			Fichier =open('data.txt', 'rb')
			#use of pickle (see import) to load the saved grid
			#into the grid variable 
			grid = pickle.load(Fichier) 
			#Close the file
			Fichier.close()
			#returns the saved grid
			return grid
		#If no data.txt exist, this exception catches it
		except IOError:
			print("No saved game were found")
			#we still return the grid in order for the main 
			#program not to have an empty return
			return grid
#Function that save the game inside an text file
#Has the grid as an arguments
#Returns nothing		
def save(grid):
		try:
			#Create/open the text file save_game.txt with the option wb that 
			#rewrite the content of the file (the previous content will be erased)
			Fichier = open('data.txt','wb')
			#use of pickle (see import) to dump the grid inside the file 
			#data.txt
			pickle.dump(grid,Fichier) # sérialisation
			#Once its done close the file
			Fichier.close()
		except Exception as e:
			print(e)
#Function that initialise the grid caracters
#Returns nothing
#has the grid for arguments
def grid_initialisation(grid):
		#Initialisation of the grid with the space caractere
		for c in range(7):
			grid.append([])
			for l in range(6):
				grid[c].append(' ')

#Function that displays the grid, it uses printf and the global grid to display the actual 
#state of the grid. 
#Returns nothing, has no arguments		
def display_grid(grid):
	#Use of the for loop to go through the grid and display the grid it self
	#and the caracters contained in the grid
	print ("+---+---+---+---+---+---+---+")
	for l in range(6):
		print ('|', end='')
		for c in range(7):
			#Display the grid from the global grid
			print ('', grid[c][l],'|', end ="")
		print(" \n+", end='')
		for c in range(7):
			print ("---+", end ='')
		print("")
	#Displays the number of column the player can play 
	print ("  1   2   3   4   5   6   7")
"""
Function that verify if a player win, there are 8 conditions 
to win, when there are four pawns aligned either in vertical, 
horizontal, left diagonal, right diagonal. The arguments of the
function are the grid, the column and the line where the last chip was
played. To verify the alignments, we use the current position stored in the 
structure and iterate with a for loop on the grid to check each 
directions previously cited. Counters will increase with every same
caracter for each direction. If a count reach four, then we go to 
the victory condition and anounce the winner thanks to the arguments
player turn. Finnally we ask if the players wants to play another 
game and return the result as an int. 
"""
def victory_conditions(grid, column, line):
	#declaration of the counter
	nb, nb1, nb2, nb3, nb4, nb5, nb6, nb7 = 1, 1, 1, 1, 1, 1, 1, 1
	for x in range(1,4):
		try:
			#Verification of the horizontal right direction 
			if grid[column][line]== grid[column + x ][line]:
				if column + x <7:
					#incrementation of the counter if a same chip is found
					nb+=1
		#if because of the incrementation is the postion is outside
		#the grid then this exception happens
		except IndexError:
			pass
		try:
			#Verification of the horizontal left direction
			if grid[column][line]== grid[column - x ][line]:
				if column - x >=0:
					nb1+=1
		except IndexError:
			pass
		try:
			#Verification of the vertical direction top
			if grid[column][line]== grid[column][line + x]:
				if line + x <6:
					nb2=nb2+1
		except IndexError:
			pass
		try:
			#Verification of the vertical direction bottom
			if grid[column][line]== grid[column][line - x]:
				if line - x >=0:
					nb3+=1
		except IndexError:
			pass
		try:
			#Verification of the diagonal direction bottom left to top right (top)
			if grid[column][line]== grid[column + x ][line + x]:
				if column + x <7 and line + x <6:
					nb4+=1
		except IndexError:
			pass
		try:
			#Verification of the diagonal direction bottom left to top right (bottom)
			if grid[column][line]== grid[column - x ][line - x]:
				if column - x >=0 and line - x >=0:
					nb5+=1
		except IndexError:
			pass
		try:
			#Verification of the diagonal direction bottom right to top left (bottom)
			if grid[column][line]== grid[column + x ][line - x]:
				if column + x <7 and line - x>=0:
					nb6+=1
		except IndexError:
			pass
		try:
			#Verification of the diagonal direction bottom right to top left (top)
			if grid[column][line]== grid[column - x][line + x]:
				if column - x >= 0 and line + x <6:
					nb7+=1
		except IndexError:
			pass
	#Verification of the counters 
	if (nb >3 or nb1 >3 or nb2 >3 or nb3 >3 or nb4 >3 or nb5 >3 or nb6 >3 or nb7 >3):
		victory = 1
		#Case of the victory
		return victory
	else:
		#Case not a victory
		victory = 0
		return victory
#Function that ask for the player if he wants to play another game
#Has no arguments
#Returns nothing
def new_game():
	#Loops until a char is entered
	while True:
	    try:
	    	retry = input('Would you like to play another game ?(y/n) :')
	    except ValueError:
	        print("Sorry, I didn't understand that.")
	        continue
	    else:
	        break
	#If the player did not enter y or n then the 
	#the player will be asked to enter y or n until the 
	#player gets it right
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
#Function that ask for user input 
#and check whether it is a proper input or not
#Has the grid as an arguments
#Returns the input from the user once it has been 
#validated in the form of two int (colum, line); 
#10, 10 if the player wishes to leave the game 
#11, 11 if the column is full
def ask_action(grid):
		#while loop to accept only an int 
		while True:
		    try:
		        action = int(input('Enter a column between 1 and 7, enter 0 to leave the game:'))
		    #the following exception activate when another
	    	#type is enter like a char 
		    except ValueError:
		        print("Sorry, I didn't understand that.")
		        continue
		    else:
		        break
		#this while loop makes sure that the colum
		#entered by the player is within the grid
		while action <0 or action >7:
			print("You did not enter a column between 1 and 7 or 0 to leave")
			#while the player doesn't enter a right column
			#he will be asked again
			while True:
			    try:
			        action = int(input('Enter a column between 1 and 7, enter 0 to leave the game:'))
			    except ValueError:
			        print("Sorry, I didn't understand that.")
			        continue
			    else:
			        break
		#Case:the player want to leave the game
		if action ==0:
			return 10, 10
		else:
			#A for loop is used to go backward through the grid to do the 
 			#transcription of the column entered into a [column] [line] position 
 			#within the grid by verifying if the column is empty
			col = action - 1
			line = 0
			for l in range(6):
				if grid[col][l] == ' ':
					line = l
			#Verification that the position is empty
			#if so returns the position of the chip on
			#the grid
			if grid[col][line] == ' ':	
				return col, line
			#Case: the player played in a full column
			else:
				print("You can't play here...")
				return 11, 11
#Function that check if the grid is complete
#Returns an int to know if the grid is complete or not
#Has the grid as an arguments
def grid_complete(grid):
	#Double for loop to go through the grid to check whether
	#it finds a space inside the global grid
	for c in range(7):
		for l in range(6):
			if grid[c][l] ==' ':
				return 0
"""
Function where most of the game happens, where the data is sent.
Has the variables turn and the grid, the object conn, the char chip1 
and chip2 has arguments.
Inside a while loop, the function ask the player for a column where
to play then send the position column line through the connexion. 
Then it verify if the player wants to leave thus asking to save.
Next the function displays the grid updated, checks the victory conditions
and do the same backward to receive the position from the other player.
"""		
def play(tn, turn, grid, chip1, chip2):
	#variable for column, line received
	a, b = 0, 0
	#initialisation for the victory state
	victory = 0
	#initialisation for the grid state
	grid_state = 1
	#the function loops until one of the conditions which
	#contains a return is fullfilled
	while True:
			#depending on the value of turn the function either asks
			#a column to the player or wait to receive a position 
			#from the other player
			#This part is to receive the other player grid 
			#position, it's the other player's turn
			if turn == 0:	
				print ("Waiting for player 1 to play ...")
				data = b'\x00\x00'
				data = tn.read_until(b'M')
				if data[0] - 1 ==10 and data[1] -1 ==10:
					print("Your opponent left the game.")
					save(grid)
					break
				grid[data[0]-1][data[1]-1] = chip1
				display_grid(grid)
				#check for the victory and if the grid is complete
				victory = victory_conditions(grid, data[0]-1, data[1]-1)
				grid_state = grid_complete(grid)
				#Condition fullfilled if the grid is not full
				if grid_state == 0:
					pass
				#when the grid is full the function returns the result
				#of the new_game() function (y or n)
				else:
					print("Equality the grid is complete")
					turn =1
					return new_game()
				#Condition fullfilled if the player won the game the 
				#function returns the result of new_game() (y or n)
				if victory ==1:
					print("You lost the match !!")
					turn =1
					return new_game()
				turn = 1
			else:
				a, b = ask_action(grid)
				#Loop in case the player played in a full column
				while a==11 or b==11:
					a, b = ask_action(grid)
				#add 1 to a and b in order to be able
				#to send the 0
				a+=1
				b+=1
				#use of the struct module with the method pack
				#to format the data to send.
				#On python docs :Return a string containing the values v1, v2, ...
				#packed according to the given format. 
				#The arguments must match the values required by the format exactly.
				packed_int = pack("i", a)
				packed_int1 = pack("i", b)
				#Send the data on the tn object with the method write()
				#from the module telnetlib
				tn.write(packed_int)
				tn.write(packed_int1)
				#substract a and b to return to the originals
				a-=1
				b-=1
				#condition if the player whishes to leaves
				#started when the player enters 0
				if a ==10 and b ==10:
					save(grid)
					break
				#assign the chip to its grid position 
				grid[a][b] = chip2
				display_grid(grid)
				victory = victory_conditions(grid, a, b)
				grid_state = grid_complete(grid)
				#Condition fullfilled if the grid is not full
				if grid_state == 0:
					pass
				#when the grid is full the function returns the result
				#of the new_game() function (y or n)
				else:
					print("Equality the grid is complete")
					turn = 0
					return new_game()
				#Condition fullfilled if the player won the game the 
				#function returns the result of new_game() (y or n)
				if victory ==1:
					print("You won the match !!")
					turn = 0
					return new_game()
				turn=0
				
#Main function of the program where the functions are called 
#in order to complete the program
if __name__ == '__main__':
	
	#interface presentation of the 4 connect 
	print ("***********************************")
	print ("*				  *")
	print ("*  Welcome to the 4 connect game  *")
	print ("*				  *")
	print ("***********************************\n")
	#Initialisation of the chips
	#Use of ANSI escape sequences to output the 
	#chip in color inside the terminal 
	#'\033[94m' BLEU
	#'\033[91m' ROUGE
	#'\033[0m'  ENDC
	chip1= '\033[94m' + "X" + '\033[0m'
	chip2 = '\033[91m' + "O" + '\033[0m'
	#Initialisation of variables for the program
	victory = 0
	turn = 0
	grid = []
	conn = None

	#try catch exception where the functions/method are called
	try:
		#initialisation of a connexion object with the method Telenet
		#from the module telnetlib
		tn = telnetlib.Telnet('127.00.1', '8023')#adress ip public to reach and port
		print('Connected to Master')
		print("Waiting for master to decide whether to continue a saved game ...")
		ret = b'\x00\x00'
		#Use of the read_until method from the module telnetlib
		#the method reads the connexion until master player
		#send the b'M' signal to stop reading
		ret = tn.read_until(b'M')
		#initialisation of two counter
		p1, p2 = 0,0
		#If b'RM' is read on the connexion then it
		#means that the master player wants to start
		#a saved game so we retrieve the saved game
		if ret == b'RM':
			print("Starting saved game ...")
			grid = ask_to_retrieve(grid)
			#Double for loop to count the number of X and O in 
			#the grid to check which player should play
			#increment players counter when a match is found
			for c in range(7):
				for l in range(6):
					if grid[c][l] == chip1:
						p1 = p1+1	
					elif grid[c][l] == chip2:
						p2 = p2+1
					else:
						pass
			#change the value of turn depending of the 
			#number of each chip in the grid
			if p2<p1:
				turn =1
		else:
			pass
		#call the function that initialise the grid
		grid_initialisation(grid)
		#call the function that displays the grid
		display_grid(grid)
		#char initialisation
		newGame = ' '
		#Call the function where most of the game is played
		#the function will can return y, n or nothing
		#the program will end 
		newGame = play(tn, turn, grid, chip1, chip2)
		#If the play function returns y then the function 
		#to play will be called
		while newGame == 'y':
			#reinitialisation of the grid
			grid = []
			try:
				#function that initialise the grid 
				#with the new grid = [] for argument
				grid_initialisation(grid)
				#then diplays it
				display_grid(grid)
				newGame ='y'
				#The play function is then called again 
				#the program will stay in this while loop
				#untill the program fullfill one of the condition
				#to trigger the return of n or nothing
				newGame = play(tn, turn, grid, chip1, chip2)
			except Exception as e:
				print(e)
				print("Game interrupted by the other player")
				break
		#If the play function return n or nothing then the
		#socket is closed
		print("Bye !! Have a nice day :)")
		tn.close()
	#will print the Exception in case something wrong happens
	except Exception as e:
		print(e)
		print("Please try again")