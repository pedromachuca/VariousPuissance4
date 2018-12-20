/* Pierre Coiffey
   Connect 4 game v1
   07/09/2018
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

  javac TCPServer.java

  To launch the game enter the following command line

  java TCPServer

  Then to join the game the two players must enter the following
  command line the port is defined bellow we chose 9020 you can change it 
  to fit specific needs.

  telnet Server_IP Server_Port

  or

  nc Server_IP Server_Port

  Example : telnet 64.71.732.37 9020

  The game will start and you will be able to play.
  Follow the inscription in the program to leave the 
  game, save a game and play.
*/
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
/*Provides the classes for implementing networking applications.
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

//gridPlay[pos[0]][pos[1]] = "\u001B[34m"+'X'+"\u001B[0m";
/*
Class TCPServer that contains the main method. It is where the socket is declared, the number of accepted connections
is 2 the class player is called, the 2 threads are started.
*/
class TCPServer{
  //private hides from other classes within the package. 
  //declaration of the grid as a char table
  private char [][] grid = new char[7][6];
  //Declaration of a table to store the 2 instances of the Player
  //class in order to be able to  access the instances separately
  private Player players[];
  //Declaration of variable server to store the object ServerSocket
  private ServerSocket server;
  //Declaration of an integer variable 
  //It will help to track the player's turn
  private int currentPlayer;
  //Method TCP to bind the connection to the local adress
  //and port and define the number of accepted connection 
  //Has no arguments returns nothing
  //however the server object will be accessible by other 
  //method in this class
  //private int victory = 0;

  public TCPServer(){
    //Declaration of a table of 2 instances of the class Player
    players = new Player[ 2 ];
    currentPlayer = 0;
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
        //class TCPPlayer, the number of the instance
        //of Player and the grid. The arguments are passed at 
        //the time of the object creation of the class
        //so that the method with the same name as the class can take 
        //the arguments passed at the time of the object creation of the 
        players[ i ] = new Player( server.accept(), this, i, grid);
        //The start() method causes both instances of the threads to begin execution, 
        //the Java Virtual Machine calls the run method of the threads.
        //The result is that two threads are running concurrently
        players[ i ].start();
      //IOException is the general class of exceptions produced
      //by failed or interupted I/O operations here the creation of 
      //the instance of the class Player and the start on those instances.
      // if (victory==1) {
      //   players[ i ].start();
      // }
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
  // Determine if a move is valid.
  // This method is synchronized because only one move can be
  // made at a time.
  public synchronized boolean validMove( int pos[], int player, PrintWriter output){

      // boolean victory = false;
      // while(player != currentPlayer){
      //   try{
      //     System.out.println("wait");
      //     wait();
      //   }catch(InterruptedException e){
      //     e.printStackTrace();}
      // }

      if(!isOccupied(pos))
      {        
       // grid[pos[0]][pos[1]] =(currentPlayer == 0 ? 'X' : 'O' );
        currentPlayer = ( currentPlayer + 1 ) % 2;
        if (players[currentPlayer].otherPlayerMoved(pos,output)) 
        {
          System.out.println("test before nortify");
         // victory = true;
        }
        notify(); 
        return true; 
      }
      else{return false;} 
  }

  public boolean isOccupied(int pos[]){
    if (grid[pos[0]][pos[1]] == 'X' || grid[pos[0]][pos[1]] == 'O'){
        return true;
    }
    else{return false;}
  }
  // public int victory(int pos[]){
  //   if (gameOver(pos)){
  //     victory = 1;  
  //     System.out.println("testé");   
  //   }
  //   System.out.println(""+victory);
  //   return victory;
  // }
  public  boolean gameOver(int pos[]){
    System.out.println("test1");
    //declaration of the counter
    int nb = 1, nb1 = 1, nb2 = 1, nb3 =1, nb4 = 1, nb5 = 1, nb6 = 1, nb7 =1;
    for (int i = 1; i < 4; ++i){
      try{
        //Verification of the horizontal right direction 
        if (pos[0] +i <= 6){
          if (grid[pos[0]][pos[1]]== grid[pos[0] + i ][pos[1]]){nb++;}     
        }
      }catch(ArrayIndexOutOfBoundsException e){continue;}
      try{
        //Verification of the horizontal left direction
         if (pos[0] - i >=0){
          if (grid[pos[0]][pos[1]]== grid[pos[0] - i ][pos[1]]){nb1++;}      
        }
      }catch(ArrayIndexOutOfBoundsException e){continue;}
      try{
        //Verification of the vertical direction top
        if (pos[1] + i <=5){
          if (grid[pos[0]][pos[1]]== grid[pos[0]][pos[1] +i]){nb2++;}      
        }
      }catch(ArrayIndexOutOfBoundsException e){
        System.out.println(e);
        continue;}
      try{
        //Verification of the vertical direction bottom
        if (pos[1] - i >=0){
          if (grid[pos[0]][pos[1]]== grid[pos[0]][pos[1] - i]){nb3++;}
        } 
      }catch(ArrayIndexOutOfBoundsException e){continue;} 
      try{ 
        //Verification of the diagonal direction bottom left to top right (top)
        if (pos[0] + i <=6 && pos[1] + i <=5){
          if (grid[pos[0]][pos[1]]== grid[pos[0] + i ][pos[1] + i]){nb4++;}
        } 
      }catch(ArrayIndexOutOfBoundsException e){continue;} 
      try{
        //Verification of the diagonal direction bottom left to top right (bottom)
        if (pos[0] - i >=0 && pos[1] - i >=0){
          if (grid[pos[0]][pos[1]]== grid[pos[0] - i ][pos[1] - i]){nb5++;}     
        }
      }catch(ArrayIndexOutOfBoundsException e){continue;} 
      try{  
        //Verification of the diagonal direction bottom right to top left (bottom)
        if (pos[0] + i <=6 && pos[1] - i >=0){
          if (grid[pos[0]][pos[1]]== grid[pos[0] + i ][pos[1] - i]){nb6++;}     
        }
      }catch(ArrayIndexOutOfBoundsException e){continue;}
      try{  
        //Verification of the diagonal direction bottom right to top left (top)
        if (pos[0] - i >=0 && pos[1] + i <=5){
          if (grid[pos[0]][pos[1]]== grid[pos[0] - i ][pos[1] + i]){nb7++;}       
        } 
      }catch(ArrayIndexOutOfBoundsException e){continue;}
    } 
   System.out.println("nb : "+ nb +" nb1 : "+ nb1+" nb2 : "+ nb2+" nb3 : "+ nb3+" nb4 : "+ nb4+" nb5 : "+ nb5+" nb6 : "+ nb6+" nb7 : "+ nb7);
    //Verification of the counters 
    if (nb >3||nb1 >3||nb2 >3||nb3 >3||nb4 >3||nb5 >3||nb6 >3||nb7 >3){
      //asking the player if he wants to play another game
      //verification of the input as detailed previously
    System.out.println("test2");

      return true;
    }
    return false;
  }

  public static void main(String args[]){

    TCPServer game = new TCPServer();
    game.execute();
    // if (game.victory ==1) {
    //   game.execute();      
    // }
  }
}
// Player class to manage each Player as a thread
class Player extends Thread {

