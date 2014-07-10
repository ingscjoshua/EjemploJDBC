package net.haikudev.View;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import net.haikudev.BO.UsuarioBO;
import net.haikudev.DAO.UsuarioDAO;
import net.haikudev.TO.UsuarioTO;

public class MainView{
	
	public static void main(String args[]){
		PropertyConfigurator.configure("log4j.properties");
		Logger log= Logger.getLogger(MainView.class.getName());
		UsuarioTO usuario= new UsuarioTO();
		UsuarioBO usuarioBO= new UsuarioBO();
		usuario.setNombreUsuario("Josue");
		usuario.setApellidoUsuario("Hernandez");
		usuario.setCorreoUsuario("jhernandez@gmail.com");
		ArrayList<UsuarioTO>ListaUsuarios;
		try {
			//usuarioBO.guardaUsuarioBO(usuario);
			ListaUsuarios=usuarioBO.getUsuarios();
			
			if(!ListaUsuarios.isEmpty()){
				for (int i = 0; i < ListaUsuarios.size(); i++) {
					UsuarioTO usuarioTO=ListaUsuarios.get(i);
					log.info("id usuario:"+ usuarioTO.getIdUsuario().toString());
					log.info("Usuario:"+usuarioTO.getNombreUsuario() );
					log.info("mail:" +usuarioTO.getCorreoUsuario());
				}
			}
			/*for (UsuarioTO usuarioTO : ListaUsuarios) {
				log.info("id usuario:"+ usuarioTO.getIdUsuario().toString());
				log.info("Usuario:"+usuarioTO.getNombreUsuario() );
				log.info("mail:" +usuarioTO.getCorreoUsuario());
			}*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
