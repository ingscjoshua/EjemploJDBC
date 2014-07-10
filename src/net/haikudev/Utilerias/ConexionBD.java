package net.haikudev.Utilerias;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class ConexionBD {
	/** Variable que guarda el valor del driver de la conexión */
	private String driver;
	
	/** Variable que guarda el valor de la url para la conexión */
	private String url;
	
	/** Variable que guarda el usuario para la conexión */
	private String usuario;
	
	/** Variable que guarda el password para la conexión */
	private String password;
	
	/** Variable que guarda el valor del Owner y Owner2  */
	protected String esqSimaTiendas="TIENDA_DB.";
	
	/** La conexión a la base de datos */
	protected Connection conexion;
	
	/** Variable para Log4j */
	private static Logger log=Logger.getLogger(ConexionBD.class.getName());
	
	/**
	 * Constructor de default de la clase
	 * @author Luis Fernando Castro
	 * @since 2011/08/30
	 */
	public ConexionBD(){
		driver	= "com.mysql.jdbc.Driver";
		url		= "jdbc:mysql://localhost/EJEMPLO_JDBC";
		usuario	= "root";
		password= "root";
	}
	
	/**
	 * Método para asignar el valor al atributo owner que es el dueño de los objetos
	 * de la BD de sima tienda
	 * 
	 * @author 
	 * @param unOwner
	 * @since 2011/08/30 
	 */
	public void setEsqSimaTiendas(String unEsqSimaTiendas) {
		esqSimaTiendas = unEsqSimaTiendas;
	}

	/**
	 * Método para obtener el valor al atributo owner que es el dueño de los objetos
	 * de la BD de sima tienda
	 * 
	 * @author 
	 * @return Cadena con el nombre del propietario de los objetos de BD
	 * @since 2011/08/30 
	 */
	public String getEsqSimaTiendas() {
		return esqSimaTiendas;
	}
	
	/**
	 * Obtiene directamente la conexión a la base de datos que se esta utilizando
	 * 
	 * @author 
	 * @return Apuntador a la base de datos
	 */
	public Connection getConexion() {
		return conexion;
	}

	/**
	 * Método para asignar una conexión al atributo conexion
	 * 
	 * @author 
	 * @param unaConexionBD Conexion abierta a la BD
	 * @since 2011/08/30 
	 */
	public void setConexion( Connection unaConexionBD ) {
		conexion = unaConexionBD;
	}
	
	/**
	 * Método que permite abrir una conexión a la BD de oracle
	 * 
	 * @author
	 * @throws SQLException Lanza el error cuando no puede conectarse a la BD
	 * @since 2011/08/30
	 */
	public void abreConexion() throws SQLException {
		try {
			Class.forName(this.driver);
			conexion = DriverManager.getConnection(this.url,this.usuario,this.password);
		}catch (Exception e) {
			log.error("Error al abrir la conexion a la base de datos");
			log.error("Descripcion del error:" + e.getMessage());
			throw new SQLException();
		} 
	}

	

	/**
	 * Inhabilita el modo Auto-commit.
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @since 2011/08/30 
	 */
	public void inhabilitaAutoCommit() throws SQLException {
		conexion.setAutoCommit(false);
	}

	/**
	 * Cierra la transacción.
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @since 2011/08/30
	 */
	public void commit() throws SQLException {
		conexion.commit();
	}

	/**
	 * Restablece la transacción.
	 * 
	 * @author Luis Fernando Castro
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @since 2011/08/30
	 */
	public void rollback() throws SQLException {
		conexion.rollback();
	}

	/**
	 * Cierra la conexion a la base de datos. Si la conexion fue abierta por otro objeto y
	 * pasada a la clase por el metodo setConexion(), entonces solo se cerrarán los objetos statement y
	 * resulset
	 * 
	 * @author Luis Fernando Castro
	 * @since 2011/08/15
	 */
	public void cierraConexion() {
		try {
			if( conexion != null){ 
				conexion.close();
				conexion = null;
			}
		}catch (Exception e) {
			log.error("Error.ConexionOracle.cierraConexion: Error al cerrar la conexión de la BD " +e.getMessage());
		}
	
	}

	/**
	 * Verifica si la clase tiene hecha una conexion a la base de datos
	 * 
	 * @author Luis Fernando Castro
	 * @return Verdadero - Si la clase tiene una conexión a la base de datos en caso contrario falso.
	 * @since 2011/08/15 
	 */
	public boolean isConectado() {
		if (conexion != null) {
			return true;
		}else {
			return false;
		}
	}

}
