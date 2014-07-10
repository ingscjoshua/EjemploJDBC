package net.haikudev.Utilerias;

import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * Clase que sirve para ejecutar consultas a la BD 
 * 
 * @author Luis Fernando Castro
 * @version 1.0
 */
public class UtileriasBD {
	
	/** Variable para Log4j */
	private static Logger log=Logger.getLogger(UtileriasBD.class);
	
	/** Esta variable se utilizará para construir las consultas a la base de datos */
	protected StringBuilder sql;
	
	/** Variable para ejecutar SQL */
	protected PreparedStatement parametros=null;
	
	/** Resultset a la base de datos */
	protected ResultSet rs = null;
	
	/** Statement a la base de datos */
	protected Statement st = null;
	
	/** CallableStatement a la base de datos para procedimientos almacenados. */
	protected CallableStatement sto = null;
	
	/** Atributo para el manejo de la conexion a la BD */
	private ConexionBD conexionBD;
	
	/** Atributo para saber si se cierra una conexión si hay algún error ó es gestionada por una transaccion atómica */
	private boolean banderaAutocommit; 
	
	/**
	 * Constructor de default de la clase
	 * 
	 * @author Luis Fernando Castro
	 * @since 2011/08/15
	 */
	public UtileriasBD() throws SQLException{
		sql=new StringBuilder();
		conexionBD = new ConexionBD();
		banderaAutocommit = false;
	}
	
	/**
	 * Método que permite abrir una conexión a la BD de oracle
	 * 
	 * @author Luis Fernando Castro
	 * @throws SQLException Lanza el error cuando no puede conectarse a la BD
	 * @since 2011/08/30
	 */
	public void abreConexion() throws SQLException {
		conexionBD.abreConexion();
	}
	
	/**
	 * Ejecuta un procedimiento almacenado. La llamada a la base de datos se construye en la
	 * variable Sql y los carácteres que se utilizan para indicar la naturaleza de los
	 * parametros son los siguientes:
	 * 		C= Char , VarChar
	 *		R= Cursor
	 *		I= Integer
	 *		B= Double
	 *		D= Date
	 *		T= Time
	 * La ejecución del procedimiento almacenado se deposita en la variable sto. La variable Sql
	 * se modifica y se eliminan los caracteres utilizados para identificar la naturaleza de los
	 * parámetros
	 * @author Luis Fernando Castro
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @since 2011/08/15
	 */
	public CallableStatement ejecutaSp() throws SQLException  {
		try {
			String miconsulta=new String("");
			int miIndice = 0;
			int[] misTipos = new int[40];
			String miTipo = "";
			String miTemp = "";
			int miTip = 0;
			int misParametros = 0;
			int miTamanio = 0;
	
			//OBTIENE LA LISTA DE PARAMETROS DE SALIDA DE LA CADENA sSQL
			miconsulta=sql.toString();
			miTamanio = miconsulta.length();
			miIndice = miconsulta.indexOf("?");
			while (miIndice != -1) {
				miTipo = miconsulta.substring(miIndice + 1, miIndice + 2);
	
				/*
				PARAMETROS
				SIGUIENTES:
				C= Char, VarChar
				R= Cursor
				I= Integer
				F= Float
				B= Double
				D= Date
				T= Time
				 */
				if (miTipo.equals("C")) {
					miTip = java.sql.Types.CHAR;
				} else  if (miTipo.equals("R")) {
					miTip = java.sql.Types.OTHER;
				} else if (miTipo.equals("I")) {
					miTip = java.sql.Types.INTEGER;
				} else if (miTipo.equals("F")) {
					miTip = java.sql.Types.FLOAT;
				} else if (miTipo.equals("B")) {
					miTip = java.sql.Types.DOUBLE;
				} else if (miTipo.equals("T")) {
					miTip = java.sql.Types.TIME;
				} else {
					miTip = java.sql.Types.OTHER;
				}
			
				miTemp = miTemp + miconsulta.substring(0, miIndice + 1);
				miconsulta = miconsulta.substring(miIndice + 2, miTamanio);
	
				misTipos[misParametros] = miTip;
				misParametros++;
				
				miTamanio = miconsulta.length();
				miIndice = miconsulta.indexOf("?");
			}
	
			//COMPLETA LA LLAMADA AL PROCEDIMIENTO ALMACENADO
			if (misParametros > 0) {
				miconsulta = miTemp + miconsulta;
			}
	
			//VERIFICA SI EL STATEMENT ESTA ABIERTO
			if (sto != null) {
				//CIERRA EL STATEMENT
				sto.close();
				sto = null;
			}
	
			//PREPARA LA LLAMADA AL PROCEDIMIENTO ALMACENADO
			sto = conexionBD.conexion.prepareCall(miconsulta);
	
			//REGISTRA LOS PARAMETROS DE SALIDA
			for (int miParametro = 0; miParametro < misParametros;miParametro++) {
				sto.registerOutParameter(miParametro + 1, misTipos[miParametro]);
			}
			//EJECUTA EL PROCEDIMIENTO ALMACENADO
			sto.execute();
			
		}catch (SQLException e) {
			log.error("Error.UtileriasBD.ejecutaSp: Error al ejecutar el sp - ");
			log.error(sql.toString());
			log.error("Descripción del error:" + e.getMessage());
			if (banderaAutocommit)
				conexionBD.rollback();
			
			cierraConexion();
			throw e;
		} finally{
			sql=new StringBuilder();
		}
		
		return sto;
		
	}
	