  private Socket connection;
  private BufferedReader input;
  private PrintWriter output;
  private BufferedReader input1;
  private PrintWriter output1;
  private TCPServer control;
  private int number;
  private char mark;
  protected boolean threadSuspended = true;
  private char [][] gridPlay;
  //protected int victory = 0;
 
 
  public Player(Socket s, TCPServer t, int num , char grid [][]){

    mark = ( num == 0 ? 'X' : 'O' );
    connection = s;
       
    try{
      if (mark =='X') {
        input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        output = new PrintWriter (connection.getOutputStream(),true);
      }
      else{
        input1 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        output1 = new PrintWriter (connection.getOutputStream(),true);
      }
      
    }catch(IOException e){
      e.printStackTrace();
      System.exit( 1 );}

    control = t;
    number = num;
    gridPlay = grid;
  }

  public void display_grid(PrintWriter output){

    int column = 0;
    int line = 0;
    //Use of the for loop to go through the grid and display the grid it self
    //and the caracters contained in the grid
    output.printf("+---+---+---+---+---+---+---+\n"); 
    for(line = 0; line < 6; line++){
      output.printf("|");
      for(column = 0; column < 7; column++){
        //Display the grid from the global grid
        output.printf(" "+gridPlay[column][line]+" |");
      }
      output.printf("\n+");
      for(column = 0; column < 7; column++){
        output.printf("---+");
      }
      output.printf("\n");
    }
    //Displays the number of column the player can play 
    output.printf("  1   2   3   4   5   6   7\n");
    // read name
  }

  public void grid_initialisation(){
      
    int column = 0;
    int line = 0;
    //Initialisation of the grid with the space caractere
    for(column = 0; column < 7; column++){
      for(line = 0; line < 6; line++){
        gridPlay[column][line] = ' ';
      }
    }
  }

  public int [] grid_position(int position){
    
    int ln;
    int column;
    int line = 0;
    //A for loop is used to go backward through the grid to do the 
    //transcription of the column entered into a [column] [line] position 
    //within the grid by verifying if the column is empty
    column = position -1;
    for(ln = 5; ln >= 0; ln--){
      if(gridPlay[column][ln] == ' '){
        line = ln;
        break;
      }
      else{line = 0;}
    }
    return new int[] {column, line};
  }

