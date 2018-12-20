import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
 
public class TicTacToeServer extends JFrame {
 
   private byte board[];
   private boolean xMove;
   private JTextArea output;
   private Player players[];
   private ServerSocket server;
   private int currentPlayer;
 
   public TicTacToeServer()
   {
      super( "Tic-Tac-Toe Server" );
 
      board = new byte[ 9 ];
      xMove = true;
      players = new Player[ 2 ];
      currentPlayer = 0;
  
      // set up ServerSocket
      try {
         server = new ServerSocket( 5000, 2 );
      }
      catch( IOException e ) {
         e.printStackTrace();
         System.exit( 1 );
      }
 
      output = new JTextArea();
      getContentPane().add( output, BorderLayout.CENTER );
      output.setText( "Server awaiting connections\n" );
 
      setSize( 300, 300 );
      show();
   }
 
   // wait for two connections so game can be played
   public void execute()
   {
      for ( int i = 0; i < players.length; i++ ) {
         try {
            players[ i ] =
               new Player( server.accept(), this, i );
            players[ i ].start();
         }
         catch( IOException e ) {
            e.printStackTrace();
            System.exit( 1 );
         }
      }
 
      // Player X is suspended until Player O connects.
      // Resume player X now.          
      synchronized ( players[ 0 ] ) {
         players[ 0 ].threadSuspended = false;   
         players[ 0 ].notify();
      }
   
   }
    
   public void display( String s )
   {
      output.append( s + "\n" );
   }
  
   // Determine if a move is valid.
   // This method is synchronized because only one move can be
   // made at a time.
   public synchronized boolean validMove( int loc,
                                          int player )
   {
      boolean moveDone = false;
 
      while ( player != currentPlayer ) {
         try {
            wait();
         }
         catch( InterruptedException e ) {
            e.printStackTrace();
         }
      }
 
      if ( !isOccupied( loc ) ) {
         board[ loc ] =
            (byte) ( currentPlayer == 0 ? 'X' : 'O' );
         currentPlayer = ( currentPlayer + 1 ) % 2;
         players[ currentPlayer ].otherPlayerMoved( loc );
         notify();    // tell waiting player to continue
         return true;
      }
      else
         return false;
   }
 
   public boolean isOccupied( int loc )
   {
      if ( board[ loc ] == 'X' || board [ loc ] == 'O' )
          return true;
      else
          return false;
   }
 
   public boolean gameOver()
   {
      // Place code here to test for a winner of the game
      return false;
   }
 
   public static void main( String args[] )
   {
      TicTacToeServer game = new TicTacToeServer();
 
      game.addWindowListener( new WindowAdapter() {
        public void windowClosing( WindowEvent e )
            {
               System.exit( 0 );
            }
         }
      );
 
      game.execute();
   }
}
 
// Player class to manage each Player as a thread
class Player extends Thread {
   private Socket connection;
   private DataInputStream input;
   private DataOutputStream output;
   private TCPServer control;
   private int number;
   private String mark;
   protected boolean threadSuspended = true;
 
   public Player( Socket s, TCPServer t, int num )
   {
      mark = ( num == 0 ? 'X' : 'O' );
 
      connection = s;
       
      try {
         input = new DataInputStream(
                    connection.getInputStream() );
         output = new DataOutputStream(
                    connection.getOutputStream() );
      }
      catch( IOException e ) {
         e.printStackTrace();
         System.exit( 1 );
      }
 
      control = t;
      number = num;
   }
 
   public void otherPlayerMoved( int pos[] )
   {
      try {
         output.writeUTF( "Opponent moved" );
         output.writeInt( pos[0], pos[1] );
      }
      catch ( IOException e ) { e.printStackTrace(); }
   }
 
   public void run()
   {
      boolean done = false;
 
      try {
         control.display( "Player " +
            ( number == 0 ? 'X' : 'O' ) + " connected" );
         output.writeChar( mark );
         output.writeUTF( "Player " +( number == 0 ? "X connected\n" :"O connected, please wait\n" ) );
 
         // wait for another player to arrive
         if ( mark == 'X' ) {
            output.writeUTF( "Waiting for another player" );
 
            try {
               synchronized( this ) {   
                  while ( threadSuspended )
                     wait();  
               }
            } 
            catch ( InterruptedException e ) {
               e.printStackTrace();
            }
 
            output.writeUTF("Other player connected. Your move." );
         }
 
         // Play game
         while ( !done ) {
            int location = input.readInt();
 
            if ( control.validMove( location, number ) ) {
               control.display( "loc: " + location );
               output.writeUTF( "Valid move." );
            }
            else
               output.writeUTF( "Invalid move, try again" );
 
            if ( control.gameOver() )
               done = true;
         }         
 
         connection.close();
      }
      catch( IOException e ) {
         e.printStackTrace();
         System.exit( 1 );
      }
   }
}                                                         
 
// Client for the TicTacToe program
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
 
