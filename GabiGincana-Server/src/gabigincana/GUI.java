package gabigincana;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Point;
import java.awt.CardLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Canvas;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Color;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;

public class GUI {
	public JFrame frm;
	public JTextField inPort, inMaxClients;
	public JButton btnIniciarServidor, btnDesconectarServidor;
	public JTextField textField;
	public JTextArea txtInfo, txtLog;
	public JScrollPane scllClients, scllLog;
	
	public GUI(){
		frm = new JFrame("GabiGincana");
		frm.getContentPane().setLayout(null);
		frm.setSize(445, 685);
		frm.setDefaultCloseOperation(frm.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Conexi\u00F3n", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 55, 408, 84);
		frm.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblPort = new JLabel("Puerto: ");
		lblPort.setBounds(10, 22, 69, 14);
		panel.add(lblPort);
		
		inPort = new JTextField();
		inPort.setBounds(89, 19, 92, 20);
		panel.add(inPort);
		inPort.setColumns(10);
		
		JLabel lblClientesMaximos = new JLabel("Clientes maximos: ");
		lblClientesMaximos.setBounds(191, 22, 125, 14);
		panel.add(lblClientesMaximos);
		
		inMaxClients = new JTextField();
		inMaxClients.setBounds(326, 19, 71, 20);
		panel.add(inMaxClients);
		inMaxClients.setColumns(10);
		
		btnIniciarServidor = new JButton("Iniciar servidor");
		btnIniciarServidor.setBackground(new Color(204, 255, 204));
		btnIniciarServidor.setForeground(new Color(0, 0, 0));
		btnIniciarServidor.setBounds(10, 50, 182, 23);
		panel.add(btnIniciarServidor);
		
		btnDesconectarServidor = new JButton("Desconectar servidor");
		btnDesconectarServidor.setBackground(new Color(255, 153, 153));
		btnDesconectarServidor.setBounds(215, 50, 182, 23);
		panel.add(btnDesconectarServidor);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(10, 547, 408, 88);
		frm.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(128, 26, 47, 20);
		panel_2.add(textField);
		textField.setColumns(10);
		
		JLabel lblClientenumero = new JLabel("Cliente (Numero):");
		lblClientenumero.setBounds(11, 29, 107, 14);
		panel_2.add(lblClientenumero);
		
		JComboBox cbCommandsList = new JComboBox();
		cbCommandsList.setBackground(new Color(220, 220, 220));
		cbCommandsList.setModel(new DefaultComboBoxModel(new String[] {"Expulsar jugador", "A\u00F1adir punto al jugador", "Quitar punto al jugador", "Reiniciar juego del jugador", "Reiniciar juego para todos"}));
		cbCommandsList.setBounds(184, 26, 214, 20);
		panel_2.add(cbCommandsList);
		
		JButton btnEjecutarComando = new JButton("Ejecutar comando");
		btnEjecutarComando.setBackground(new Color(220, 220, 220));
		btnEjecutarComando.setBounds(11, 54, 387, 23);
		panel_2.add(btnEjecutarComando);
		
		JLabel lblGabigincanaControlPanel = new JLabel("SMRGincana Control Panel");
		lblGabigincanaControlPanel.setBounds(10, 11, 408, 33);
		frm.getContentPane().add(lblGabigincanaControlPanel);
		lblGabigincanaControlPanel.setFont(new Font("MV Boli", Font.BOLD, 20));
		lblGabigincanaControlPanel.setHorizontalAlignment(JLabel.CENTER);
		
		scllLog = new JScrollPane();
		scllLog.setBounds(10, 150, 408, 217);
		frm.getContentPane().add(scllLog);
		
		txtLog = new JTextArea();
		txtLog.setLineWrap(true);
		scllLog.setViewportView(txtLog);
		txtLog.setEditable(false);
		txtLog.setForeground(Color.BLACK);
		txtLog.setBackground(Color.WHITE);
		
		scllClients = new JScrollPane();
		scllClients.setBounds(10, 378, 408, 158);
		frm.getContentPane().add(scllClients);
		
		txtInfo = new JTextArea();
		txtInfo.setLineWrap(true);
		scllClients.setViewportView(txtInfo);
		txtInfo.setForeground(Color.BLACK);
		txtInfo.setEditable(false);
		txtInfo.setBackground(Color.WHITE);
		
		frm.setVisible(true);
	}
	
	public void printLog(String txt){
		this.txtLog.setText(this.txtLog.getText()+new Date()+" | "+txt);
		Dimension tamanhoTextArea = txtLog.getSize();
		Point p = new Point(
		   0,
		   tamanhoTextArea.height);
		scllLog.getViewport().setViewPosition(p);
	}
	
	public void printClients(String txt){
		this.txtInfo.setText(txt);
	}
}