  public boolean otherPlayerMoved(int pos[], PrintWriter output){
    
    //display_grid(output);
    if (!control.gameOver(pos)) {
      //output.printf( "Enter a column between 1 and 7 : " );
      return false;
    }
    else{
      output.printf( "You lost the match!! " );
      return true;
    }
  }
 public int read_input(BufferedReader input){
  
    int column = 10;
    String userInput ="";
    while(column<=0 || column>7){
      try{
        userInput=input.readLine();
        try{
          column= Integer.parseInt(userInput);
        }catch(NumberFormatException e){e.printStackTrace();continue;}
      }catch(IOException e){e.printStackTrace();continue;}
    }
  return column; 
  }
  public void run(){
    String column = " ";
    int newGame = 1;
    int pos[] = {10,10};
    int res = 0;
      // output.printf( "Player " +( number == 0 ? 'X' : 'O' ) + " connected" );
      // output.printf( mark );
      //output.printf("You are connected\n");
      grid_initialisation();
      // wait for another player to arrive
      if(mark == 'X'){
        output.printf("Waiting for another player\n" );
        try{
          synchronized(this){   
            while(threadSuspended){
              wait();
            }  
          }
        }catch(InterruptedException e){
          e.printStackTrace();}

        display_grid(output);
        output.printf( "Enter a column between 1 and 7 : " );
      }
      else{
        display_grid(output1);
        output1.println( "Waiting for player X" );
      }
      // Play game
      while(true)
      { 
          if (mark == 'X') 
          {
            int col = read_input(input);
            pos= grid_position(col);
            // output.println("c :"+pos[0]+"l : "+pos[1]);
            // gridPlay[pos[0]][pos[1]] = ( number == 0 ? 'X' : 'O' );
            // res = control.victory(pos);
            //  gridPlay[pos[0]][pos[1]] = ' ';
            //  output.println(res);
            // // boolean res1 = control.validMove(pos,number);
            // // output.println(res1);
            if(control.validMove(pos,number, output)){
              output.println("test67860");
              gridPlay[pos[0]][pos[1]] = ( number == 0 ? 'X' : 'O' );
              if (control.gameOver(pos)) {
                display_grid(output1);
                output.println(( "You won the match" ));
                break;
              }
              else if (res==1){
                output.println("You lost the match");
                break;
              }
              display_grid(output);
              output.println( "Valid move ! Waiting for the other player to player" );
            }
            else{output.println( "Invalid move, try again : ");}
          }
          else
          {
            output.printf( "Enter a column between 1 and 7 : " );
            int col = read_input(input1);
            pos= grid_position(col);
            // output.println("c :"+pos[0]+"l : "+pos[1]);
            // gridPlay[pos[0]][pos[1]] = ( number == 0 ? 'X' : 'O' );
            // res = control.victory(pos);
            //  gridPlay[pos[0]][pos[1]] = ' ';
            //  output.println(res);
            // // boolean res1 = control.validMove(pos,number);
            // // output.println(res1);
            if(control.validMove(pos,number, output1)){
              output1.println("test67860");
              gridPlay[pos[0]][pos[1]] = ( number == 0 ? 'X' : 'O' );
              if (control.gameOver(pos)) {
                display_grid(output1);
                output1.println(( "You won the match" ));
                break;
              }
              else if (res==1){
                output1.println("You lost the match");
                break;
              }
              display_grid(output1);
              output1.println( "Valid move ! Waiting for the other player to player" );
            }
            else{output1.println( "Invalid move, try again : ");}  
          }       
      }
      output.println( "Would you like to play another game ? Enter (y/n) :" );
            // column = input.readLine();
      // newGame = Integer.parseInt(column);
      // if (newGame==0){
      //   grid_initialisation();
      //   display_grid();
      //   output.printf( "Enter a column between 1 and 7 : " );
      //   while(true){ 
      //     column = input.readLine();
      //     col = Integer.parseInt(column);
      //     pos= grid_position(col);
      //     gridPlay[pos[0]][pos[1]] = ( number == 0 ? 'X' : 'O' );
      //     res = control.victory(pos);
      //     gridPlay[pos[0]][pos[1]] = ' ';

      //     if(control.validMove(pos,number, res)||res == 1){
      //       gridPlay[pos[0]][pos[1]] = ( number == 0 ? 'X' : 'O' );
      //       if (control.gameOver(pos)) {
      //         display_grid();
      //         output.println(( "You won the match" ));
      //         break;
      //       }
      //       else if(res==1){
      //         break;
      //       }
      //       display_grid();
      //       output.println( "Valid move ! Waiting for the other player to player" );
      //     }
      //     else{output.println( "Invalid move, try again : ");}  
      //   } 
      // }
      // else{
      //   connection.close();
      // }    
  }
} 