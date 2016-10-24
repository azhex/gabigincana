package gabigincana;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
	public ServerSocket serverSocket;
	public Socket[] clientsSocket;
	public int maxClients;
	
	public Server(int port, int mC) {
		try{
			maxClients = mC;
			clientsSocket = new Socket[maxClients];
			serverSocket = new ServerSocket(port);
			
			System.out.println("[GabiGincana] Servidor iniciado puerto: " + String.valueOf(port));
			
			new Thread(new Runnable(){
				public void run(){
					try {
						System.out.println("[GabiGincana] Esperando conexiones entrantes [OK]");
						waitConnection();
					} catch (IOException e) {
						System.out.println("[GabiGincana] Esperando conexiones entrantes [ERROR] [" + e.toString() + "]");
					}
				}
			}).start();
			
			new Thread(new Runnable(){
				public void run(){
					try {
						System.out.println("[GabiGincana] Connection manager [OK]");
						manageConnections();
					} catch (IOException | InterruptedException e) {
						System.out.println("[GabiGincana] Connection manager [ERROR] [" + e.toString() + "]");
					}
				}
			}).start();
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					System.out.println("[GabiGincana] Enviando informacion de procesamiento requerida a los clientes, intervalo de 5 seg [OK]");
					while(true){
						for(int i=0;i < maxClients; i++){
							if(clientsSocket[i] != null && clientsSocket[i].isConnected()){
								sendClientList(clientsSocket[i], String.valueOf(i));
							}
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			
		} catch (IOException e1) {
			System.out.println("[GabiGincana] Error al iniciar servidor [" + e1.toString() + "]");
		}
	}
	
	public void closeConnections() throws IOException{
		for(int i=0;i<clientsSocket.length;i++){
			if(clientsSocket[i] != null){
				clientsSocket[i].close();
				clientsSocket[i] = null;
			}
		}
	}
	
	private void waitConnection() throws IOException{
		while(true){
			for(int i=0;i < maxClients; i++){
				if(clientsSocket[i] == null || clientsSocket[i].isInputShutdown()){
					final int clientN = i;
					clientsSocket[i] = serverSocket.accept();
					if(clientsSocket[i].isConnected()){
						System.out.println("Cliente conectado -> "+i);
						PrintWriter out = new PrintWriter(clientsSocket[i].getOutputStream(), true);
						new Thread(new Runnable(){
							public void run(){
								recibirMensajes(clientN);
							}
						}).start();
						System.out.println("[Mensaje de bienvenida a "+i+"]");
						out.println("[Bienvenido al servidor de azhex, eres el cliente -> "+i+"]");
						break;
					}
				}else{
					
				}
			}
		}
	}
	
	private void sendClientList(Socket cli, String id){
		String connMangData = "";
		
		connMangData += "clientslist-1-2-3-";
		connMangData += new Date()+"-4-5-6-";
		connMangData += "------------Connection Manager-------------4-5-6-";
		for(int i=0;i < maxClients;i++){
			if(clientsSocket[i] == null){
				connMangData += String.valueOf(i)+" -> null-4-5-6-";
			}else if(clientsSocket[i].isInputShutdown() || clientsSocket[i].isClosed() || clientsSocket[i].isOutputShutdown()){
				connMangData += String.valueOf(i)+" -> Desconectado-4-5-6-";
				clientsSocket[i] = null;
			}else{
				connMangData += String.valueOf(i)+" -> ["+clientsSocket[i].getInetAddress().toString()+":"+String.valueOf(clientsSocket[i].getPort())+"] [OK]-4-5-6-";
			}
		}
		connMangData += "-------------------------------------------4-5-6-";
		sendMsg(cli, connMangData, id, false);
	}
	
	private void manageConnections() throws IOException, InterruptedException{
		while(true){
			String connMangData = "";
			
			connMangData += new Date()+"\n";
			connMangData += "------------Connection Manager------------\n";
			for(int i=0;i < maxClients;i++){
				if(clientsSocket[i] == null){
					connMangData += String.valueOf(i)+" -> null\n";
				}else if(clientsSocket[i].isInputShutdown() || clientsSocket[i].isClosed() || clientsSocket[i].isOutputShutdown()){
					connMangData += String.valueOf(i)+" -> Desconectado\n";
					clientsSocket[i] = null;
				}else{
					connMangData += String.valueOf(i)+" -> ["+clientsSocket[i].getInetAddress().toString()+":"+String.valueOf(clientsSocket[i].getPort())+"] [OK]\n";
				}
			}
			connMangData += "------------------------------------------\n";
			
			Thread.sleep(1000);
		}
	}
	
	private void recibirMensajes(int i){
		while(true){
			if(clientsSocket[i] != null && !clientsSocket[i].isInputShutdown() && !clientsSocket[i].isClosed()){
				try {
					String data;
					BufferedReader in = new BufferedReader(new InputStreamReader(clientsSocket[i].getInputStream()));
					
					data = in.readLine();
					
					if(data == null){
						clientsSocket[i].close();
						clientsSocket[i] = null;
						break;
					}else{
						String[] dataAux = data.split("-1-2-3-");
						
						/*
						 * DataAux -> Datos extra que deben ser procesados
						 * Chat -> DataAux[0]=tipo de procesamiento, dataAux[1]:mensaje, dataAux[2]:client id
						 */
						
						if(dataAux.length > 1){
							if(dataAux.length == 2){
								if(dataAux[0].equals("sendall")){
									sendMsgAll(clientsSocket, "chatrecv-1-2-3-"+String.valueOf(i)+"-1-2-3-"+dataAux[1], false);
								}
							}else if(dataAux.length == 3){
								if(dataAux[0].equals("sendto")){
									sendMsg(clientsSocket[Integer.parseInt(dataAux[2].toString())], "chatrecv-1-2-3-"+String.valueOf(i)+"-1-2-3-"+dataAux[1], dataAux[2].toString(), false);
								}
							}else{
							}
						}else{
						}
					}
				} catch (IOException e1) {
					System.out.println("Connection reset error en recv");
				}
			}else{
				break;
			}
		}
	}
	
	private void sendMsgAll(Socket[] clientsSocket, String msg, boolean display){
		for(int i = 0;i < clientsSocket.length;i++){
			if(clientsSocket[i] != null){
				try {
					PrintWriter out = new PrintWriter(clientsSocket[i].getOutputStream(), true);
					out.println(msg);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void sendMsg(Socket clientSocket, String msg, String id, boolean display){
		if(clientSocket != null){
			try {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				out.println(msg);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
