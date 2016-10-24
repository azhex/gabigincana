package gabigincana;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Init {
	static Server serv;

	public static void main(String[] args) {
		System.out.println("[GabiGincana] Iniciado aplicacion");
		System.out.println("[GabiGincana] Creando interfaz grafica...");
		GUI gui = new GUI();
		gui.frm.setVisible(true);
		System.out.println("[GabiGincana] Interfaz grafica [OK]");
		
		gui.btnIniciarServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("[GabiGincana] Iniciado servidor puerto: " + gui.inPort.getText().toString());
				try{
					serv = new Server(Integer.parseInt(gui.inPort.getText()), Integer.parseInt(gui.inMaxClients.getText()));
				}catch(Exception e){
					System.out.println("[GabiGincana] [ERROR] Error al iniciar servidor, se necesitan datos numericos");
					JOptionPane.showMessageDialog(gui.frm, "Error al iniciar servidor, comprueba que los datos introducidos (Puerto y maxClientes) son valores numericos enteros");
				}
			}
		});
		
		gui.btnDesconectarServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					if(serv.serverSocket.isBound()){
						serv.closeConnections();
					}
					System.out.println("[GabiGincana] Conexiones cerradas, saliendo del programa [OK]");
					System.exit(1);
				}catch(IOException err){
					System.out.println("[GabiGincana] Error al cerrar conexiones [ERROR] [" + err.toString() + "]");
				}
			}
		});
	}

}
