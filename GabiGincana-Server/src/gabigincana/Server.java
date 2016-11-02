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

import javax.swing.JOptionPane;

public class Server {
	public ServerSocket serverSocket;
	public Socket[] clientsSocket;
	public String[] cliUser;
	public boolean[] cliPlaying;
	public int[] cliScore;
	public int maxClients;
	public String gincanaClave = "";
	public boolean conectado;
	private String separador1 = "-1-2-3-4-";
	private GUI gui;
	
	public Server(int port, int mC, GUI g) {
		try{
			maxClients = mC;
			gui = g;
			clientsSocket = new Socket[maxClients];
			cliUser = new String[maxClients];
			cliScore = new int[maxClients];
			cliPlaying = new boolean[maxClients];
			
			gincanaClave = JOptionPane.showInputDialog(gui.frm, "Introduce una clave para la gincana, cuando un usuario entre tendra que introducirla");
			if(gincanaClave == null || gincanaClave.equals("")){
				gincanaClave = "000000";
			}
			
			serverSocket = new ServerSocket(port);
			conectado = true;
			System.out.println("[GabiGincana] Servidor iniciado puerto: " + String.valueOf(port));
			gui.printLog("[Servidor iniciado puerto: " + String.valueOf(port) + " | Clave: " + gincanaClave + "]" + "\n");
			
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
					System.out.println("[GabiGincana] Enviando informacion de procesamiento requerida a los clientes, intervalo de 2 seg [OK]");
					while(conectado){
						for(int i=0;i < maxClients; i++){
							if(clientsSocket[i] != null && clientsSocket[i].isConnected()){
								sendRequiredData(clientsSocket[i], i);
							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			
		} catch (IOException e1) {
			System.out.println("[GabiGincana] Error al iniciar servidor [" + e1.toString() + "]");
			gui.printLog("[Error al iniciar servidor [" + e1.toString() + "]]" + "\n");
		}
	}
	
	public void closeConnections() throws IOException{
		for(int i=0;i<clientsSocket.length;i++){
			if(clientsSocket[i] != null){
				cerrarConexion(i);
			}
		}
		serverSocket.close();
		conectado = false;
		
		gui.printLog("[Servidor cerrado]" + "\n");
	}
	
	private void waitConnection() throws IOException{
		while(conectado){
			for(int i=0;i < maxClients; i++){
				if(clientsSocket[i] == null || clientsSocket[i].isInputShutdown()){
					final int clientN = i;
					clientsSocket[i] = serverSocket.accept();
					if(clientsSocket[i].isConnected()){
						System.out.println("Cliente conectado -> "+i);
						gui.printLog("[Cliente conectado -> " + String.valueOf(i) + "]" + "\n");
						new Thread(new Runnable(){
							public void run(){
								recibirMensajes(clientN);
							}
						}).start();
						
						break;
					}
				}
			}
		}
	}
	
	private void sendRequiredData(Socket cli, int id){
		int top = cliScore.length;
		
		for(int i=0;i < cliScore.length;i++){
			if(cliScore[i] < cliScore[id]){
				top--;
			}
		}
		
		cliScore[id] += 10;
		
		sendMsg(cli, "userInfo" + separador1 + String.valueOf(cliScore[id]) + separador1 + String.valueOf(top));
		
		return;
	}
	
	private void manageConnections() throws IOException, InterruptedException{
		while(conectado){
			String connMangData = "";
			
			connMangData += new Date()+"\n";
			connMangData += "------------Connection Manager------------\n";
			for(int i=0;i < maxClients;i++){
				if(clientsSocket[i] == null){
					connMangData += String.valueOf(i)+" -> null\n";
					cliUser[i] = "";
				}else if(clientsSocket[i].isInputShutdown() || clientsSocket[i].isClosed() || clientsSocket[i].isOutputShutdown()){
					connMangData += String.valueOf(i)+" -> Desconectado\n";
					cerrarConexion(i);
				}else{
					connMangData += String.valueOf(i)+" -> ["+cliUser[i]+"] ["+clientsSocket[i].getInetAddress().toString()+"] [OK]\n";
				}
			}
			connMangData += "------------------------------------------\n";
			gui.printClients(connMangData);
			
			Thread.sleep(1000);
		}
	}
	
	private void recibirMensajes(int i){
		while(conectado){
			if(clientsSocket[i] != null && !clientsSocket[i].isInputShutdown() && !clientsSocket[i].isClosed()){
				try {
					String data;
					BufferedReader in = new BufferedReader(new InputStreamReader(clientsSocket[i].getInputStream()));
					
					data = in.readLine();
					
					if(data == null){
						cerrarConexion(i);
						break;
					}else{
						String[] dataAux = data.split(separador1);
						
						if(dataAux.length > 1){
							if(dataAux.length == 3){
								if(dataAux[0].equals("login")){
									boolean repetido = false;
									for(int u=0;u<cliUser.length;u++){
										if(cliUser[u].equals(dataAux[1])){
											repetido = true;
											break;
										}
									}
									if(dataAux[1].toUpperCase().matches(".*SERVIDOR.*")){
										repetido = true;
									}
									if(dataAux[2].equals(gincanaClave)){
										if(!repetido){
											cliUser[i] = dataAux[1];
											sendMsg(clientsSocket[i], 
													"login" + separador1 +
													"OK");
											gui.printLog("[Login] IP: " + clientsSocket[i].getInetAddress() + " | USUARIO: " + dataAux[1] + " | CLAVE: " + dataAux[2] + "\n");
											System.out.println("Login desde " + clientsSocket[i].getInetAddress() + " user: " + dataAux[1] + " clave: " + dataAux[2]);
										}else{
											cliUser[i] = dataAux[1];
											sendMsg(clientsSocket[i], "USER_EXISTENTE");
											gui.printLog("[LoginError][USER_EXISTENTE] IP: " + clientsSocket[i].getInetAddress() + " | USUARIO: " + dataAux[1] + " | CLAVE: " + dataAux[2] + "\n");
											System.out.println("Login error [USER_EXISTENTE] desde " + clientsSocket[i].getInetAddress() + " user: " + dataAux[1] + " clave: " + dataAux[2]);
										}
									}else{
										cliUser[i] = dataAux[1];
										sendMsg(clientsSocket[i], "CLAVE_INCORRECTA");
										gui.printLog("[LoginError][CLAVE_INCORRECTA] IP: " + clientsSocket[i].getInetAddress() + " | USUARIO: " + dataAux[1] + " | CLAVE: " + dataAux[2] + "\n");
										System.out.println("Login error [CLAVE_INCORRECTA] desde " + clientsSocket[i].getInetAddress() + " user: " + dataAux[1] + " clave: " + dataAux[2]);
									}
								}else if(dataAux[0].equals("chatMsgSend")){
									sendMsgAll(clientsSocket, "chatMsgRecv"+ separador1 + "<b>" + dataAux[1] + "</b>" + separador1 + dataAux[2]);
									gui.printLog("[ChatMsg] Mensaje de [" + dataAux[1] + "] -> " + dataAux[2] + "\n");
								}
							}else if(dataAux.length == 2){
								if(dataAux[0].equals("playing")){
									if(dataAux[1].equals("OK")){
										cliPlaying[i] = true;
										sendMsgAll(clientsSocket, "chatMsgRecv" + separador1 + "<b><i><font color=\"#FA5858\">[SERVIDOR]" + separador1 + "[" + cliUser[i] + " ha entrado en el chat]</font></i></b>");
										gui.printLog("[ChatInfo] El usuario [" + cliUser[i] + "] ha entrado al chat\n");
									}else if(dataAux[1].equals("CLOSE")){
										cliPlaying[i] = false;
									}
								}
							}else{
								System.out.println("MSG: " + data);
							}
						}else{
							System.out.println("MSG: " + data);
						}
					}
				} catch (IOException e1) {
					System.out.println("Connection reset error en recv");
					cerrarConexion(i);
				}
			}else{
				break;
			}
		}
	}
	
	private void sendMsgAll(Socket[] clientsSocket, String msg){
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
	
	private void sendMsg(Socket clientSocket, String msg){
		if(clientSocket != null){
			try {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				out.println(msg);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void cerrarConexion(int id){
		if(clientsSocket[id] != null){
			try {
				clientsSocket[id].close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		clientsSocket[id] = null;
		cliUser[id] = "";
		cliScore[id] = 0; 
		cliPlaying[id] = false;
	}
}
