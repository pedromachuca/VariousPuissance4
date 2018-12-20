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

public class Connect{

    private BufferedReader input;
    private PrintWriter output;
    private char [][] grid = new char[7][6];
    static boolean isThread1 = false;
    //Declaration of variable server to store the object ServerSocket
    private ServerSocket server;
    public Connect(){
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

    public static synchronized boolean isThread1() {
        return isThread1 = !isThread1;
    }
    public void display_grid(){

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

  public void grid_initialisation(){
      
    int column = 0;
    int line = 0;
    //Initialisation of the grid with the space caractere
    for(column = 0; column < 7; column++){
      for(line = 0; line < 6; line++){
        grid[column][line] = ' ';
      }
    }
  }
  public int read_input(){
  
    int column = 10;
    String userInput ="";
    while(column<=0 || column>7){
      try{
        output.println("test1");
        userInput=input.readLine();
        try{
          column= Integer.parseInt(userInput);
        }catch(NumberFormatException e){e.printStackTrace();continue;}
      }catch(IOException e){e.printStackTrace();continue;}
    }
  return column; 
  }
  public void reader(){
       
    try{
      input = new BufferedReader(new InputStreamReader(thread1.getInputStream()));
      output = new PrintWriter (thread1.getOutputStream(),true);
      input1 = new BufferedReader(new InputStreamReader(thread2.getInputStream()));
      output1 = new PrintWriter (thread2.getOutputStream(),true);
    }catch(IOException e){
      e.printStackTrace();
      System.exit( 1 );}
  }
    public static void main(String args[]) {

        Runnable runnableObject = new Runnable() {
            @Override
            public void run() {
                grid_initialisation();
                display_grid();
                while(true){
                  synchronized (this) {
                      for (int i = 1; i <= 100; i++) {
                          try {
                              if (Thread.currentThread().getName().equals("thread1")) {
                                  if (isThread1()){
                                      System.out.println(Thread.currentThread().getName() + "    :   " + i);
                                  }else{
                                      this.notify();
                                      this.wait();
                                  }
                              } else {
                                  if (!isThread1()){
                                      System.out.println(Thread.currentThread().getName() + "    :   " + i);
                                      this.notify();
                                      this.wait();
                                  }
                                  else{
                                  }
                              }
                          } catch (Exception e) {
                          }
                      }
                  }
                }
            }
        };
        Thread thread1 = new Thread(runnableObject);
        thread1.accept();
        thread1.setName("thread1");
        thread1.start();
        Thread thread2 = new Thread(runnableObject);
        thread2.setName("thread2");
        thread1.accept();
        thread2.start();
        System.out.println(Thread.currentThread().getName() + "Main thread finished");
    }
}