	/**
	 * Inicializa la clase para ejecutar una consulta a la BD
	 * 
	 * @author Luis Fernando Castro
	 * @param unaConexionBD Conexión abierta a la BD
	 * @throws SQLException Error al inicializar el prepareStatement
	 * @since 2011/08/15
	 */
	public void prepara() throws SQLException {
		parametros = conexionBD.conexion.prepareStatement(sql.toString());
	}
	
	/**
	 * Ejecuta un procedimiento almacenado. A diferencia del método anterior este recibe como
	 * parámetro la llamada al procedimiento almacenado. Una vez ejecutado el método la variable
	 * Sql queda con la cadena que se utilizó para llamar al procedimiento almacenado.
	 * 
	 * @author Luis Fernando Castro
	 * @param unProcedimiento Cadena que representa la llamada al procedimiento almacenado.
	 * @return 
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @since 2011/08/15
	 */
	public CallableStatement ejecutaSp(StringBuilder unProcedimiento) throws SQLException {
		sql = unProcedimiento;
		return ejecutaSp();
	}
	
	/**
	 * Ejecuta una sentencia sql. Toma el valor de la variable sql y ejecuta la consulta sobre la
	 * base de datos, regresando un ResultSet. Este método emplea la variable st que es de tipo
	 * Statement para realizar la conexión a la base de datos.
	 * 
	 * @author Luis Fernando Castro
	 * @param unaConexionBD Conexion abierta a la BD
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @return Regresa la consulta sobre la base de datos en un objeto ResultSet.
	 * @since 2011/08/15
	 */
	public ResultSet ejecutaSqlRs() throws SQLException {
		
		try {
			//VERIFICA SI HAY ALGUNA CONSULTA ABIERTA
			if (rs != null) {
				//CIERRA LA CONSULTA
				rs.close();
				rs = null;
			}
			//VERIFICA SI EL STATEMENT ESTA ABIERTO
			if (st != null) {
				//CIERRA EL STATEMENT
				st.close();
				st = null;
			}
			if(parametros!=null){
				rs = parametros.executeQuery();	
			}else {
				//ABRE UN STATEMENT
				st = conexionBD.conexion.createStatement();
				rs = st.executeQuery(sql.toString());
			}
		}catch (SQLException e) {
			log.error("Error.UtileriasBD.ejecutaSqlRs: Error al ejecutar la siguiente consulta - ");
			log.error(sql.toString());
			log.error("Descripcion del error:" + e.getMessage());
			
			if (banderaAutocommit){
				conexionBD.rollback();
			}
				
			cierraConexion();
			throw e;
		}finally{
			sql=new StringBuilder();
		}
		return rs;
	}
	
