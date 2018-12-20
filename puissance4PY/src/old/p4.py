#!/usr/bin/env python
#-*- coding:utf-8 -*-

chip1 = '\033[94m' + "X" + '\033[0m'
chip2 = '\033[91m' + "O" + '\033[0m'
grid = []
col = 7
line = 6


def grid_initialisation():
	for l in range(line):
		grid.append([])
		for c in range(col):
			grid[l].append(' ')

def display_grid():
	#Use of the for loop to go through the grid and display the grid it self
	#and the caracters contained in the grid
	print ("+---+---+---+---+---+---+---+")
	for l in range(line):
		print ('|', end='')
		for c in range(col):
			#Display the grid from the global grid
			print (grid[l][c],' |', end ="")
		print(" \n+", end='')
		for c in range(col):
			print ("---+", end ='')
		print("")
	#Displays the number of column the player can play 
	print ("  1   2   3   4   5   6   7")

def ask_action():
	line = 0
	action = int(input('Entrer une colone :'))
	col = action - 1
	for l in range(line):
		if grid[col][l] == ' ':
			line = l

	return col, line

def menu():
	print ("***********************************")
	print ("*				  *")
	print ("*  Welcome to the 4 connect game  *")
	print ("*				  *")
	print ("***********************************\n")
	print ("To continue, choose an option from the menu (1 or 2):")
	print ("1- Local game")
	print ("2- Online game")

	while True:
	    try:
	        choice = int(input())
	    except ValueError:
	        print("Sorry, I didn't understand that.")
	        continue
	    else:
	        break
	while choice !=1 or choice!=2:
		if choice == 1:
			return choice
		elif choice == 2:
			return choice
		else:
			print("You did not enter 1 or 2")
		print ("To continue, choose an option from the menu (1 or 2):")
		while True:
		    try:
		        choice = int(input())
		    except ValueError:
		        print("Sorry, I didn't understand that.")
		        continue
		    else:
		        break
	return choice

if __name__ == "__main__":
	menu()
	#print "chip1 :", chip1, "\nchip2 :", chip2
	grid_initialisation()
	#ask_action()
	display_grid()
	a, b = ask_action()
	grid[a][b] = chip2
	display_grid()


