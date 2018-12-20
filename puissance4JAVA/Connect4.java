/* Pierre Coiffey
   Connect 4 game v1
   27/09/2018
   You can play this game online.
   2 players

   Rules :
   To win Connect Four, all you have to do is connect four
   of your chip pieces in a row. This can be done 
   horizontally, vertically or diagonally. Each player 
   will drop in one chip piece at a time. This will give 
   you a chance to either build your row, or stop your 
   opponent from getting four in a row.

  The game was developped under the following version of java :
  
  openjdk version "1.8.0_181"
  OpenJDK Runtime Environment (build 1.8.0_181-8u181-b13-1~deb9u1-b13)
  OpenJDK 64-Bit Server VM (build 25.181-b13, mixed mode)

  How to launch :

  To play the game please compile this code 
  with the following command line.

  javac Connect4.java

  The to launch the game enter the following command line

  java Connect4

  Then to join the game the two players must enter the following
  command line

  telnet Server_IP Server_Port

  The game will start and you will be able to play.
  Follow the inscription in the program to leave the 
  game, save a game and play.
*/
//import java.io.* to import all of java.io 
//Provides for system input and output through data streams,
//serialization and the file system. Unless otherwise noted,
//passing a null argument to a constructor or method in any class 
//or interface in this package will cause a NullPointerException to be thrown. 
import java.io.*;
//Contains the collections framework, legacy collection classes,
//event model, date and time facilities, internationalization,
//and miscellaneous utility classes (a string tokenizer,
// a random-number generator, and a bit array). 
import java.util.*;
/*
Provides the classes for implementing networking applications.
The java.net package can be roughly divided in two sections:
  A Low Level API, which deals with the following abstractions:
    Addresses, which are networking identifiers, like IP addresses.
    Sockets, which are basic bidirectional data communication mechanisms.
    Interfaces, which describe network interfaces.
  A High Level API, which deals with the following abstractions:
    URIs, which represent Universal Resource Identifiers.
    URLs, which represent Universal Resource Locators.
    Connections, which represents connections to the resource pointed to by URLs.
*/
import java.net.*;
/*
Class TCPServer that contains the main method. It is where the socket is declared, the number of accepted connections
is 2 the class player is called, the 2 threads are started.
*/
class Connect4{
  //private hides from other classes within the package. 
  //declaration of the grid as a char table
  public char [][] grid = new char[7][6];
  //Declaration of a table to store the 2 instances of the Player
  //class in order to be able to  access the instances separately
  public Player players[];
  //Declaration of variable server to store the object ServerSocket
  private ServerSocket server;
  //Method Connect4 to bind the connection to the local adress
  //and port and define the number of accepted connection 
  //Has no arguments returns nothing
  //however the server object will be accessible by other 
  //method in this class
  public Connect4(){
    //Declaration of a table of 2 instances of the class Player
    players = new Player[ 2 ];
    // set up ServerSocket port 9020 local IP adress 
    //and 2 connections allowed. Declaration of the 
    //instance of ServerSocket inside a try catch.
    try{
      server = new ServerSocket( 9020, 2 );
    //IOException is the general class of exceptions produced
    //by failed or interupted I/O operations here the socket conection.
    }catch(IOException e){
      //method on Exception instances that prints the stack trace 
      //of the instance to System.err.
      e.printStackTrace();
      //In case an Exception is caught the program exits
      System.exit( 1 );}
  }
  // Method execute that wait for two connections in order 
  //to play the game. Once there is two connections, the 
  //method start is called on the two instance of the class Player
  public void execute(){
    //loops through the table players 2 times
    for (int i = 0; i < players.length; i++){
      try{
        //Declaration of the 2 instances of the player class 
        //in the constructor we pass the object socket, the instance of the 
        //class Connect4, the number of the instance
        //of Player and the grid. The arguments are passed at 
        //the time of the object creation of the class
        //so that the method with the same name as the class can take 
        //the arguments passed at the time of the object creation of the 
        players[ i ] = new Player( server.accept(), this, i, grid);
        //The start() method causes both instances of the threads to begin execution, 
        //the Java Virtual Machine calls the run method of the threads.
        //The result is that two threads are running concurrently
        players[ i ].start();
        //Set players 0 as the priority thread, this way the 
        //thread 0 will always excute first
        players[0].setPriority(Thread.MAX_PRIORITY);
      //IOException is the general class of exceptions produced
      //by failed or interupted I/O operations here the creation of 
      //the instance of the class Player and the start on those instances.
      }catch(IOException e){
        e.printStackTrace();
        //In case an Exception is caught the program exits
        System.exit( 1 );}
    }
    // Player X is suspended until Player O connects.
    // Resume player X now.          
    synchronized(players[0]){
      players[0].threadSuspended = false;   
      players[0].notify();
    }
  }
  /*
  Method that reads the char entered by the player.
  The argument is the Buffered Reader.
  It returns a char.
  The input is synchronized, in the run method there are wait()
  in order for only one thread to be asked for input at a time
  when one of the threads enter this method the other is waiting.
  When the thread reach the corresponding synchronized(), it will
  notify the other thread to wake up.
  */
  public char read_input0(BufferedReader input){
    //The scanner class is used here to avoid issues
    //of expected int but receiving a char with the readLine
    //method used to get the column from the playe  r
    //The Delimiter used is enter
    Scanner sc = new Scanner(input).useDelimiter("\n");
    //initialisation of a space char
    char ch =' ';
    //Loops until a char is received and
    //is y or n
    while (ch!='y'&&ch!='n'){
        ch = sc.next().charAt(0);     
    }
    //Methods synchronized explained earlier to 
    //wake up the waiting thread
    synchronized(players[0]){players[0].notify();}
    synchronized(players[1]){players[1].notify();    }
    //returns the char found
    return ch;
  }
  /*
  Method that reads the column (int) entered by the player.
  The argument is the Buffered Reader.
  It returns an int.
  The input is synchronized, in the run method there are wait()
  in order for only one thread to be asked for input at a time
  when one of the thread enter this method the other is waiting.
  When the thread reach the corresponding synchronized(), it will
  notify the other thread to wake up.
  */
  public int read_input1(BufferedReader input){
    //In case nothing is read the method will
    //return 10
    int column = 10;
    //The method readLine() returns a String so
    //we initialise one.
    String userInput ="";
    //Loops until the player enters a column between
    //1 and 7 or enters 0 to leaves the game.
    //The loop is broken if something else than an int 
    //is enter so the method will return 10.
    //In the run method this method is called in a loop
    //that will call this method until it returns 
    //a column between 1 and 7 or 0 to leave
    while(column<=0 || column>7){
      try{
            //the method readLine() is called on the buffered reader
            //and stored in the previously declared String
            //It is called inside a try catch to break if an IOException occur
            userInput=input.readLine();
        try{
          //Parse the received String into an int 
          //In case something else is entered the 
          //loop breaks;
          column= Integer.parseInt(userInput);
        }catch(NumberFormatException e){e.printStackTrace();break;}
      }catch(IOException e){e.printStackTrace();break;}
    }
    //Methods synchronized explained earlier to 
    //wake up the waiting thread
    if (column != 10) {
      synchronized(players[0]){players[0].notify();}
      synchronized(players[1]){players[1].notify();}
    }
    //returns the int found
    return column;          
  }
  //Method that display the grid, it is synchronized in order
  //to avoid both method accessing it at the same time 
  //The method takes the Printwriter has an argument to 
  //display the grid in the player's terminal
  //This method will display in both players terminal the 
  //updated grid. Since only one thread cann access this 
  //method at a time one player will display is move in 
  //both player's terminal and vice versa.
  public synchronized void display_grid(PrintWriter output){
    int column = 0;
    int line = 0;
    //Use of the for loop to go through the grid and display the grid it self
    //and the caracters contained in the grid
    players[0].output.printf("+---+---+---+---+---+---+---+\n"); 
    players[1].output.printf("+---+---+---+---+---+---+---+\n"); 
    for(line = 0; line < 6; line++){
      players[0].output.printf("|");
      players[1].output.printf("|");
      for(column = 0; column < 7; column++){
        //Display the grid from the global grid
        players[0].output.printf(" "+grid[column][line]+" |");
        players[1].output.printf(" "+grid[column][line]+" |");
      }
      players[0].output.printf("\n+");
      players[1].output.printf("\n+");
      for(column = 0; column < 7; column++){
        players[0].output.printf("---+");
        players[1].output.printf("---+");
      }
      players[0].output.printf("\n");
      players[1].output.printf("\n");
    }
    //Displays the number of column the player can play 
    players[0].output.printf("  1   2   3   4   5   6   7\n");
    players[1].output.printf("  1   2   3   4   5   6   7\n");
  }
  // Method that takes the column entered by the player 
  //and transforms it into an int [] with the column and line
  public int [] grid_position(int position){
    //definition of ints for the for loop
    int ln;
    int column;
    int line = 0;
    //A for loop is used to go backward through the grid to do the 
    //transcription of the column entered into a [column] [line] position 
    //within the grid by verifying if the column is empty
    column = position -1;
    for(ln = 5; ln >= 0; ln--){
      if(grid[column][ln] == ' '){
        line = ln;
        break;
      }
    }
    return new int[] {column, line};
  }
  //Method that check for one winner.
  //Unlike the other program where the 
  //victory depended on the last move here the method
  //will check all of the grid every time and looks for 
  //any players victory 
  //Has no arguments
  //Returns the char corresponding to the players' victory
  //or nothing if there is no victory
  public char gameOver(){
    //definition of the variable defining the lenght of the grid
    //by asking the length method on the grid
    final int HEIGHT = grid.length;
    final int WIDTH = grid[0].length;
    //Initialisation of an empty char
    final char EMPTY_SLOT = ' ';
    // iterate rows, bottom to top
    for (int r = 0; r < HEIGHT; r++) { 
        // iterate columns, left to right
        for (int c = 0; c < WIDTH; c++){ 
            //which player's char is it on the current position
            char player = grid[r][c];
            // don't check empty slots
            if (player == EMPTY_SLOT){continue;} 
            // look right; if four same char as the player is found the method return this player
            //to avoid out of bound exception the current tested position + 3 must be inferior to the size 
            //of the grid
            if (c + 3 < WIDTH &&player == grid[r][c+1] && player == grid[r][c+2] && player == grid[r][c+3]){
              return player;  
            }
            if (r + 3 < HEIGHT) {
                // look up
                if (player == grid[r+1][c] && player == grid[r+2][c] && player == grid[r+3][c]){
                  return player;
                }  
                // look up & right            
                if (c + 3 < WIDTH && player == grid[r+1][c+1] && player == grid[r+2][c+2] && player == grid[r+3][c+3]){
                  return player;
                }
                // look up & left
                if (c - 3 >= 0 && player == grid[r+1][c-1] && player == grid[r+2][c-2] && player == grid[r+3][c-3]){
                  return player;
                }
            }
        }
    }
    // no winner found
    return EMPTY_SLOT; 
  }
  /*
  Method that depending on the state of the game 
  will print in the corresponding player's terminal
  the next action the player can or has to take.
  It takes the current mark of the player's thread and
  and the Printwriter has arguments.
  If a winner is found, the method returns 1.
  If no winner is found the method returns 0.
  Reminder thanks to the thread Priority the thread players[O]
  will always be X
  */
  public int action(PrintWriter output, char mark){
    //Case player X access the method and the gameOver method returns no victory
    if (mark == 'X' && gameOver()==' '){
      //This method will always be called after a display grid
      //thus after a player has made is move. So every time
      //this method is access it means this thread will have to wait
      //and the other thread will have to make its move
      players[0].output.println( "Waiting for the other player" );
      players[1].output.println( "Enter a column between 1 and 7 -  Enter 0 then press ENTER two times to leave : " );
    }
    //Case player O access the method  and the gameOver method returns no victory
    else if(mark == 'O' && gameOver()==' '){
      players[0].output.println( "Enter a column between 1 and 7 -  Enter 0 then press ENTER two times to leave : " );
      players[1].output.println( "Waiting for the other player" );
    }
    //Case gameOver method returns player X  as a winner
    else if (gameOver()=='X'){
      players[0].output.println( "Player X you win the match !!" );
      players[1].output.println( "Player X won the match. Press Enter to continue" );
      //Case there is a victory the method return 1.
      return 1;
    }
    //Case gameOver method returns player O  as a winner
    else if (gameOver()=='O'){
      players[0].output.println( "Player O you win the match !!" );
      players[1].output.println( "Player O won the match. Press Enter to continue" );
      //Case there is a victory the method return 1.
      return 1;
    }
    //Case no victory is found returns 0
    return 0;
  }
  //Main method of the class Connect4
  //It will be access when the server side
  //will enter the command java Connect4 
  //The main method creates a new instannce of the class 
  //Connect 4, then call the method execute where the socket,
  //is bound to its address and port, start the threads.
  //And finally wait until two threads are created from
  //the two connections
  public static void main(String args[]){
    Connect4 game = new Connect4();
    game.execute();
  }
}
/*
Player class 
The choosen mechanism is to extend the Player class
to threads so each thread will run an instance of this class.
Indeed with the extends to Thread, the run method can be used.
First the variable of the class will be defined. Some of them
are public to be access by the method from the Connect4 class
and other are private in order to be only accessed through this class. 
Then as seen previously, a method with the same name as the class is used to 
initialize variables of each players. Indeed, this class player
will be called by the execute method of the class Connect4 with the 
arguments Socket, instance of Connect, the number of the thread and the grid.
Next, methods of the class player are defined to be used in the run method:
public void grid_initialisation()
public boolean isOccupied(int pos[])
public void retrieve_game()
public void retrieve_turn()
Finally, the run() method is called this is where most of the game happens,
where each thread can access its part and wait for the other thread.
A principal while loop will run during most of the game and once the loop
if finished the server side program will stop
*/
class Player extends Thread {
  //Definition of an object Socket
  private Socket connection;
  //Definition of the object buffered reader for reading ints
  public BufferedReader input;
  //Definition of the object buffered reader to read chars
  public BufferedReader input1;
  //Definition of a single object print writter to write 
  //to the user's terminal
  public PrintWriter output;
  //Definition of a single object output stream, to write files
  public ObjectOutputStream outputStream;
  //Definition of an object input stream 
  //to read the file with the grid stored
  public ObjectInputStream inputStream;
  //Definition of an object input stream 
  //to read the file with the turn stored
  public ObjectInputStream inputStream1;
  //Definition of an object game of the class Connect4
  private Connect4 game;
  //This int will be use to store the current thread number
  private int number;
  //This char will be use to store the current player's mark X or O
  private char mark;
  //Definition of boolean variable(true or false)
  //this variable will be used by the synchronized method in the 
  //execute method of the class Connect4 to wait for the other 
  //player to connect
  protected boolean threadSuspended = true;
  //Definition of a char double array
  //to store the current grid 
  private char [][] gridPlay;
  //This int will be use to retrieve the 
  //player's turn from the file
  public int turn = 0;

