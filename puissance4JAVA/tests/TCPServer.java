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

  The to launch the game enter the following command line

  java TCPServer

  Then to join the game the two players must enter the following
  command line

  telnet Server_IP Server_Port

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

class TCPServer 
{
  char [][] grid = new char[7][6]; 
  int turn =0;

  //Vector<String> users = new Vector<String>();
  //Vector<HandleClient> clients = new Vector<HandleClient>();
  private HandleClient handleClient[];
  int PORT = 9020;
  int NumClients = 2;

  public void process() throws Exception  
  {

      ServerSocket server = new ServerSocket(PORT,NumClients);
      System.out.println("Server Connected...");
      Socket client = server.accept();
    for (int i = 0; i < handleClient.length; i++){
      try{
        //Declaration of the 2 instances of the player class 
        //in the constructor we pass the object socket, the instance of the 
        //class TCPPlayer, the number of the instance
        //of Player and the grid. The arguments are passed at 
        //the time of the object creation of the class
        //so that the method with the same name as the class can take 
        //the arguments passed at the time of the object creation of the 
        handleClient[ i ] = new HandleClient( server.accept(), this, i, grid);
        //The start() method causes both instances of the threads to begin execution, 
        //the Java Virtual Machine calls the run method of the threads.
        //The result is that two threads are running concurrently
        handleClient[ i ].start();
      //IOException is the general class of exceptions produced
      //by failed or interupted I/O operations here the creation of 
      //the instance of the class Player and the start on those instances.
      }catch(IOException e){
        e.printStackTrace();
        //In case an Exception is caught the program exits
        System.exit( 1 );}
    }
  }

  public static void main(String ... args) throws Exception 
  {
     TCPServer game = new TCPServer();
     game.process();
  } // end of main
//String user, String message
  // public void broadcast(int turn)  
  // {
  //       //send message to all connected users
  //       for (HandleClient c : clients)
  //          if (!c.getUserName().equals(user))
  //          {
  //             c.sendMessage(user,message);
  //          }
  // }
  class HandleClient extends Thread 
  {
    private String column = "";
    private BufferedReader input;
    private PrintWriter output;
    private char mark;
    private Socket connection;
    private TCPServer game;
    private int number;
    protected boolean threadSuspended = true;
    protected boolean inputSuspended = true;
    private char [][] gridPlay;

      // get input and output streams
    public HandleClient(Socket s, TCPServer t, int num , char grid [][]) {
      
      mark = ( num == 0 ? 'X' : 'O' );
      connection = s;
       
      try{
        input = new BufferedReader(new InputStreamReader(connection.getInputStream())) ;
        output = new PrintWriter (connection.getOutputStream(),true);
      }catch(IOException e){e.printStackTrace();}

      game = t;
      number = num;
      gridPlay = grid;
      output.println("Welcome to the 4 connect Game!\n");
      grid_initialisation();
      display_grid();
      // start();
    }
    /*
    {
      System.out.println("client Id : "+test);
      // get input and output streams
      input = new BufferedReader(new InputStreamReader(client.getInputStream())) ;
      output = new PrintWriter (client.getOutputStream(),true);
      output.println("Welcome to the 4 connect Game!\n");
      grid_initialisation();
      display_grid();
      start();
    }*/
    public void display_grid()
    {
      int column = 0;
      int line = 0;
      //Use of the for loop to go through the grid and display the grid it self
      //and the caracters contained in the grid
      output.printf("+---+---+---+---+---+---+---+\n"); 
      for(line = 0; line < 6; line++){
        output.printf("|");
        for(column = 0; column < 7; column++){
          //Display the grid from the global grid
          output.printf(" "+grid[column][line]+" |");
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
    public void grid_initialisation()
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
    //Definition of the position on the grid
    //Returns nothing, the arguments are the column entered by the player and 
    //the pointeur on the object pos, the global structure used to fill the grid
    public int [] grid_position(int position){
      
      int ln;
      int column;
      int line = 0;
      //A for loop is used to go backward through the grid to do the 
      //transcription of the column entered into a [column] [line] position 
      //within the grid by verifying if the column is empty
        column = position -1;
        for(ln = 5; ln >=0; ln--)
        {
          if(grid[column][ln] == ' ')
          {
            line = ln;
            break;
          }
          else
          {
            line = 0;
          }
      }
    return new int[] {column, line};
    }
    public void run()  
    {
         String line;
         try    
         {
            while(true)   
            {
                long threadId = Thread.currentThread().getId();
                output.printf("Thread "+threadId+ " your turn to play : ");
                column  = input.readLine();
                output.println("turn : "+turn);
                //users.add(name); // add to vector
                int col = Integer.parseInt(column);      
                int pos[] = grid_position(col);
                output.println("test");
                for (int i = 0; i < game.players.length; i++)
                {
                  if (turn %2 == 0) 
                  {
                    grid[pos[0]][pos[1]] = "X";
                    c.display_grid();
                  }
                  else
                  {
                    grid[pos[0]][pos[1]] = "0";
                    c.display_grid();
                  }
                }
                turn ++;
            }// end of while
          } // try
         catch(Exception e) 
         {
           System.out.println("Exception:");
           System.out.println(e.getMessage());
         }
    } // end of run()
  } // end of inner class
} // end of Server