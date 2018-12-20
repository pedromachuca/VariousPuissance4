#include <stdio.h> 
#include <ctype.h>

int input_validation(int column);
int demande_action(int *);
int vider_tampon(FILE *);
//Global grid definition
char grid[7][6];

//Definition of a global object for the grid
struct position{

	int column;
	int line;
};

//Fonction that initialise the grid caracters  OK
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
//Fonction to display the grid OK
void display_grid(void){

	int column = 0;
	int line = 0;

	printf ("+---+---+---+---+---+---+---+\n"); 

        for(line = 0; line < 6; line++){
		printf("|");
		for(column = 0; column < 7; column++){

			printf(" %c |", grid[column][line]);
		}
		printf("\n+");

		for(column = 0; column < 7; column++){
			printf("---+");
		}
		printf("\n");
	}

	printf("  1   2   3   4   5   6   7\n");

}

//Definition of the position on the grid
int grid_position(int input, struct position *pos){
	
	int ln;
 	//transcription of the column entered into a line column position within the grid
   	//by verifying if the column is empty
   	pos->column = input -1;
   	for(ln = 5; ln >= 0; --ln){
   		if(grid[pos->column][ln] == ' '){
           		pos->line = ln;
                   	break;
		}
	}
}
//Fonction that verify if the user input is valid OK
int input_validation(int column){

	if(column <= 0 || column >= 8){
		return 1;
	}
	else {
		return 0;
	}
}

//Fonction that check if he grid is complete
int grid_complete(void){

	int column = 0; 
	int line = 0;

	for(column = 0; column < 7; column++){
		for(line = 0; line < 6; line++){
			if(grid[column][line] == ' ')
				return 0;
		}
	}
	return 1;
}
int demande_action(int *coup)
{
    /*
     * Demande l'action à effectuer au joueur courant.
     * S'il entre un chiffre, c'est qu'il souhaite jouer.
     * S'il entre la lettre « Q » ou « q », c'est qu'il souhaite quitter.
     * S'il entre autre chose, une nouvelle saisie sera demandée.
     */

    char c;
    int ret = 0;

    if (scanf("%d", coup) != 1)
    {
        if (scanf("%c", &c) != 1)
        {
            fprintf(stderr, "Erreur lors de la saisie\n");
            return ret;
        }

    /*    switch (c)
        {
        case 'Q':
        case 'q':
            ret = ACT_QUITTER;
            break;
        default:
            ret = ACT_NOUVELLE_SAISIE;
            break;
        }*/
    }
    else
        ret = 1;

    if (!vider_tampon(stdin))
    {
         fprintf(stderr, "Erreur lors de la vidange du tampon.\n");
         ret = 0;
    }

    return ret;
}
int vider_tampon(FILE *fp)
{
    /*
     * Vide les données en attente de lecture du flux spécifié.
     */

    int c;

    do
        c = fgetc(fp);
    while (c != '\n' && c != EOF);

    return ferror(fp) ? 0 : 1;
}
int main() {

	struct position pos;
	int i = 1;

	grid_initialisation();
	display_grid();

	while(1){
		int input;
        int coup;

        input = demande_action(&coup);
		grid_position(input, &pos);

		if(i%2 == 0){
			grid[pos.column][pos.line] = 'X';
		}
		else{
			grid[pos.column][pos.line] = 'O';
		}
			display_grid();
		i++;
	}
	return 0;
}