	/**
	 * Ejecuta una setencia sql de INSERT, UPDATE o DELETE. Toma el valor de la variable Sql y 
	 * ejecuta la consulta sobre la base de datos. El resultado de la consulta será el número de 
	 * registros procesados por las sentencias INSERT, UPDATE o DELETE, o 0 para una setentencia 
	 * que no regresa algún valor cual es de tipo ResultSet. Este método emplea la variable 
	 * st que es de tipo Statement para realizar la conexion a la base de datos.
	 * 
	 * @author Luis Fernando Castro
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @return La cantidad de registros que fueron afectados.
	 * @since 2011/08/15
	 */
	public int ejecutaUpdate() throws SQLException {
		int iNumRegistros = 0;
		try {
			//VERIFICA SI HAY ALGUNA CONSULTA ABIERTA
			if (rs != null) {
				//CIERRA LA CONSULTA
				rs.close();
				rs = null;
			}

			//VERIFICA SI EL STATEMENT ESTA ABIERTO
			if (st != null) {
				//CIERRA EL STATEMENT
				st.close();
				st = null;
			}
			if(parametros!=null){
				iNumRegistros= parametros.executeUpdate();	
			}else {
				//ABRE UN STATEMENT
				st = conexionBD.conexion.createStatement();

				iNumRegistros = st.executeUpdate(sql.toString());
			}
		}
		catch (SQLException e) {
			log.error("Error.UtileriasBD.ejecutaUpdate: Error al ejecutar la siguiente actualizacion - ");
			log.error(sql.toString());
			log.error("Descripcion del error:" + e.getMessage());
			
			if (banderaAutocommit){
				conexionBD.rollback();
			}
			
			cierraConexion();
			throw e;
		}finally{
			sql=new StringBuilder();
		}
		return iNumRegistros;
	}
	
	/**
	 * Ejecuta un lote de setencias sql INSERT, UPDATE o DELETE. El resultado de la consulta será el número de 
	 * registros procesados por cada una de las secuencias dadas (en un arreglo int[]), o 0 para una setentencia 
	 * que no regresa algún valor cual es de tipo ResultSet. 
	 * 
	 * @author Herlindo Chavez
	 * @return int[] - Arreglo de tipo primitivo indicando el resultado de cada operacion
	 * @throws BatchUpdateException
	 * @throws SQLException
	 */
	public int[] ejecutaUpdateBatch() throws BatchUpdateException, SQLException {
		int[] iNumRegistros = null;
		try {
			//VERIFICA SI HAY ALGUNA CONSULTA ABIERTA
			if (rs != null) {
				//CIERRA LA CONSULTA
				rs.close();
				rs = null;
			}

			//VERIFICA SI EL STATEMENT ESTA ABIERTO
			if (st != null) {
				//CIERRA EL STATEMENT
				st.close();
				st = null;
			}
			log.info("UtileriasBD: A punto de ejecutar el executeBatch()");
			if(parametros!=null){
				log.info("UtileriasBD: Parametros Trae algo");
				iNumRegistros= parametros.executeBatch();	
			}
			log.info("UtileriasBD: se acaba de ejecutar el executeBatch()");
		} catch(BatchUpdateException b) {
			log.error("SQLException: " + b.getMessage());
			log.error("SQLState:  " + b.getSQLState());
			log.error("Message:  " + b.getMessage());
			log.error("Vendor:  " + b.getErrorCode());
			log.error("Update counts:  ");
			int [] updateCounts = b.getUpdateCounts();
			for (int i = 0; i < updateCounts.length; i++) {
				System.err.print(updateCounts[i] + "   ");
			}
			if (banderaAutocommit){
				conexionBD.rollback();
			}
			cierraConexion();
			throw b;
		}catch (SQLException e) {
			log.error("Error.UtileriasBD.ejecutaUpdateBatch: Error al ejecutar la operacion por lotes");
			log.error("Descripcion del error:" + e.getMessage());
			if (banderaAutocommit){
				conexionBD.rollback();
			}
			cierraConexion();
			throw e;
		}finally{
			sql=new StringBuilder();
		}
		return iNumRegistros;
	}
	
