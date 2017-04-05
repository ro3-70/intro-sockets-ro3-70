//package ro;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

	public class cliente implements Runnable{
	    private Socket cliente;
	    private DataInputStream in;
		BufferedReader entrada;
		String linea;
	
	    public void run_tcp(String ip, int puerto) {
	        try{
	        	cliente = new Socket (ip, puerto);//creamos un socket con los paramatros de la ip y el puerto, una vez hecho esto, si el servidor esta esperando se realizara la conexion, sino hay srvidor se genera error y termina programa (por el try catch)
	            in = new DataInputStream(cliente.getInputStream());  	
	            while (true){//Ciclo infinito que escucha por mensajes del servidor y los muestra en el panel
	            	entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
	            	linea = entrada.readLine();
	            	if (linea != null){
	            		System.out.println (linea);
	            	}
	            } 
	        }catch (SocketException e) {
	            System.out.println("Socket: " + e.getMessage());
	          } catch (IOException e) {
	            System.out.println("IO: " + e.getMessage());
	          }  catch (Exception e) {
	                e.printStackTrace();        
	      }
	    }
	    public void run_udp(String ip, int puerto) {
	    	 try {
	    	      DatagramSocket socketUDP = new DatagramSocket();
	    	      String sms = "es udp";
	    	      String mensaje_formateado ="";
	    	      byte[] mensaje = sms.getBytes();
	    	      InetAddress hostServidor = InetAddress.getByName(ip);
	    	      
	    	      DatagramPacket peticion = new DatagramPacket(mensaje, sms.length(), hostServidor,puerto);// Construyo un datagrama para enviar el mensaje al servidor
         	      socketUDP.send(peticion);// Envio el datagrama
	    	      
	    	      byte[] bufer = new byte[1000];
	    	      DatagramPacket respuesta =new DatagramPacket(bufer, bufer.length);// Construyo el DatagramPacket que contendra la respuesta
	    	      socketUDP.receive(respuesta);
	    	      
	    	      mensaje_formateado = new String(bufer).trim(); //formateo
	    	   
	    	      System.out.println(mensaje_formateado);// Envio la respuesta del servidor a la salida estandar
	    	      
	    	      socketUDP.close();// Cierro el socket

	    	    } catch (SocketException e) {
	                System.out.println("Socket: " + e.getMessage());
	            } catch (IOException e) {
	              System.out.println("IO: " + e.getMessage());
	            }  catch (Exception e) {
	                  e.printStackTrace();          
	        }
	    }
	    
		public void run() {//la runnable necesita esta			
		}
	    
	    public static void main(String[] args) {
			final String ip =args[1];
			final String modo_conexion = args [0];
			final int puerto=Integer.parseInt(args[2]);
			
			cliente c= new cliente();
			if (modo_conexion.equals("-tcp")){
		        c.run_tcp(ip, puerto);
			}
			if (modo_conexion.equals("-udp")){
		        c.run_udp(ip, puerto);
			}
	    }
	}