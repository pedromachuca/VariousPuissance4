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

class Server{  
    
    int amount=10000;  
      
    synchronized void withdraw(int amount)
    {  
        System.out.println("going to withdraw...");  
          
        if(this.amount<amount)
        {  
            System.out.println("Less balance; waiting for deposit...");  
            try{wait();}catch(Exception e){}  
            System.out.println("amount : "+amount);  
        }

        this.amount-=amount;  
        System.out.println("withdraw completed...");  
    }  
      
    synchronized void deposit(int amount)
    {  
        System.out.println("going to deposit...");  
        this.amount+=amount;  
        System.out.println("deposit completed... ");  
        notify();  
    }  

     public static void main(String args[])
    {  
        final Server c=new Server();  
        new Thread(){  
            public void run()
            {
                c.withdraw(15000);
                System.out.println("with amount : "+amount);  
            }  
        }.start();  
        new Thread(){  
            public void run()
            {
                c.deposit(10000);
                System.out.println("depo amount : "+amount);  
            }  
        }.start();  
      
    }
 
 }  