//package ro;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class servidor {

  //Creo una lista de sockets, donde guardo los sockets que se vayan conectando
  private LinkedList<Socket> usuarios = new LinkedList<Socket>();
	private ServerSocket servidor;
	
	public final Runnable TCP;
	public final Runnable UDP;
	
	static int puerto;
	static String direc_fichero;
	static File fichero;
	
	
	
	public servidor (){//ejecucion de ambos servidores simultaneamente (servidores tcp y udp) mediante dos hilos
		TCP = new Runnable (){
			public void run (){
				servidor.this.conexionTCP();
			}
		};
		UDP = new Runnable (){
			public void run (){
				servidor.this.conexionUDP();
			}
		};
	}
	
    public static void main(String[] args) {
		puerto=Integer.parseInt(args[0]);
	  String datos = args [1];
		fichero=new File(datos);
		direc_fichero = fichero.getAbsolutePath();  
    servidor servidor= new servidor();
    new Thread(servidor.TCP).start();
    new Thread(servidor.UDP).start();
      }
    

    public void conexionUDP(){
  
        try {
            DatagramSocket socketUDP = new DatagramSocket(puerto);
            byte[] bufer = new byte[1000];
            DatagramPacket peticion = new DatagramPacket(bufer, bufer.length);
            int numero_clientes = 1;

            
            while (true) {
              System.out.println("Escuchando UDP");  
              socketUDP.receive(peticion);// Leo una petición del DatagramSocket
              
              //una vez recibo una peticion de un cliente, leo una linea del fichero y se la mando              
              FileReader fr = null;
              BufferedReader br = null;
              BufferedReader br2 = null;

              try {

                //En UDP hay que enviar una linea del fichero, para que no sea siempre la misma lo he programado de tal
                //forma que en la primera conexion UDP le envie la primera linea del fichero, en la segunda la segunda, etc
                //al finalizar el fichero, volveria a enviarle la primera
                
                 fr = new FileReader (fichero);
                 br = new BufferedReader(fr);
                 String linea;
              //   linea=br.readLine();
                 int num_lineas = 0;
                 while((linea=br.readLine())!=null){
                	 if (linea==null){
                		 break;
                	 }
                	 num_lineas++;
                 }

               	 if (numero_clientes == num_lineas+1){//he llegado al fin del fichero, vuelvo a enviarles la primera linea
            	   	 numero_clientes =1;
              	 }

                 
                 int linea_estoy = 0;
                 fr = new FileReader (fichero);
                 br2 = new BufferedReader(fr);
                 while (linea_estoy != numero_clientes){
                	 linea=br2.readLine();
                	 linea_estoy++;	            	 
                 }
                 if (linea_estoy == 0 & numero_clientes==0){
                	 linea=br2.readLine();
                 }
                // System.out.println (linea_estoy+numero_clientes);
                 byte [] linea_byte = linea.getBytes();
                 DatagramPacket respuesta = new DatagramPacket(linea_byte, linea_byte.length,peticion.getAddress(), peticion.getPort()); // Construyo el DatagramPacket para enviar la respuesta
                 socketUDP.send(respuesta);// Enviamos la respuesta(linea del mensaje)
                 
                 numero_clientes++;
                 
                 fr.close();
                 br.close();
              }   
              catch(Exception e){
                 e.printStackTrace();
              }finally{
                 try{                    
                    if( null != fr ){   
                       fr.close();     
                    }                  
                 }catch (Exception e2){ 
                    e2.printStackTrace();
                 }
              }         
            } 
          } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
          } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
          }  catch (Exception e) {
                e.printStackTrace();   
      }    	
    }
	
    
    public void conexionTCP(){//Funcion para que el servidor empieze a recibir conexiones de clientes
    	try {
            System.out.println("Escuchando TCP");  
            servidor = new ServerSocket(puerto);
            while(true){//Ciclo infinito para estar escuchando por nuevos clientes
                Socket cliente = servidor.accept();//Cuando un cliente se conecte guardamos el socket en nuestra lista
                usuarios.add(cliente);
                Runnable  run = new HiloServidor(cliente,usuarios,direc_fichero);//Instanciamos un hilo que estara atendiendo al cliente y lo ponemos a escuchar
                Thread hilo = new Thread(run);
                hilo.start();         
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
          } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
          }  catch (Exception e) {
                e.printStackTrace();    
             
      }
    }
}

	

