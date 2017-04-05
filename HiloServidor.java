//package ro;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

	public class HiloServidor implements Runnable{
	    private Socket socket;
	    private LinkedList<Socket> usuarios = new LinkedList<Socket>();//Lista de los usuarios conectados al servidor
	    PrintWriter alCliente;
	    String direccion;
	
	    public HiloServidor(Socket soc,LinkedList users, String dir){//Constructor que recibe el socket que atendera el hilo y la lista de usuarios conectados
	        socket = soc;
	        usuarios = users;
	        direccion = dir;
	    }
	
	    public void run() {
	   			try {
					alCliente = new PrintWriter(socket.getOutputStream(),true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

	   		File archivo =null;
	        FileReader fr = null;
	        BufferedReader br=null ;

	        try {
	           // Apertura del fichero y creacion de BufferedReader para poder
	           archivo = new File (direccion);
	           fr = new FileReader (archivo);
	           br = new BufferedReader(fr);

	           // Lectura del fichero
	           String linea;
	           while (true){
	        	   linea=br.readLine();
	        	   if ((linea==null)){
	            	   System.out.println ("Fichero finalizado");
	        		   break;
	        	   }
	        	   alCliente.println(linea);
		           Scanner input=new Scanner(System.in);
	        	   System.out.print ("Por favor introduzca una cadena para visualizar la siguiente linea del fichero:");
	        	   String entradaTeclado = input.nextLine ();	        	   
	           }
	            //mediante este bucle cierro el socket cuando he finalizado la lectuta del fichero
	            for (int i = 0; i < usuarios.size(); i++) {
	                if(usuarios.get(i) == socket){
	                    usuarios.remove(i);
	                    socket.close();
	                    break;
	                } 
	            }
	        }
	        catch (SocketException e) {
	            System.out.println("Socket: " + e.getMessage());
	          } catch (IOException e) {
	            System.out.println("IO: " + e.getMessage());
	          }  catch (Exception e) {
	                e.printStackTrace();      
	      }finally{// En el finally cierro el fichero, para asegurarme que se cierra tanto si todo va bien como si salta una excepcion.
	           try{                    
	              if( null != fr ){   
	                 fr.close();     
	              }                  
	           }catch (Exception e2){ 
	              e2.printStackTrace();
	           }
	        } 	      
	    }
	}