	/**
	 * Cierra la conexion a la base de datos. Si la conexion fue abierta por otro objeto y
	 * pasada a la clase por el metodo setConexion(), entonces solo se cerrarán los objetos statement y
	 * resulset
	 * 
	 * @author Luis Fernando Castro
	 * @since 2011/08/30
	 */
	public void cierraConexion() {
		try {
			//VERIFICA SI ESTA ABIERTO UN RESULTSET
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (st != null) {
				st.close();
				st = null;
			}
			if( parametros != null ){
				parametros.close();
				parametros = null;
			}
			if( sto != null ){
				sto.close();
				sto = null;
			}
			if( conexionBD != null){ 
				conexionBD.cierraConexion();
			}
		}catch (Exception e) {
			log.error("Error.UtileriasBD.cierraConexion: "+e.getMessage());
		}
	
	}
	
	/**
	 * Inhabilita el modo Auto-commit del atributo conexionBD
	 * 
	 * @author Luis Fernando Castro
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @since 2011/08/30 
	 */
	public void inhabilitaAutoCommit() throws SQLException {
		conexionBD.inhabilitaAutoCommit();
		banderaAutocommit = true;
	}

	/**
	 * Guarda los cambios en la BD del atributo conexionBD
	 * 
	 * @author Luis Fernando Castro
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @since 2011/08/30
	 */
	public void commit() throws SQLException {
		conexionBD.commit();
	}

	/**
	 * Restablece la transacción en la BD del atributo conexionBD
	 * 
	 * @author Luis Fernando Castro
	 * @throws SQLException Si se genera algún error al accesar la base de datos.
	 * @since 2011/08/30
	 */
	public void rollback() throws SQLException {
		conexionBD.rollback();
	}

	/**
	 * Accesor para poder utilizar la variable sql desde el DAO
	 * 
	 * @author Luis Fernando Castro
	 * @return referencia al atributo sql de la clase
	 * @since 2011/08/31
	 */
	public StringBuilder getSql() {
		return sql;
	}

	/**
	 * Accesor para asignar un valor a la variable sql desde el DAO
	 * 
	 * @author Luis Fernando Castro
	 * @param unSQL Objeto StringBuilder que contiene la sentencia sql que se quiere ejecutar
	 * @since 2011/08/31
	 */
	public void setSql(StringBuilder unSQL) {
		sql = unSQL;
	}

	/**
	 * Accesor para poder utilizar y asignar valores al atributo parametros desde el DAO
	 * 
	 * @author Luis Fernando Castro
	 * @since 2011/08/31
	 */
	public PreparedStatement getParametros() {
		return parametros;
	}

	/**
	 * Accesor para asignar un valor a la variable sql desde el DAO
	 * 
	 * @author Luis Fernando Castro
	 * @param parametros Objeto PreparedStatement para asignar un valor al atriburo parametros
	 * @since 2011/08/31
	 */
	public void setParametros(PreparedStatement parametros) {
		this.parametros = parametros;
	}
	
	/**
	 * Método para asignar una conexión al atributo conexion
	 * 
	 * @author Luis Fernando Castro
	 * @param unaConexionBD Conexion abierta a la BD
	 * @since 2011/08/30 
	 */
	public void setConexion( Connection unaConexionBD ) {
		conexionBD.setConexion(unaConexionBD);
	}
	
	/**
	 * Método para obtener una referencia al atributo conexion
	 * 
	 * @author Luis Fernando Castro
	 * @param unaConexionBD Conexion abierta a la BD
	 * @since 2011/08/30 
	 */
	public Connection getConexion() {
		return conexionBD.getConexion();
	}
	
	/**
	 * Método para obtener el dueño de los objetos
	 * de la BD de sima tienda 
	 * 
	 * @author Luis Fernando Castro
	 * @param owner cadena con el dueño de los objetos de la BD
	 * @since 2011/09/01 
	 */
	public String getEsqSimaTiendas(){
		return conexionBD.getEsqSimaTiendas();
	}	
	
}
