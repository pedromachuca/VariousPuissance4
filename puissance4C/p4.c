/* Pierre Coiffey
   Connect 4 game v1
   30/07/2018
   You can play this game localy.
   2 players

   Rules :
   To win Connect Four, all you have to do is connect four
   of your chip pieces in a row. This can be done 
   horizontally, vertically or diagonally. Each player 
   will drop in one chip piece at a time. This will give 
   you a chance to either build your row, or stop your 
   opponent from getting four in a row.

	How to launch :

	To play the game please compile this code 
	with the following command line.

	gcc p4.c -o p4

	The to launch the game enter the following command line

	./p4

	The game will start and you will be able to play.
	Follow the inscription in the program to leave the 
	game, save a game play.
*/
#include <stdio.h> 
#include <stdlib.h>
//Global grid definition
char grid[7][6];
//Definition of a global structure for the grid
struct position{
	int column;
	int line;
};
//Function that initialise the grid caracters
//Returns nothing, has no arguments
void grid_initialisation(void)
{
	int column = 0;
	int line = 0;
	//Initialisation of the grid with the space caractere
	for(column = 0; column < 7; column++){
		for(line = 0; line < 6; line++){
			grid[column][line] = ' ';
		}
	}
}
//Function that displays the grid, it uses printf and the global grid to display the actual 
//state of the grid. 
//Returns nothing, has no arguments
void display_grid(void){
	int column = 0;
	int line = 0;
	//Use of the for loop to go through the grid and display the grid it self
	//and the caracters contained in the grid
	printf ("+---+---+---+---+---+---+---+\n"); 
    for(line = 0; line < 6; line++){
		printf("|");
		for(column = 0; column < 7; column++){
			//Display the grid from the global grid
			printf(" %c |", grid[column][line]);
		}
		printf("\n+");
		for(column = 0; column < 7; column++){
			printf("---+");
		}
		printf("\n");
	}
	//Displays the number of column the player can play 
	printf("  1   2   3   4   5   6   7\n");
}
//Definition of the position on the grid
//Returns nothing, the arguments are the column entered by the player and 
//the pointeur on the object pos, the global structure used to fill the grid
void grid_position(int input, struct position *pos){
	
	int ln;
 	//A for loop is used to go backward through the grid to do the 
 	//transcription of the column entered into a [column] [line] position 
 	//within the grid by verifying if the column is empty
   	pos->column = input -1;
   	for(ln = 5; ln >= 0; --ln){
   		if(grid[pos->column][ln] == ' '){
       		pos->line = ln;
            break;
		}
	}
}
//Function that check if the grid is complete
//Returns an int to know if the grid is complete or not
//Has no arguments
int grid_complete(void){
	int column = 0; 
	int line = 0;
	//Double for loop to go through the grid to check whether it finds
	//a space inside the global grid
	for(column = 0; column < 7; column++){
		for(line = 0; line < 6; line++){
			if(grid[column][line] == ' '){
				return 0;
			}
		}
	}
	//Return 1 if no empty space are found
	//It allows us to know that the grid is full
	return 1;
}
//Function that ask for user input 
//and check whether it is a proper input or not
//Has no arguments
//Returns the input from the user once it has been validated
int ask_action(void){	
	int status, input;
	char tmp;
	//to verify that the scanf gets an int we will check the 
	//return of scanf, it has to be 1
	status = scanf("%d", &input);
	//while loop use that loops until the user input is correct
	while(status!=1 || input < 0 ||input >= 8){
		//while loop to to empty scanf buffer to make sure there are no errors
		while((tmp=getchar()) != EOF && tmp != '\n');
		printf("Incorrect number... please try again: ");
		status = scanf("%d", &input);
	}
	return input;
}
/*Function that verify if a player win, there are 8 conditions 
to win, when there are four pawns aligned either in vertical, 
horizontal, left diagonal, right diagonal. The arguments of the
function are the player turn as well as the the pointeur on the 
object pos, the global structure used to fill the grid. To verify 
the alignments, we use the current position stored in the 
structure and iterate with a for loop on the grid to check each 
directions previously cited. Counters will increase with every same
caracter for each direction. If a count reach four, then we go to 
the victory condition and anounce the winner thanks to the arguments
player turn. Finnally we ask if the players wants to play another 
game and return the result as an int. */ 
int victory_conditions(int turn, struct position *pos){
	//declaration of the counter
	int nb = 1, nb1 = 1, nb2 = 1, nb3 =1, nb4 = 1, nb5 = 1, nb6 = 1, nb7 =1;
	for (int i = 1; i < 4; ++i){
		//Verification of the horizontal right direction 
	 	if (grid[pos->column][pos->line]== grid[pos->column + i ][pos->line]){
	 		if (pos->column +i <= 6){nb++;}	  	
	 	}
	 	//Verification of the horizontal left direction
	 	if (grid[pos->column][pos->line]== grid[pos->column - i ][pos->line]){
	 		if (pos->column - i >=0){nb1++;}	  	
	 	}
		//Verification of the vertical direction top
	 	if (grid[pos->column][pos->line]== grid[pos->column][pos->line +i]){
	 		if (pos->line + i <=5){nb2++;}	  	
	 	}
	 	//Verification of the vertical direction bottom
	 	if (grid[pos->column][pos->line]== grid[pos->column][pos->line - i]){
	 		if (pos->line - i >=0){nb3++;}
	 	}	 	
		//Verification of the diagonal direction bottom left to top right (top)
	 	if (grid[pos->column][pos->line]== grid[pos->column + i ][pos->line + i]){
	 		if (pos->column + i <=6 && pos->line + i <=5){nb4++;}
	 	}  
	 	//Verification of the diagonal direction bottom left to top right (bottom)
	 	if (grid[pos->column][pos->line]== grid[pos->column - i ][pos->line - i]){
	 		if (pos->column - i >=0 && pos->line - i >=0){nb5++;}	  	
	 	}   
		//Verification of the diagonal direction bottom right to top left (bottom)
	 	if (grid[pos->column][pos->line]== grid[pos->column + i ][pos->line - i]){
	 		if (pos->column + i <=6 && pos->line - i >=0){nb6++;}	  	
	 	}  
	 	//Verification of the diagonal direction bottom right to top left (top)
	 	if (grid[pos->column][pos->line]== grid[pos->column - i ][pos->line + i]){
	 		if (pos->column - i >=0 && pos->line + i <=5){nb7++;} 	  	
	 	} 
	} 
	//Verification of the counters 
	if (nb >3||nb1 >3||nb2 >3||nb3 >3||nb4 >3||nb5 >3||nb6 >3||nb7 >3){
		//asking the player if he wants to play another game
		//verificationo of the input as detailed previously
		char c, tmp;
		printf("Player %d wins the game !! \n", turn%2 +1);
		printf("Do you wish to play another game ? Enter (y/n):");	
		int status = scanf(" %c", &c);
		while(status!=1 || c != 'y'&& c != 'n'){
			//while loop to to empty scanf buffer to make sure there are no errors
			while((tmp=getchar()) != EOF && tmp != '\n');
			printf("Incorrect char... please try again: ");
			status = scanf(" %c", &c);
		}	
		//Return 0 if the players wants to play another game
		if (c == 'y'){
			printf("\n\n\n\n\n\n\n\n");
			return 0;
		}
		//Return 1 if the players wants to exit the game 
		else{
			printf("Hope you enjoyed it !! See you later !!\n\n");
			return 1;
		}
	}
	//As the game continue we need to check
	//if the grid is full as it is a victory condition, 
	//to do so we call the function that go through the grid
	//to check if it finds an empty space
	else if(grid_complete() == 1){		
		printf("Equality the grid is complete !! \nDo you wish to play another game ? Enter (y/n):\n");
		char t, tmp;
		int status = scanf(" %c", &t);
		while(status!=1 || t != 'y'&& t != 'n'){
			//while loop to to empty scanf buffer to make sure there are no errors
			while((tmp=getchar()) != EOF && tmp != '\n');
			printf("Incorrect char... please try again: ");
			status = scanf(" %c", &t);
		}	
		//Return 0 if the players wants to play another game
		if (t == 'y'){
			printf("\n\n\n\n\n\n\n\n");
			return 0;
		}
		//Return 1 if the players wants to exit the game 
		else{
			printf("Hope you enjoyed it !! See you later !!\n\n");
			return 1;
		}
	}
	//If no victory conditions were fullfiled return 2
	else{return 2;}
	//The returns allows for the main function to decide whether or not 
	//to continue, stop or restart the program
}
//Function that save the game inside an text file
//Has no arguments
//Returns nothing
void save_game(void){
	//Initialisation of the file
	FILE* file = NULL;
	//Create/open the text file save_game.txt with the option w+ that 
	//rewrite the content of the file (the previous content will be erased)
	file = fopen("saved_game.txt", "w+");
	//Check if there is an issue with the file
    if(!file){
        printf("There was an issue with the file\n");;
    }
    //Write the grid content inside the text file
    fwrite(grid, sizeof grid, sizeof *grid, file);
    //Closes the file
    fclose(file);
}
//Function that retrieve the saved grid from previous games
//Has no arguments
//Returns nothing 
void retrieve_game(void){	
	//Initialisation of the file
	FILE* file = NULL;
	//Open the text file saved_game.text in read mode
	file = fopen("saved_game.txt", "r");
    //Check if there is an issue with the file
    if(!file){
        printf("There was an issue with the file\n");;
    }
   //Reads the content of the file, the grid is set to 
   //its previously saved state
   fread(grid, sizeof grid, sizeof *grid, file);
   //Closes the file
   fclose(file);
}
/*Function that count the caracter of the player 1 and 2
which are on the grid. Thanks to this we can know which player's
turn it is so that in case of a saved game, the right player is 
asked to play. I could have used instead the turn variable as an argument 
for the retrieve_game function.*/ 
//Returns the players turn
//Has no arguments
int player_turn(void){
	//Initialisation of the counter and the indexes for the loops.
	int player1 = 0, player2 = 0, column = 0, line = 0;
	//Double loop to count the number of X on the grid
	for(column = 0; column < 7; column++){
		for(line = 0; line < 6; line++){
			if(grid[column][line] == 'X')
				player1++;
		}
	}
	//Double loop to count the number of O on the grid
	for(column = 0; column < 7; column++){
		for(line = 0; line < 6; line++){
			if(grid[column][line] == 'O')
				player2++;
		}
	}
	//If the number of X is inferior or equal to the number of
	//O, then it is the first player's turn to play
	if (player1<=player2){return 2;}
	else{return 3;}
}
//Main function of the program where the functions are called 
//in order to complete the program
int main(void) {
	printf("\n\nWelcome to the P4 game !!\n\n");
	//Initialisation of the structure pos
	struct position pos;
	int turn = 2;
	int v = 0;
	char tmp;
	//Call for the function that initialises the grid
	grid_initialisation();
	//First we check if there is a saved game and if the player wants
	//to continue the game or play a new one
	char u;
	//Initialisation of the file
	FILE * file =NULL;
	//Opening the file
	file = fopen("saved_game.txt", "r");
	//Check if a game is saved
	if (file != NULL){	
		//A game has been saved so we ask the player if he wants to continue 
		printf("A saved game exist do you wish to continue the saved game ?\n");
		printf("Press (y/n) to confirm:");
		//We check if it is a proper input with the return of the scanf function 
		//and the while loops
		int status = scanf(" %c", &u);	
		while(status!=1 || u != 'y'&& u != 'n'){
			//while loop to to empty scanf buffer to make sure there are no errors
			while((tmp=getchar()) != EOF && tmp != '\n');
			printf("Incorrect char... please try again: ");
			status = scanf(" %c", &u);
		}	
		//Condition if the player chooses to continue the game
		if (u == 'y'){
			//We call the function that will load the grid from
			//the saved game
			retrieve_game();
			//We call the player_turn function to 
			//assign the real turn to the variable
			turn = player_turn();
			//We notify the user that the game has been retrieved
			printf("The game was retrieved\n");
		}
		//If the player enter n then the program continues
	}
	//We call the function that will display the current state of the gris
	display_grid();
	int input;
	//Then whether or not it's a new or a saved game we execute the
	//principal while loop where most of the program takes places.
	while(1){
		printf("Player %d enter a column [1-7] or 0 to leave the game: ", turn%2+1);
		//Call for the function that asks for the column the player wants to play in 
		//and that verify if the input is correct, the return of the function is stored
		//inside the input variable
		input = ask_action();
		//The players can leave the game by pressing 0 when asked to play.
		//The wile loop is here for this case, the players will be asked if
		//they wants to save the game. The input needs to be verified. 
		while(input == 0){
			char c;
			printf("Do you wish to save the game before leaving ?\n");
			printf("If a saved game exist it will be erased ? Press (y/n) to confirm:");		
			//verification of the player input
			int status = scanf(" %c", &c);
			while(status!=1 || c != 'y'&& c != 'n'){
				//while loop to to empty scanf buffer to make sure there are no errors
				while((tmp=getchar()) != EOF && tmp != '\n');
				printf("Incorrect char... please try again: ");
				status = scanf(" %c", &c);
			}	
			//Condition if the player chose to save the game.
			if (c == 'y'){
				//Call for the function to save the grid state 
				//then exit the program with success
				save_game();
				return EXIT_SUCCESS;
			}
			else{
				//The player did not want to save the game so we exit the 
				//program with a return exit success, the game will not be saved.
				return EXIT_SUCCESS;
			}
		}
		//After the column is choosen, we call the function that
		//will transform the column enter into a [column][line] 
		//position on the grid and stores it in the structure pos.
		grid_position(input, &pos);
		//with the structure we can now fill the grid with the 
		//corresponding player caracter
		//Use of the turn variable to know which caracter to use
		if (grid[pos.column][pos.line] == ' '){
			if(turn%2 == 0){
				//Call for the pos structure at the 
				//previously defined position on the 
				//global grid with player 1 caracter
				grid[pos.column][pos.line] = 'X';
			}
			else{
				//Call for the pos structure at the 
				//previously defined position on the 
				//global grid with player 2 caracter
				grid[pos.column][pos.line] = 'O';
			}
		//We call again the display function 
		//to display the grid updated		
		display_grid();
		//Finally, it is time to check for the victory conditions
		//the function is called with the arguments previously 
		//detailed inside the function
		v = victory_conditions(turn, &pos);
		turn++;
		}
		//if the return is 1, we exit the game, one of the 
		//player won and they want to stop playing
		if (v==1){
			return EXIT_SUCCESS;
		}
		//No victory conditions were fullfiled,
		//the principal while loop continues, ie the 
		//game continues
		else if(v == 2){
			continue;
		}
		//The players wants to do another game.
		//We thus reinitialise the grid and 
		//the player's turn in order to start 
		//from scratch and the principal while 
		//loop continues ie the game continues
		else{
			printf("Welcome to the P4 game !!\n");
			grid_initialisation();
			display_grid();
			turn =2;
		}
	}
	return 0;
}
