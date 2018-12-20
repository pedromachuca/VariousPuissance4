import java.io.*;
import java.net.*;

class C4GameSession{

private Socket player1;
private Socket player2;


// Create and initialize cells
private char[][] cell =  new char[7][6];

private DataInputStream fromPlayer1;
private DataOutputStream toPlayer1;
private DataInputStream fromPlayer2;
private DataOutputStream toPlayer2;
private int CONTINUE;
private int PLAYER1_WON;
private int PLAYER2_WON;
private int DRAW;
// Continue to play
private boolean continueToPlay = true;

/** Construct a thread */

public C4GameSession(Socket player1, Socket player2) {

this.player1 = player1;
this.player2 = player2;

// Initialize cells with a blank character

for (int i = 0; i < 7; i++)
  for (int j = 0; j < 6; j++)
    cell[i][j] = ' ';
}

public void runGame() {

try {

  // Create data input and output streams

  DataInputStream fromPlayer1 = new DataInputStream(player1.getInputStream());
  DataOutputStream toPlayer1 = new DataOutputStream(player1.getOutputStream());
  DataInputStream fromPlayer2 = new DataInputStream(player2.getInputStream());
  DataOutputStream toPlayer2 = new DataOutputStream(player2.getOutputStream());

  // Write anything to notify player 1 to start
  // This is just to let player 1 know to start

  // in other words, don't let the client start until the server is ready

  toPlayer1.writeInt(CONTINUE);

  // Continuously serve the players and determine and report
  // the game status to the players

  while (true) {

    // Receive a move from player 1

    int row = fromPlayer1.readInt();
    int column = fromPlayer1.readInt();

    cell[row][column] = 'X';

    // Check if Player 1 wins

    if (isWon('X')) {
      toPlayer1.writeInt(PLAYER1_WON);
      toPlayer2.writeInt(PLAYER1_WON);
      sendMove(toPlayer2, row, column);
      break; // Break the loop
    }
    else if (isFull()) { // Check if all cells are filled
      toPlayer1.writeInt(DRAW);
      toPlayer2.writeInt(DRAW);
      sendMove(toPlayer2, row, column);
      break;
    }
    else {

      // Notify player 2 to take the turn - as this message is not '1' then
      // this will swicth to the relevant player at the client side

      toPlayer2.writeInt(CONTINUE);

      // Send player 1's selected row and column to player 2
      sendMove(toPlayer2, row, column);
   }

    // Receive a move from Player 2
    row = fromPlayer2.readInt();
    column = fromPlayer2.readInt();

    cell[row][column] = 'O';

    // Check if Player 2 wins
    if (isWon('O')) {
      toPlayer1.writeInt(PLAYER2_WON);
      toPlayer2.writeInt(PLAYER2_WON);
      sendMove(toPlayer1, row, column);
      break;
    }
    else {
      // Notify player 1 to take the turn
      toPlayer1.writeInt(CONTINUE);

      // Send player 2's selected row and column to player 1
      sendMove(toPlayer1, row, column);
    }
    }
  }
  catch(IOException ex) {
  System.err.println(ex);
  }
 }

 /** Send the move to other player */
private void sendMove(DataOutputStream out, int row, int column) throws IOException {

out.writeInt(row); // Send row index
out.writeInt(column); // Send column index
}

/** Determine if the cells are all occupied */

private boolean isFull() {

for (int i = 0; i < 7; i++)
  for (int j = 0; j < 6; j++)
    if (cell[i][j] == ' ')
      return false; // At least one cell is not filled

// All cells are filled
return true;
}

/** Determine if the player with the specified token wins */

private boolean isWon(char token) {

/*
int count = 0;
for (int i = 0; i < 6; ++i) 
for (int j = 0; j < 7; ++j) 
  if (cell[i][j] == token) 
     ++count;
    if (count == 4) 
        return true;  // found
  /* else 
     count = 0; // reset and count again if not consecutive
  */

int count_piece = 0;

    //Checking Horizontal Win
    for (int i = 0; i < 6; i++) {
        count_piece = 0;
        for (int j = 0; j < 7; j++) {

            if (cell[i][j] == 'X') {
                count_piece++;
                if (count_piece == 4) {
                    System.out.println("you win");
                    return true;
                }

            } else {
                count_piece = 0;
            }
          }
        }

     return false;  // no 4-in-a-line found

    }
 }

class C4Game{
    private static ServerSocket server1;
    private static ServerSocket server2;
    public static Socket player1;
    public static Socket player2;
    public C4Game(){
      try{
        server1 = new ServerSocket( 9020, 1);
        server2 = new ServerSocket( 9020, 1);
        player1 = server1.accept();
        player2 = server2.accept();
      //IOException is the general class of exceptions produced
      //by failed or interupted I/O operations here the socket conection.
      }catch(IOException e){
        //method on Exception instances that prints the stack trace 
        //of the instance to System.err.
        e.printStackTrace();
        //In case an Exception is caught the program exits
        System.exit( 1 );}
    }
  public static void main(String args[]){

    C4GameSession game = new C4GameSession(player1, player2);
  }
}