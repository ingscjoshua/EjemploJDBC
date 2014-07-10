package net.haikudev.DAO;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.haikudev.TO.UsuarioTO;
import net.haikudev.Utilerias.UtileriasBD;

import org.apache.log4j.Logger;



/**
 * 
 * @author Josue Hernandez Ramirez 15/08/2011
 */
public class UsuarioDAO {

	private static Logger log = Logger.getLogger(UsuarioDAO.class.getName());
	

	

	/**
	 * Método que lleva a cabo el guardado de un objeto de tipo
	 * <strong>UsuarioDTO</strong> en la base de datos
	 * 
	 * @author 
	 * @param unUusuario
	 *            - Objeto a guardar
	 * @param unaConexionBD Conexion de base de datos
	 * @throws SQLException
	 */
	public void guardaUsuario(UsuarioTO unUsuarioTO, Connection unaConexionBD) throws SQLException {
		log.info("DAO : Entra a guardaUsuario");
		log.info("DAO : Usuario a guardar : " + unUsuarioTO.toString());
		UsuarioTO miUsuario = unUsuarioTO;
		
		int posParametro = 0;
		UtileriasBD miUtileriasBD = new UtileriasBD();
		
		miUtileriasBD.setConexion(unaConexionBD);
		miUtileriasBD.getSql().append("INSERT INTO ");
		miUtileriasBD.getSql().append("USUARIOS");
		miUtileriasBD.getSql().append("(NOMBRE_USUARIO, ");
		miUtileriasBD.getSql().append("APELLIDO_USUARIO, ");
		miUtileriasBD.getSql().append("CORREO_USUARIO) ");
		miUtileriasBD.getSql().append("VALUES (?, ?, ?)");
		
		
		miUtileriasBD.prepara();
		
		posParametro++;
		log.info("DAO : Setteando parametros");

		miUtileriasBD.getParametros().setString(posParametro, miUsuario.getNombreUsuario());
		posParametro++;
		miUtileriasBD.getParametros().setString(posParametro, miUsuario.getApellidoUsuario());
		posParametro++;
		miUtileriasBD.getParametros().setString(posParametro, miUsuario.getCorreoUsuario());
		
		miUtileriasBD.ejecutaUpdate();
		
		
		log.info("DAO : Finaliza");
	}
	
	
	/**
	 * Método que lleva a cabo la busqueda de usuarios en la bd.
	 * @param unUsuario - Objeto usuario que contiene las caracteristicas de la busqueda
	 * @param unaConexionBD Conexion de base de datos
	 * @return - Arreglo de objetos UsuarioDTO
	 * @throws SQLException
	 */
	public ArrayList<UsuarioTO> consultaUsuarios( Connection unaConexionBD) throws SQLException {
		ArrayList<UsuarioTO> listaUsuarios = new ArrayList<UsuarioTO>();
		UsuarioTO miUsuarioConsultado = null;
		
		
		int posParametro = 0;
		UtileriasBD miUtileriasBD = new UtileriasBD();
		ResultSet rs;
		miUtileriasBD.setConexion(unaConexionBD);
		
		miUtileriasBD.getSql().append("SELECT ");
		miUtileriasBD.getSql().append("* ");
		miUtileriasBD.getSql().append("FROM ").append("USUARIOS");
		
		log.info("DAO : SqlApend : " + miUtileriasBD.getSql());
		
		rs = miUtileriasBD.ejecutaSqlRs();
		
		if (rs != null) {
			while (rs.next()) {
				miUsuarioConsultado = new UsuarioTO();
				miUsuarioConsultado.setIdUsuario(rs.getInt("idUSUARIOS"));
				miUsuarioConsultado.setNombreUsuario(rs.getString("NOMBRE_USUARIO").trim());
				miUsuarioConsultado.setCorreoUsuario(rs.getString("CORREO_USUARIO").trim());
				listaUsuarios.add(miUsuarioConsultado);
			}
		}
		return listaUsuarios;
	}

}