  //Definition and initialisation of two object 
  //FileOutputStream to read each files
  FileOutputStream fos = null;
  FileOutputStream fos1 = null;
  //Definition and initialisation of 
  //two instance of files one for the grid (data)
  //and the other for the stored player's turn (turn)
  File data = new File("data.txt");
  File saved = new File("turn.txt");
 
  //This is the method with the same name as the class.
  //In the execute method of the class player the constructor of Player is called with the arguments 
  //object socket, the instance of the 
  //class Connect4, the number of the instance
  //of Player and the grid. 
  public Player(Socket s, Connect4 t, int num , char grid [][]){
    //With the Thread priority it is sure that the first thread
    //will be the number 0 and we want to make sure that this
    //thread will alway be the mark X. In case num = 0, mark = X
    //In case num = 1 mark = O
    mark = ( num == 0 ? 'X' : 'O' );
    //Assignement of the passed argument to the previously
    //initialized variables of the class Player
    //The Object Socket
    connection = s;
    try{
      //The method getInputStream() is called on the object Socket
      //as the argument of the new InputStreamReader itself as the 
      //argument of the new BufferReader stored in the previously 
      //initialized objects BufferedReader. Idem for the PrintWritter
      //these will be use to read data from user terminal and write
      //data for user terminal. One of the input will be use for 
      //reading char the other for int.
      input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      output = new PrintWriter (connection.getOutputStream(),true);
      input1 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    //These new instances are created inside a try catch IOException. If an exception
    //occurs at this time Then the program will exit and the player will have to 
    //start it again server side.
    }catch(IOException e){e.printStackTrace();System.exit( 1 );}
    //Assignement of the other passed arguments to the previously
    //initialized variables of the class Player.
    //Instance of the Connect4 class
    game = t;
    //Current number of the thread
    number = num;
    //State of the grid
    gridPlay = grid;
  }
  //Method to initialise the grid with spaces
  //Has no arguments
  //returns nothing
  public void grid_initialisation(){
    //Variable use to go 
    //through the grid  
    //used in the for loop
    int column = 0;
    int line = 0;
    //Initialisation of the grids with the space char
    //A double loop is used to initialize every
    //position on the grid
    for(column = 0; column < 7; column++){
      for(line = 0; line < 6; line++){
        //Initialisation of the grid inside the Player Class
        gridPlay[column][line] = ' ';
        //Initialisation of the grid inside the Connect4 Class
        game.grid[column][line] = ' ';
      }
    }
  }
  //Method use to check wether or not a position is valid
  //Its argument is the position returned by the method grid_position()
  //which gives an int[column][line] from the column entered by
  //the player
  //This method is boolean if the condition is fullfilled
  public boolean isOccupied(int pos[]){
    //If the current position does not contain either a X or a O
    //then the method returns true, otherwise it means that the position
    //is occupied so it returns false
    if (gridPlay[pos[0]][pos[1]] == 'X' || gridPlay[pos[0]][pos[1]] == 'O'){
      return true;
    }
    else{return false;}
  } 
  //Method to retrieve a saved game.
  //It has no arguments
  //It returns nothing
  //When the method is called, if the files data.txt and turn.txt exists
  //then the files are read and the corresponding variables are assigned
  //from the values of the files. This method will only be called by the first 
  //thread i.e. the player 0
  public void retrieve_game(){
    if (data.exists()&&saved.exists()){
      //Ask the user if he wants to continue a saved game
      output.println("A saved game exist. Would you like to continue the game ? Enter (y or n)");
      //The read input method is called it can only return a y or a n 
      char retrieve = game.read_input0(input1);
      //if the char returned is y the files are read 
      //Otherwise the method ends
      if (retrieve =='y') {
        //The files are read within a try catch
        //statement if any of those three exceptions
        //occurs then the program will continue 
        //without reading the files. Normally
        //the FileNotFoundException should not be
        //caught since the condition is for the file to exist.
        try{
          try{
            try{
              //Assignement of the new ObjectInputStream with the
              //FileInput stream on the corresponding file as an argument
              inputStream = new ObjectInputStream(new FileInputStream(data));
              inputStream1 = new ObjectInputStream(new FileInputStream(saved));
              //Then the object inputStream corresponding to the file data
              //is read then parse to the format of the grid char [][]
              game.grid = (char [][])inputStream.readObject();
              //Idem with the file turn parsed as an int
              int player = (int)inputStream1.readObject();
              //player is then assigned to turn (class variable)
              turn = player;
            }catch(FileNotFoundException e){e.printStackTrace();}
          }catch(IOException e){e.printStackTrace();}
        }catch(ClassNotFoundException e){e.printStackTrace();}
      }
    }
  }
  //Same method as before except it just read the turn.txt file
  //This method will only be read by the thread players 1
  public void retrieve_turn(){
    if (saved.exists()){
      try{
        try{
          try{
            inputStream1 = new ObjectInputStream(new FileInputStream(saved));
            int player = (int)inputStream1.readObject();
            turn = player;
          }catch(FileNotFoundException e){e.printStackTrace();}
        }catch(IOException e){e.printStackTrace();}
      }catch(ClassNotFoundException e){e.printStackTrace();}
    }
  }
  //Method to save the game
  //It has no argument 
  //It returns nothing
  //This method will be called when a players
  //wish to exit the game, he will be prompted if 
  //he wants to save the game before leaving
  public void save_game_turn(){
    try{
      try{
        //Verification of the existence of the files
        //If they doesn't exists, then this method create them
        //If the files exists the files will be overwritten
        if (!data.exists()){data.createNewFile();}
        if (!saved.exists()){saved.createNewFile();}
        //Assignment of the variable created
        //within the Player class one for the 
        //file data.txt, and the file turn.txt
        fos = new FileOutputStream(data, false);
        fos1 = new FileOutputStream(saved, false);
      }catch(FileNotFoundException e){e.printStackTrace();}
      //The ObjectOutputStream allows to write object
      //directly into a file output stream so with the help of the 
      //writeObject() method we write the object number which represent
      //the player's turn and the object grid which is the current 
      //state of the grid. 
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
      oos.writeObject(game.grid);
      oos1.writeObject(number);
    }catch(IOException e){e.printStackTrace();}
  }
//Function that check if the grid is complete
//Returns an int to know if the grid is complete or not
//Has no arguments
public boolean grid_complete(char grid [][]){
  int column = 0; 
  int line = 0;
  //Double for loop to go through the grid to check whether it finds
  //a space inside the global grid
  for(column = 0; column < 7; column++){
    for(line = 0; line < 6; line++){
      if(grid[column][line] == ' '){
        return false;
      }
    }
  }
  //Return 1 if no empty space are found
  //It allows us to know that the grid is full
  return true;
}
  //Method run that come from the extends Thread of the class
  //Player where most of the game happens.
  //There is at the begining initialisations of the necessary
  //variables. Then the method will depending of the thread
  //perform certain action.
  public void run(){
    //Use to determine path depending 
    //on the return of the function read input
    //that returns the input entered by the player
    int col = 0;
    //Initialisation of the column line in order to have 
    //a default case for the conditions
    int pos [] = {10,10};
    //The winner variable will be use
    //to set the winner being when the thread enters
    //the winning condition
    int winner = 0;
    //The test_victory will be use to assign the return
    //of the action() method victory or not
    int test_victory = 0;
    //Since we are in the run method(), it means the player is connected
    output.println("You are connected\n");
    //both thread initialisze its grid.
    grid_initialisation();
    //with this condition, the first thread action will be treated in the if
    //since the first player connected will always be thread 0 and the 
    //second threads action will be treated in the else corresponding statement
    //I choose for simplification purpose to treat threads separetly thank to condition
    //on the current mark's thread. This if else condition is use to start the game,
    //the threads will only execute it once.
    if(mark == 'X'){
      try{
        //Wait for another player to arrive
        //This synchronized statement is used to 
        //wait for another players's thread to be started 
        //in the execute() method of the Connect4 class
        synchronized(this){   
          //Loop until the other thread start 
          //and threadSuspended is changed to false
          while(threadSuspended){
            output.println("Waiting for another player\n" );
            //Thread goes in blocked mode until the
            //other thread goes through the notify() method
            wait();
            //This method is called to ask the first player connected
            //if he wants to continue a saved game, if no saved game
            //exists nothing happens
            retrieve_game();
            //If turn is 0 no saved game exists or the player
            //doesn't want to continue the saved game
            if (turn == 0){
              //In any of the two previously mentionned conditions
              //The first thread will perform the following action
              //Diplay the grid to both terminal
              //Ask for the player input 
              //Notice the other player to wait
              game.display_grid(output);
              output.println("Enter a column between 1 and 7 - Enter 0 then press ENTER two times to leave : ");
              game.players[1].output.println("Waiting for the other player :");
            }
            //The other possible condition is that a save game exists
            //and the player would like to continue but it  was the other player's
            //turn 
            else if(turn == 1){
              //In this case the statement synchronized is used
              //to make the current thread (this) wait
              //The thread will wake up once the other thread goes through the
              //notify method. 
              synchronized(this){wait();}
            }
          }  
        }
      //This try catch is necessary for the use of the wait() method
      }catch(InterruptedException e){e.printStackTrace();}
    }
    //The second thread enters this condition.
    else{
      try{
        //I chose for simplification purpose that only 
        //the first thread can retrieve a saved game.
        //so the thread will be blocked by the statement
        //synchronized on the current thread and the method wait
        //This thread will be blocked until the first thread enters
        //read_input0 and notify() this thread
        synchronized(this){wait();}
      }catch(InterruptedException e){e.printStackTrace();}
      //Then the turn has to be known same as before, if turn is 0
      //it will be the other player turn so this thread has to wait.
      retrieve_turn();
      //Sleep to make sure the other thread reached the wait();
      try{
        game.players[1].sleep(500);
      }catch(InterruptedException e){e.printStackTrace();}
      //There is a case where the retrieve_turn() method returns turn = 1
      //but player 1 chose not to coninue a saved game so the state 
      //of the first thread is use in the conditions
      Thread.State state = game.players[0].getState();
      //If the turn = 0 its the player X turn 
      //so this thread has to wait
      if (turn == 0&&state == Thread.State.RUNNABLE) {
        try{
          synchronized(this){wait();}
        }catch(InterruptedException e){e.printStackTrace();}
      }
      //Case where turn = 0 and the other thread is waiting
      else if(turn==0&&state == Thread.State.WAITING){
        game.display_grid(output);
        output.println("Enter a column between 1 and 7 - Enter 0 then press ENTER two times to leave :");
        game.players[0].output.println("Waiting for the other player :"); 
      }
      //This is the case where the first thread choose not
      //to continue the saved game
      else if(turn==1&&state == Thread.State.RUNNABLE){
        try{
          synchronized(this){wait();}
        }catch(InterruptedException e){e.printStackTrace();}
      }
      //Otherwise it means turn = 1 and it is this player's turn
      else if(turn==1&&state == Thread.State.WAITING){
        game.display_grid(output);
        output.println("Enter a column between 1 and 7 - Enter 0 then press ENTER two times to leave :");
        game.players[0].output.println("Waiting for the other player :"); 
      }
    }
    //Play game
    //In this while loop most of the game is executed.
    //The program never get out of the loop until a
    //System.exit( 1 ) is reached
    while(true){ 
      //Depending on the outcome of the previous conditions,
      //one of the thread will be blocked until the other
      //access this method and the notify within
      //Ask for user input, if a wrong char or
      //something unexpected happens and no conditions
      //are met afterward then the loop will restart
      //and this method will be called again
      col = game.read_input1(input);
      //Thread 0 will be executed here
      if (mark=='X') {
        try{
          //The synchronized statement is use 
          //to place this thread waiting in case of
          //victory of the other thread.
          synchronized(this){
            //10 is the default value in read_input1
            //if col = 10 then no conditions are met
            //the other thread is not notified and
            //this thread returns to the beginig of the 
            //loop asking for a user input
            //0 is the option to leave the game
            if (col != 10 && col !=0){  
              //Transform the column into a column line position 
              pos= game.grid_position(col);
              //make sure the position is not occupied
              if(!isOccupied(pos)){
                //assign the position to the corresponding mark
                game.grid[pos[0]][pos[1]] = ( number == 0 ? 'X' : 'O' );
              }
              else{
                output.println( "This column was full too bad you lose your turn" );
              }
              //display the grid to both players
              game.display_grid(output);
              //For simplicity purpose when the grid is full the program notify the users and stop
              //This is a point of improvement detailed in the comments at the end of the program
              if (grid_complete(game.grid)) {
                output.println( "EQUALITY !! The grid is complete.\nTo start over please restart the server program" );
                game.players[1].output.println( "EQUALITY !! The grid is complete.\nTo start over please restart the server program" );
                System.exit( 1 );
              }
              //In case the other thread is already victorious
              test_victory = game.action(output, mark);
              if (test_victory == 0){this.wait();}
            }
            //when the player decide to leave
            else if (col == 0){
              output.println( "Would you like to save the game ? Enter (y or n) :" );
              char save = game.read_input0(input1);
              //If he wants to save the game the method save_game() is called
              //and the program exits
              if (save =='y') {
                save_game_turn();
                game.players[1].output.println( "Player X saved the game and left ! Bye !!" );
                System.exit( 1 );
              }
              //when the player doesn't want to save the program exits
              else{
                game.players[1].output.println( "Player X left without saving ! Bye !!" );
                System.exit( 1 );
              }
            }
          }
        }catch(InterruptedException e){e.printStackTrace();}
      }
      //Thread 1 will be executed here
      //It is the same code as the thread 1 executed
      //except for the informations prompted to the 
      //users are the other way around
      else{
        try{
          synchronized(this){
            if (col != 10 && col !=0){   
              pos= game.grid_position(col);
              if(!isOccupied(pos)){
                game.grid[pos[0]][pos[1]] = ( number == 0 ? 'X' : 'O' );
              }
              else{
                output.println( "This column was full too bad you lose your turn" );
              }
              game.display_grid(output);
              if (grid_complete(game.grid)) {
                output.println( "EQUALITY !! The grid is complete.\nTo start over please restart the server program" );
                game.players[0].output.println( "EQUALITY !! The grid is complete.\nTo start over please restart the server program" );
                System.exit( 1 );
              }
              test_victory = game.action(output, mark);
              if (test_victory == 0){this.wait();}
            }
            else if (col == 0){
              output.println( "Would you like to save the game ? Enter (y or n) :" );
              char save = game.read_input0(input1);
              if (save =='y') {
                save_game_turn();
                game.players[0].output.println( "Player X saved the game and left ! Bye !!" );
                System.exit( 1 );
              }
              else{
                game.players[0].output.println( "Player X left without saving ! Bye !!" );
                System.exit( 1 );
              }
            }
          }
        }catch(InterruptedException e){e.printStackTrace();}
      }  
      //In case of one of the player victory
      if (game.gameOver()=='X'||game.gameOver()=='O'){
        //We set a flag to the player who won
        if (game.gameOver()=='X') {
          winner = 0;
        }else{
          winner = 1;
        }
        try{
          game.players[0].sleep(100);
          game.players[1].sleep(100);
        }catch(InterruptedException e){e.printStackTrace();}
        //the player is then asked if he wants to play a new game
        char retry = 'y';
        //only the winner will be asked if he wants to retry 
        //for simplicity purpose
        if (winner == 0) {
          if (mark=='X') {
            output.println( "Would you like to play another game? Enter (y or n) :" );
            retry = game.read_input0(input1); 
          }
        }
        else{
          if (mark=='O') {
            output.println( "Would you like to play another game? Enter (y or n) :" );
            retry = game.read_input0(input1); 
          }
        }
        //Here the execution of the threads are 
        //once again separated through conditions 
        if (mark =='X') {
          try{
            //the statement synchronized is used to wait 
            //for the winner to decide he wants to play a 
            //new game or not. Only th winner can decide
            //for simplicity purpose
            synchronized(this){   
              if (retry == 'y'){
                //the winner is the other player so this one goes directly
                //to the begining of the loop to the rea_input0() method
                if (winner==1){
                  output.println( "Waiting for the other player if he choose to retry ..." );          
                } 
                //The player that won restart the game
                //reinitialisation of the grid, display
                //of the new grid and wait for the other 
                //player to enter a column 
                else{
                  grid_initialisation();
                  game.display_grid(output);
                  game.action(output, mark);
                  this.wait();
                }           
              }
              //Case when the player wants to leave the game
              else{
                game.players[1].output.println( "Player X chose to leave bye !!" );
                System.exit( 1 );
              }   
            }
          }catch(InterruptedException e){
            e.printStackTrace();}
        }
        //This is the same code as the if but inverted
        else{
          try{
            synchronized(this){
              if (retry == 'y') {
                if (winner==1) {
                  grid_initialisation();
                  game.display_grid(output);
                  game.action(output, mark);
                  this.wait();          
                } 
                else{
                  output.println( "Waiting for the other player if he choose to retry ..." );          
                }
              }  
              else{
                game.players[0].output.println( "Player O chose to leave bye !!" );
                System.exit( 1 );
              }
            }
          }catch(InterruptedException e){e.printStackTrace();}
        }
      } 
    }//End of the wile loop
  }//End of the run method
}//End of the player Class

/*
To go further & issues:

1. First thing will be to make sure that the player can enter another column
when the column entered was full.
2. Ask for the user to retry when the grid is full.
3. Start over but find a better way to synchronize from the begining the user input
Both point 1 and 2 were not realized because of the time consuming difficulty to 
synchronize user input. 
4. The way I handled the user input is far from ideal, resulting in difficulty
to synchronize them and switch turn. I am not sure what would be a better way to
do so. 

One idea :

Sync the thread not by user input but by action, i.e. we make the 
second thread wait. Meanwhile we execute in the run method the necessary
actions and at the end make it wait and notify the other that it can start.
*/
