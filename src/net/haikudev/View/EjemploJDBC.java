package net.haikudev.View;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class EjemploJDBC extends JFrame {

	private JPanel contentPane;
	private JTextField varNomUsuario;
	private JTextField varMail;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EjemploJDBC frame = new EjemploJDBC();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EjemploJDBC() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 689, 469);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblUsuario = new JLabel("Usuario:");
		panel.add(lblUsuario);
		
		varNomUsuario = new JTextField();
		panel.add(varNomUsuario);
		varNomUsuario.setColumns(10);
		
		JLabel lblCorreo = new JLabel("Correo:");
		panel.add(lblCorreo);
		
		varMail = new JTextField();
		panel.add(varMail);
		varMail.setColumns(10);
		
		JButton btnBuscar = new JButton("Buscar");
		panel.add(btnBuscar);
	}

}
