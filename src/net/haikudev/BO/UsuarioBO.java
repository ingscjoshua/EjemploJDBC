package net.haikudev.BO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

import net.haikudev.DAO.UsuarioDAO;
import net.haikudev.TO.UsuarioTO;
import net.haikudev.Utilerias.UtileriasBD;

public class UsuarioBO {
	
	/**
	 * Metodo que invoca a <strong>UsuarioDAO</strong> para llevar a cabo el
	 * guardado de un objeto UsuarioDTO
	 * @author 
	 * @param 	unUsuarioDTO 	Usuario a buscar
	 * @throws 	SQLException	En caso de no encontrar datos
	 * @since 2011/11/23
	 */
	public void guardaUsuarioBO(UsuarioTO unUsuarioDTO) throws SQLException {
		
		UtileriasBD miUtileriasBD 	= new UtileriasBD();
		UsuarioDAO 	miUsuarioDAO 	= new UsuarioDAO();
		
		try {

			miUtileriasBD.abreConexion();
			miUtileriasBD.inhabilitaAutoCommit();
			miUsuarioDAO.guardaUsuario(unUsuarioDTO, miUtileriasBD.getConexion());

			miUtileriasBD.commit();

		} catch (Exception e) {
			miUtileriasBD.rollback();
			throw new SQLException(e);
		} finally {
			miUtileriasBD.cierraConexion();
		}
	}
	
	/**
	 * 
	 */
	public  ArrayList<UsuarioTO> getUsuarios()throws SQLException{
		UtileriasBD miUtileriasBD 	= new UtileriasBD();
		UsuarioDAO 	miUsuarioDAO 	= new UsuarioDAO();
		ArrayList<UsuarioTO>listaUsuarios;
		try {
			miUtileriasBD.abreConexion();
			listaUsuarios=miUsuarioDAO.consultaUsuarios(miUtileriasBD.getConexion());
		} catch (Exception e) {
			throw new SQLException(e);
		} finally {
			miUtileriasBD.cierraConexion();
		}
		return listaUsuarios;
	}
}
