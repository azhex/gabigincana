package gabigincana;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;
import java.awt.Font;
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
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class GUI {
	public JFrame frm;
	public JTextField inPort, inMaxClients;
	public JButton btnIniciarServidor, btnDesconectarServidor;
	private JTextField textField;
	
	public GUI(){
		frm = new JFrame("GabiGincana");
		frm.getContentPane().setLayout(null);
		frm.setSize(444, 620);
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
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.DARK_GRAY);
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_1.setBounds(10, 150, 408, 217);
		frm.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JTextArea txtLog = new JTextArea();
		txtLog.setEditable(false);
		txtLog.setForeground(Color.GREEN);
		txtLog.setBounds(10, 11, 388, 195);
		txtLog.setBackground(Color.DARK_GRAY);
		panel_1.add(txtLog);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(10, 486, 408, 84);
		frm.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(128, 26, 47, 20);
		panel_2.add(textField);
		textField.setColumns(10);
		
		JLabel lblClientenumero = new JLabel("Cliente (Numero):");
		lblClientenumero.setBounds(11, 29, 107, 14);
		panel_2.add(lblClientenumero);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Expulsar jugador", "A\u00F1adir punto al jugador", "Quitar punto al jugador", "Reiniciar juego del jugador", "Reiniciar juego para todos"}));
		comboBox.setBounds(184, 26, 214, 20);
		panel_2.add(comboBox);
		
		JButton btnEjecutarComando = new JButton("Ejecutar comando");
		btnEjecutarComando.setBounds(245, 57, 153, 23);
		panel_2.add(btnEjecutarComando);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(10, 378, 408, 97);
		frm.getContentPane().add(panel_3);
		panel_3.setLayout(null);
		panel_3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_3.setBackground(Color.DARK_GRAY);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 11, 388, 75);
		panel_3.add(textArea);
		textArea.setForeground(Color.GREEN);
		textArea.setEditable(false);
		textArea.setBackground(Color.DARK_GRAY);
		
		JLabel lblGabigincanaControlPanel = new JLabel("GabiGincana Control Panel");
		lblGabigincanaControlPanel.setBounds(33, 11, 356, 33);
		frm.getContentPane().add(lblGabigincanaControlPanel);
		lblGabigincanaControlPanel.setFont(new Font("MV Boli", Font.BOLD, 20));
		
		frm.setVisible(true);
	}
}