// Client class to let a user play Tic-Tac-Toe with
// another user across a network.
public class TicTacToeClient extends JApplet
                             implements Runnable {
   private JTextField id;
   private JTextArea display;
   private JPanel boardPanel, panel2;
   private Square board[][], currentSquare;
   private Socket connection;
   private DataInputStream input;
   private DataOutputStream output;
   private Thread outputThread;
   private char myMark;
   private boolean myTurn;
 
   // Set up user-interface and board
   public void init()
   {
      display = new JTextArea( 4, 30 );
      display.setEditable( false );
      getContentPane().add( new JScrollPane( display ),
                            BorderLayout.SOUTH );
 
      boardPanel = new JPanel();
      GridLayout layout = new GridLayout( 3, 3, 0, 0 );
      boardPanel.setLayout( layout );
 
      board = new Square[ 3 ][ 3 ];
 
      // When creating a Square, the location argument to the
      // constructor is a value from 0 to 8 indicating the
      // position of the Square on the board. Values 0, 1,
      // and 2 are the first row, values 3, 4, and 5 are the
      // second row. Values 6, 7, and 8 are the third row.
      for ( int row = 0; row < board.length; row++ )
      {
         for ( int col = 0;
                   col < board[ row ].length; col++ ) {
            board[ row ][ col ] =
               new Square( ' ', row * 3 + col );
            board[ row ][ col ].addMouseListener(
               new SquareListener(
                  this, board[ row ][ col ] ) );
 
            boardPanel.add( board[ row ][ col ] );        
         }
      }
 
      id = new JTextField();
      id.setEditable( false );
       
      getContentPane().add( id, BorderLayout.NORTH );
       
      panel2 = new JPanel();
      panel2.add( boardPanel, BorderLayout.CENTER );
      getContentPane().add( panel2, BorderLayout.CENTER );
   }
 
   // Make connection to server and get associated streams.
   // Start separate thread to allow this applet to
   // continually update its output in text area display.
   public void start()
   {
      try {
         connection = new Socket(
            InetAddress.getByName( "127.0.0.1" ), 5000 );
         input = new DataInputStream(
                        connection.getInputStream() );
         output = new DataOutputStream(
                        connection.getOutputStream() );
      }
      catch ( IOException e ) {
         e.printStackTrace();         
      }
 
      outputThread = new Thread( this );
      outputThread.start();
   }
 
   // Control thread that allows continuous update of the
   // text area display.
   public void run()
   {
      // First get player's mark (X or O)
      try {
         myMark = input.readChar();
         id.setText( "You are player \"" + myMark + "\"" );
         myTurn = ( myMark == 'X' ? true  : false );
      }
      catch ( IOException e ) {
         e.printStackTrace();         
      }
 
      // Receive messages sent to client
      while ( true ) {
         try {
            String s = input.readUTF();
            processMessage( s );
         }
         catch ( IOException e ) {
            e.printStackTrace();         
         }
      }
   }
 
   // Process messages sent to client
   public void processMessage( String s )
   {
      if ( s.equals( "Valid move." ) ) {
         display.append( "Valid move, please wait.\n" );
         currentSquare.setMark( myMark );
         currentSquare.repaint();
      }
      else if ( s.equals( "Invalid move, try again" ) ) {
         display.append( s + "\n" );
         myTurn = true;
      }
      else if ( s.equals( "Opponent moved" ) ) {
         try {
            int loc = input.readInt();
  
            board[ loc / 3 ][ loc % 3 ].setMark(
                  ( myMark == 'X' ? 'O' : 'X' ) );
            board[ loc / 3 ][ loc % 3 ].repaint();
                  
            display.append(
               "Opponent moved. Your turn.\n" );
            myTurn = true;
         }
         catch ( IOException e ) {
            e.printStackTrace();         
         }
      }
      else
         display.append( s + "\n" );
 
      display.setCaretPosition(
         display.getText().length() );
   }
 
   public void sendClickedSquare( int loc )
   {
      if ( myTurn )
         try {
            output.writeInt( loc );
            myTurn = false;
         }
         catch ( IOException ie ) {
            ie.printStackTrace();         
         }
   }
 
   public void setCurrentSquare( Square s )
   {
      currentSquare = s;
   }
}
 
// Maintains one square on the board
class Square extends JPanel {
   private char mark;
   private int location;
 
   public Square( char m, int loc)
   {
      mark = m;
      location = loc;
      setSize ( 30, 30 );
       
      setVisible(true);
   }
 
   public Dimension getPreferredSize() { 
      return ( new Dimension( 30, 30 ) );
   }
 
   public Dimension getMinimumSize() {
      return ( getPreferredSize() );
   }
 
   public void setMark( char c ) { mark = c; }
 
   public int getSquareLocation() { return location; }
 
   public void paintComponent( Graphics g )
   {
      super.paintComponent( g );
      g.drawRect( 0, 0, 29, 29 );
      g.drawString( String.valueOf( mark ), 11, 20 );   
   }
}
 
class SquareListener extends MouseAdapter {
   private TicTacToeClient applet;
   private Square square;
 
   public SquareListener( TicTacToeClient t, Square s )
   {
      applet = t;
      square = s;
   }
 
   public void mouseReleased( MouseEvent e )
   {
      applet.setCurrentSquare( square );
      applet.sendClickedSquare( square.getSquareLocation() );
   }
}