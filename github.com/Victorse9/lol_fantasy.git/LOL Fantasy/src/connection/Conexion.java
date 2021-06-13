package connection;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import modelo.Clasificacion;
import modelo.Jugador;
import modelo.RegistroDatos;

public class Conexion {

	/**
	 * Conecta con la bbdd
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public Connection connectionByProp() throws ClassNotFoundException, SQLException, IOException {
		Connection connection = null;
		try {
			Properties properties = new Properties();
			properties.load(new FileReader("src/connection/db.properties"));
			String driver = properties.getProperty("database.driver");
			String url = properties.getProperty("database.url");
			String user = properties.getProperty("database.user");
			String password = properties.getProperty("database.password");

			// Relizamos el registro del driver y obtenemos la conexion
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

			connection.setAutoCommit(false);
		} catch (ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			throw e;
		}
		return connection;
	}

	/**
	 * Desconecta de la bbdd
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public void disconect(Connection connection) throws SQLException {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Establece el nombre del equipo y escudo
	 * 
	 * @param nombreEquipo
	 * @param escudo
	 * @param usuario
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void creaEquipo(String nombreEquipo, String escudo, String usuario)
			throws ClassNotFoundException, IOException, SQLException {

		String sentenciaSql = "UPDATE REGISTRO SET EQUIPO_EXISTE = 1, NOMBRE_EQUIPO = ?, ESCUDO = ?, SALDO = 20000 WHERE USUARIO = ?";
		PreparedStatement pStatement = null;
		Connection connection = null;
		try {
			connection = connectionByProp();
			pStatement = connection.prepareStatement(sentenciaSql);
			pStatement.setString(1, nombreEquipo); // nombreEquipo
			pStatement.setString(2, escudo); // escudo
			pStatement.setString(3, usuario); // usuario
			try {
				pStatement.executeUpdate();
			} catch (SQLException sqle) {
				JOptionPane.showMessageDialog(null, "Error al crear equipo con la bbdd");
				throw sqle;
			}

			JOptionPane.showMessageDialog(null, "Equipo creado. AHORA A JUGAR!");
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	/**
	 * Devuelve si existe un Usuario
	 * 
	 * @param usuario
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean compruebaUsuarioExiste(String usuario, Connection connection)
			throws IOException, ClassNotFoundException, SQLException {
		boolean existe = false;

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		RegistroDatos registro = null;
		String sentenciaSqle = "SELECT * FROM REGISTRO WHERE NOMBRE= ?";
		try {
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				registro = new RegistroDatos();
				registro.setUsuario(resultSet.getString("USUARIO"));
				registro.setContraseña(resultSet.getString("CONTRASEÑA"));
				if (registro.getUsuario() != null) {
					existe = true;
				}
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		}

		return existe;
	}

	/**
	 * Crea un usuario nuevo
	 * 
	 * @param usuario
	 * @param contraseña
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void creaUsuario(String usuario, String contraseña, String contraseña2)
			throws ClassNotFoundException, IOException, SQLException {

		String sentenciaSql = "INSERT INTO registro (USUARIO, CONTRASEÑA) VALUES (?, ?)";
		PreparedStatement pStatement = null;
		Connection connection = null;
		try {
			connection = connectionByProp();
			if (compruebaUsuarioExiste(usuario, connection) == false) {
				pStatement = connection.prepareStatement(sentenciaSql);
				pStatement.setString(1, usuario); // USUARIO
				pStatement.setString(2, contraseña); // CONTRASEÑA
				try {
					pStatement.executeUpdate();
					JOptionPane.showMessageDialog(null, "Usuario creado.");
				} catch (SQLException sqle) {
					JOptionPane.showMessageDialog(null, "Este nombre de usuario ya existe");
				}
				connection.commit();
			}
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	/**
	 * Comprueba si la cuenta está registrada
	 * 
	 * @param usuario
	 * @param contraseña
	 * @param connection
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean compruebaRegistro(String usuario, String contraseña)
			throws IOException, ClassNotFoundException, SQLException {
		boolean existe = false;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		RegistroDatos registroD = null;
		String sentenciaSqle = "SELECT USUARIO, CONTRASEÑA FROM registro WHERE USUARIO = ? && CONTRASEÑA = ?";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			preparedStatement.setString(2, contraseña);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				registroD = new RegistroDatos();
				registroD.setUsuario(resultSet.getString("USUARIO"));
				registroD.setContraseña(resultSet.getString("CONTRASEÑA"));
				if (registroD.getUsuario() == null) {
					existe = false;
				} else {
					existe = true;
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return existe;
	}

	/**
	 * Comprueba si se ha creado ya el equipo
	 * 
	 * @param usuario
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean existeEquipo(String usuario) throws IOException, ClassNotFoundException, SQLException {
		boolean existe = false, equipo_existe = false;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		String sentenciaSqle = "SELECT EQUIPO_EXISTE FROM REGISTRO WHERE USUARIO = ?";

		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				equipo_existe = resultSet.getBoolean("EQUIPO_EXISTE");
				if (equipo_existe == true) {
					existe = true;
				} else {
					existe = false;
				}

			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return existe;
	}

	public void insertJugador(String usuario, String jugador) throws ClassNotFoundException, IOException, SQLException {

		String sentenciaSql = "INSERT INTO PLANTILLA (USUARIO, JUGADOR) VALUES (?, ?)";
		PreparedStatement pStatement = null;
		Connection connection = null;
		try {
			connection = connectionByProp();
			if (compruebaUsuarioExiste(usuario, connection) == false) {
				pStatement = connection.prepareStatement(sentenciaSql);
				pStatement.setString(1, usuario); // USUARIO
				pStatement.setString(2, jugador); // JUGADOR

				pStatement.executeUpdate();
				connection.commit();
			}
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	/**
	 * Devuelve el escudo y el nombre del equipo del usuario registrado
	 * 
	 * @param usuario
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public String[] getEscudoEquipo(String usuario) throws IOException, ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String[] escudoEquipo = new String[2];
		String sentenciaSqle = "SELECT NOMBRE_EQUIPO, ESCUDO FROM REGISTRO WHERE USUARIO = ?";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				escudoEquipo[0] = resultSet.getString("NOMBRE_EQUIPO");
				escudoEquipo[1] = resultSet.getString("ESCUDO");
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return escudoEquipo;
	}

	/**
	 * Devuelve los jugadores iniciales
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public ArrayList<String> JugadorIniciales() throws IOException, ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int i = 0;
		ArrayList<String> Jugador = new ArrayList<String>();
		String sentenciaSqle = "SELECT NOMBRE FROM JUGADOR WHERE NOMBRE IN ('Aesenar', 'CarritosKami', 'Hadess', 'knighter', 'SendOo');";

		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				i += 1;
				Jugador.add(resultSet.getString(i));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return Jugador;
	}

	/**
	 * Devuelve el jugador indicado
	 * 
	 * @param jugador
	 * @param connection
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Jugador getJugador(String jugador) throws IOException, ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Jugador jug = null;
		String sentenciaSqle = "SELECT NOMBRE, POSICION, CALIDAD, PRECIO FROM JUGADOR WHERE NOMBRE= ?";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, jugador);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				jug = new Jugador("", "", 0, 0);
				jug.setNombre(resultSet.getString("NOMBRE"));
				jug.setPosicion(resultSet.getString("POSICION"));
				jug.setCalidad(resultSet.getInt("CALIDAD"));
				jug.setPrecio(resultSet.getInt("PRECIO"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return jug;
	}

	/**
	 * Devuelve si el jugador pasado existe
	 * 
	 * @param jugador
	 * @param usuario
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public boolean estaEnPlantilla(String jugador, String usuario)
			throws IOException, ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		boolean existe = false;
		String sentenciaSqle = "SELECT JUGADOR FROM PLANTILLA WHERE JUGADOR = ? AND USUARIO = ?";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, jugador);
			preparedStatement.setString(2, usuario);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				existe = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return existe;
	}

	/**
	 * Borra al jugador de la plantilla
	 * 
	 * @param usuario
	 * @param jug
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void venderJugador(String usuario, Jugador jug) throws ClassNotFoundException, IOException, SQLException {
		String jugador = jug.getNombre();
		String sentenciaSql = "DELETE FROM PLANTILLA WHERE USUARIO = ? AND JUGADOR = ?";
		PreparedStatement pStatement = null;
		Connection connection = null;
		try {
			connection = connectionByProp();
			pStatement = connection.prepareStatement(sentenciaSql);
			pStatement.setString(1, usuario);
			pStatement.setString(2, jugador);
			try {
				pStatement.executeUpdate();
				JOptionPane.showMessageDialog(null, "Jugador vendido");
			} catch (SQLException sqle) {
				JOptionPane.showMessageDialog(null, "Fallo");
				throw sqle;
			}
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	/**
	 * Inserta a la plantilla el jugador pasado
	 * 
	 * @param usuario
	 * @param jug
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void comprarJugador(String usuario, Jugador jug) throws ClassNotFoundException, IOException, SQLException {
		String jugador = jug.getNombre();
		String sentenciaSql = "INSERT INTO PLANTILLA (USUARIO, JUGADOR) VALUES(?,?)";
		PreparedStatement pStatement = null;
		Connection connection = null;
		try {
			connection = connectionByProp();
			pStatement = connection.prepareStatement(sentenciaSql);
			pStatement.setString(1, usuario);
			pStatement.setString(2, jugador);
			try {
				pStatement.executeUpdate();
				JOptionPane.showMessageDialog(null, "Jugador comprado");
			} catch (SQLException sqle) {
				JOptionPane.showMessageDialog(null, "Fallo");
				throw sqle;
			}
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	/**
	 * Crea la clasificacion con los equipos a 0 partidos
	 * 
	 * @param usuario
	 * @param equipo
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void creaClasificacion(String usuario, String equipo)
			throws ClassNotFoundException, IOException, SQLException {
		String sentenciaSql = "INSERT INTO CLASIFICACION (USUARIO, EQUIPO, N_PARTIDO, PARTIDOS_GANADOS, ELIMINACIONES) VALUES(?,?, 1, 0, 0)";
		PreparedStatement pStatement = null;
		Connection connection = null;
		try {
			connection = connectionByProp();
			pStatement = connection.prepareStatement(sentenciaSql);
			pStatement.setString(1, usuario);
			pStatement.setString(2, equipo);
			try {
				pStatement.executeUpdate();
			} catch (SQLException sqle) {
				JOptionPane.showMessageDialog(null, "Fallo");
				throw sqle;
			}
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	/**
	 * Devuelve los partidos jugados
	 * 
	 * @param usuario
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public int getJornada(String usuario, String equipo) throws IOException, ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int n_partido = 0;
		String sentenciaSqle = "SELECT DISTINCT N_PARTIDO FROM CLASIFICACION WHERE USUARIO = ? AND EQUIPO = ?";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			preparedStatement.setString(2, equipo);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				n_partido = resultSet.getInt("N_PARTIDO");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return n_partido;
	}

	/**
	 * Devuelve la tabla de clasificacion ordenada
	 * 
	 * @param usuario
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public ObservableList<Clasificacion> getClasificacion(String usuario)
			throws IOException, ClassNotFoundException, SQLException {
		ObservableList<Clasificacion> lClasificacion = FXCollections.observableArrayList();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sentenciaSqle = "SELECT EQUIPO, N_PARTIDO, PARTIDOS_GANADOS, ELIMINACIONES FROM CLASIFICACION WHERE USUARIO = ? ORDER BY PARTIDOS_GANADOS DESC, ELIMINACIONES DESC";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Clasificacion clasif = new Clasificacion();
				clasif.setEquipo(resultSet.getString("EQUIPO"));
				clasif.setN_partido((resultSet.getInt("n_partido"))-1);
				clasif.setPartidos_ganados(resultSet.getInt("partidos_ganados"));
				clasif.setEliminaciones(resultSet.getInt("eliminaciones"));
				lClasificacion.add(clasif);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return lClasificacion;
	}
	
	public String getGanador(String usuario)
			throws IOException, ClassNotFoundException, SQLException {
		String ganador = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sentenciaSqle = "SELECT equipo FROM CLASIFICACION WHERE USUARIO = ? ORDER BY PARTIDOS_GANADOS DESC, ELIMINACIONES DESC limit 1";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				ganador = resultSet.getString("EQUIPO");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return ganador;
	}

	/**
	 * Devuelve la calidad del equipo pasado
	 * 
	 * @param equipo
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public int getCalidadEquipo(String equipo) throws IOException, ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int calidad = 0;
		String sentenciaSqle = "select sum(calidad) as CALIDAD from jugador where equipo = ?";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, equipo);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				calidad = resultSet.getInt("CALIDAD");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return calidad;
	}

	/**
	 * Devuelve la calidad de los jugadores alineados
	 * 
	 * @param equipo
	 * @param jugador1
	 * @param jugador2
	 * @param jugador3
	 * @param jugador4
	 * @param jugador5
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public int getCalidadAlineados(String usuario, String jugador1, String jugador2, String jugador3, String jugador4,
			String jugador5) throws IOException, ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int calidad = 0;
		String sentenciaSqle = "select sum((select calidad from jugador where nombre = p.jugador)) as CALIDAD from plantilla p where p.usuario = ? and (p.jugador = ? or p.jugador = ? or p.jugador = ? or p.jugador = ? or p.jugador = ?)";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			preparedStatement.setString(2, jugador1);
			preparedStatement.setString(3, jugador2);
			preparedStatement.setString(4, jugador3);
			preparedStatement.setString(5, jugador4);
			preparedStatement.setString(6, jugador5);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				calidad = resultSet.getInt("CALIDAD");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return calidad;
	}

	/**
	 * Actualiza la tabla clasificación
	 * 
	 * @param ganado
	 * @param eliminaciones
	 * @param usuario
	 * @param equipo
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void updateClasificacion(int ganado, int eliminaciones, String usuario, String equipo)
			throws ClassNotFoundException, IOException, SQLException {
		String sentenciaSql = "UPDATE CLASIFICACION SET N_PARTIDO = N_PARTIDO+1, PARTIDOS_GANADOS = PARTIDOS_GANADOS+?, ELIMINACIONES = ELIMINACIONES+? WHERE USUARIO = ? AND EQUIPO = ?";
		PreparedStatement pStatement = null;
		Connection connection = null;
		try {
			connection = connectionByProp();
			pStatement = connection.prepareStatement(sentenciaSql);
			pStatement.setInt(1, ganado);
			pStatement.setInt(2, eliminaciones);
			pStatement.setString(3, usuario);
			pStatement.setString(4, equipo);
			try {
				pStatement.executeUpdate();
			} catch (SQLException sqle) {
				throw sqle;
			}
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	/**
	 * Elimina todos los datos de la liga del usuario pasado
	 * 
	 * @param usuario
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void eliminarLiga(String usuario) throws ClassNotFoundException, IOException, SQLException {
		String sentenciaSql1 = "DELETE FROM PLANTILLA WHERE USUARIO = ?";
		String sentenciaSql2 = "delete from clasificacion WHERE USUARIO = ?";
		String sentenciaSql3 = "update registro set equipo_existe = null, nombre_equipo = null, escudo = null, saldo = null where usuario = ?";
		PreparedStatement pStatement1 = null;
		PreparedStatement pStatement2 = null;
		PreparedStatement pStatement3 = null;
		Connection connection = null;
		try {
			connection = connectionByProp();
			// DELETE PLANTILLA
			pStatement1 = connection.prepareStatement(sentenciaSql1);
			pStatement1.setString(1, usuario);
			// DELETE CLASIFICACION
			pStatement2 = connection.prepareStatement(sentenciaSql2);
			pStatement2.setString(1, usuario);
			// DELETE REGISTRO
			pStatement3 = connection.prepareStatement(sentenciaSql3);
			pStatement3.setString(1, usuario);
			try {
				pStatement1.executeUpdate();
				pStatement2.executeUpdate();
				pStatement3.executeUpdate();
			} catch (SQLException sqle) {
				JOptionPane.showMessageDialog(null, "Fallo");
				throw sqle;
			}
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (pStatement1 != null)
				try {
					pStatement1.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			if (pStatement2 != null)
				try {
					pStatement2.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			if (pStatement3 != null)
				try {
					pStatement3.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}

	/**
	 * Devuelve el saldo del usuario
	 * 
	 * @param usuario
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public int getSaldo(String usuario) throws IOException, ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int saldo = 0;
		String sentenciaSqle = "SELECT SALDO FROM REGISTRO WHERE USUARIO = ?";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				saldo = resultSet.getInt("SALDO");
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
			} catch (SQLException e) {
				throw e;
			}
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				throw e;
			}
		}

		return saldo;
	}

	/**
	 * Actualiza el saldo al usuario por la cantidad pasada
	 * 
	 * @param usuario
	 * @param cambioSaldo
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void actualizaSaldo(String usuario, int cambioSaldo)
			throws ClassNotFoundException, IOException, SQLException {
		String sentenciaSql = "update registro set saldo = saldo+? where usuario = ?";
		PreparedStatement pStatement = null;
		Connection connection = null;
		try {
			connection = connectionByProp();
			pStatement = connection.prepareStatement(sentenciaSql);
			pStatement.setInt(1, cambioSaldo);
			pStatement.setString(2, usuario);
			try {
				pStatement.executeUpdate();
			} catch (SQLException sqle) {
				throw sqle;
			}
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (pStatement != null)
				try {
					pStatement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw e;
			}
		}
	}
}
