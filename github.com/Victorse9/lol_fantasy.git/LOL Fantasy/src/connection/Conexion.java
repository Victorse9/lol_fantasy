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

	public void creaEquipo(String nombreEquipo, String escudo, String usuario)
			throws ClassNotFoundException, IOException, SQLException {

		String sentenciaSql = "UPDATE REGISTRO SET EQUIPO_EXISTE = 1, NOMBRE_EQUIPO = ?, ESCUDO = ? WHERE USUARIO = ?";
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
				} catch (SQLException sqle) {
					JOptionPane.showMessageDialog(null, "Este nombre de usuario ya existe");
					throw sqle;
				}

				JOptionPane.showMessageDialog(null, "Usuario creado.");
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
		String[] escudoEquipo= new String[2];
		String sentenciaSqle = "SELECT NOMBRE_EQUIPO, ESCUDO FROM REGISTRO WHERE USUARIO = ?";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, usuario);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				escudoEquipo [0]= resultSet.getString("NOMBRE_EQUIPO");
				escudoEquipo [1]= resultSet.getString("ESCUDO");
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
	 * @param jugador
	 * @param connection
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Jugador getJugador(String jugador)
			throws IOException, ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Jugador jug = null;
		String[] datos= new String[4];
		String sentenciaSqle = "SELECT NOMBRE, POSICION, CALIDAD, PRECIO FROM JUGADOR WHERE NOMBRE= ?";
		try {
			connection = connectionByProp();
			preparedStatement = connection.prepareStatement(sentenciaSqle);
			preparedStatement.setString(1, jugador);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
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

}
