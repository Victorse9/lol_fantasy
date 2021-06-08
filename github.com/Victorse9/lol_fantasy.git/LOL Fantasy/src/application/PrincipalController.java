package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import connection.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import modelo.Jugador;

public class PrincipalController {
	private String nombreUsuario, nombreEquipo, escudo;
	@FXML
	private Label lblUsuario, lblEquipo, lblTop, lblJungla, lblMid, lblSupport, lblAdc;
	@FXML
	private ImageView imgEscudo;
	@FXML
	private Pane pMercado, pClasificacion, pJugar;
	private Conexion conexion = new Conexion();
	private String[] escudoEquipo;
	@FXML
	private TableView<Jugador> tablaMercado = new TableView<Jugador>();
	@FXML
	private TableColumn cNombre, cPosicion, cCalidad, cPrecio, c3Nombre, c3Posicion, c3Calidad, c3Precio, c2Nombre,
			c2Posicion, c2Calidad, c2Precio;
	@FXML
	private TableView<Jugador> tablaPlantilla = new TableView<Jugador>();
	@FXML
	private TableView<Jugador> tablaPlantillaJugar = new TableView<Jugador>();
	@FXML
	private ObservableList<Jugador> lJugadoresMercado;
	@FXML
	private ObservableList<Jugador> lJugadoresPlantilla;
	private Jugador jug;
	@FXML
	private Button btnMercado, btnJugar, btnClasificacion, btnVender, btnComprar;

	/**
	 * Constructor
	 * 
	 * @param nombreUsuario
	 */
	public PrincipalController(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public void initialize() throws Exception {
	
		try {
			escudoEquipo = conexion.getEscudoEquipo(nombreUsuario);
			// Guarda escudo y nombre equipo
			nombreEquipo = escudoEquipo[0];
			escudo = escudoEquipo[1];
			lblUsuario.setText(nombreUsuario);
			lblEquipo.setText(nombreEquipo);
			imgEscudo.setImage(new Image("/imagenes_escudos/" + escudo + ".png"));
		} catch (ClassNotFoundException | IOException | SQLException e) {
			throw e;
		}
	
		cargaTablas();
	}

	@FXML
	public void vender(ActionEvent e) throws Exception {
		Jugador jVendido = tablaPlantilla.getSelectionModel().getSelectedItem();
		if (jVendido == null) {
			JOptionPane.showMessageDialog(null, "Selecciona un jugador a vender.");
		} else {
			try {
				conexion.venderJugador(nombreUsuario, jVendido);
				cargaTablas();
			} catch (ClassNotFoundException | IOException | SQLException e1) {
				throw e1;
			}
		}
	}

	@FXML
	public void comprar(ActionEvent e) throws Exception {
		Jugador jComprado = tablaMercado.getSelectionModel().getSelectedItem();
		if (jComprado == null) {
			JOptionPane.showMessageDialog(null, "Selecciona un jugador a comprar.");
		} else {
			try {
				conexion.comprarJugador(nombreUsuario, jComprado);
				cargaTablas();
			} catch (ClassNotFoundException | IOException | SQLException e1) {
				throw e1;
			}
		}
	}
	
	public void alinear() {
		Jugador jSel = tablaPlantillaJugar.getSelectionModel().getSelectedItem();
		if(jSel == null) {
			JOptionPane.showMessageDialog(null, "Selecciona un jugador para alinearlo.");
		}else {
			switch(jSel.getPosicion()) {
			
			case "TOP":
				lblTop.setText(jSel.getNombre());
				break;
			case "JUNGLE":
				lblJungla.setText(jSel.getNombre());
				break;
			case "MID":
				lblMid.setText(jSel.getNombre());
				break;
			case "SUPPORT":
				lblSupport.setText(jSel.getNombre());
				break;
			case "ADC":
				lblAdc.setText(jSel.getNombre());
				break;
			}
			
		}
		System.out.println();
	}

	@FXML
	public void volver(MouseEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		try {
			Stage primaryStage = new Stage();
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("Login.fxml"));
			Scene scene = new Scene(root, 1280, 690);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.getIcons().add(new Image("/imagenes_varias/icono.JPG"));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void cambiaPanel(ActionEvent e) throws ClassNotFoundException, IOException, SQLException {
		Button botonMenu = (Button) ((Node) e.getSource());
		String eleccion = botonMenu.getText();

		switch (eleccion) {
		case "JUGAR":
			pJugar.setVisible(true);
			pClasificacion.setVisible(false);
			pMercado.setVisible(false);
			
			lblTop.setText("TOP");
			lblJungla.setText("JUNGLA");
			lblMid.setText("MID");
			lblSupport.setText("SUPPORT");
			lblAdc.setText("ADC");
			
			cargaTablaPlantilla();
			break;
		case "MERCADO":
			pJugar.setVisible(false);
			pClasificacion.setVisible(false);
			pMercado.setVisible(true);
			break;
		case "CLASIFICACIÓN":
			pJugar.setVisible(false);
			pClasificacion.setVisible(true);
			pMercado.setVisible(false);
			break;
		}
	}

	public void cargaTablas() throws ClassNotFoundException, IOException, SQLException {
		// Añadir jugadores a la tabla
		lJugadoresMercado = FXCollections.observableArrayList();
		lJugadoresPlantilla = FXCollections.observableArrayList();
		// Establece los nombres de las columnas de la bbdd para enlazarlos a la tabla
		// Mercado
		this.cNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
		this.cPosicion.setCellValueFactory(new PropertyValueFactory("posicion"));
		this.cCalidad.setCellValueFactory(new PropertyValueFactory("calidad"));
		this.cPrecio.setCellValueFactory(new PropertyValueFactory("precio"));
		// Plantilla
		this.c2Nombre.setCellValueFactory(new PropertyValueFactory("nombre"));
		this.c2Posicion.setCellValueFactory(new PropertyValueFactory("posicion"));
		this.c2Calidad.setCellValueFactory(new PropertyValueFactory("calidad"));
		this.c2Precio.setCellValueFactory(new PropertyValueFactory("precio"));

		// Añada a la tabla plantilla o mercado según le corresponda
		if (conexion.estaEnPlantilla("Aesenar", nombreUsuario) == false) {
			jug = conexion.getJugador("Aesenar");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Aesenar");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Alexx", nombreUsuario) == false) {
			jug = conexion.getJugador("Alexx");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Alexx");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Attila", nombreUsuario) == false) {
			jug = conexion.getJugador("Attila");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Attila");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Baca", nombreUsuario) == false) {
			jug = conexion.getJugador("Baca");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Baca");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("CarritosKami", nombreUsuario) == false) {
			jug = conexion.getJugador("CarritosKami");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("CarritosKami");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Dreedy", nombreUsuario) == false) {
			jug = conexion.getJugador("Dreedy");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Dreedy");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Efias", nombreUsuario) == false) {
			jug = conexion.getJugador("Efias");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Efias");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Flakked", nombreUsuario) == false) {
			jug = conexion.getJugador("Flakked");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Flakked");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Fresskowy", nombreUsuario) == false) {
			jug = conexion.getJugador("Fresskowy");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Fresskowy");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Hadess", nombreUsuario) == false) {
			jug = conexion.getJugador("Hadess");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Hadess");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("kamilius", nombreUsuario) == false) {
			jug = conexion.getJugador("kamilius");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("kamilius");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("knighter", nombreUsuario) == false) {
			jug = conexion.getJugador("knighter");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("knighter");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Koldo", nombreUsuario) == false) {
			jug = conexion.getJugador("Koldo");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Koldo");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Lebron", nombreUsuario) == false) {
			jug = conexion.getJugador("Lebron");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Lebron");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Miniduke", nombreUsuario) == false) {
			jug = conexion.getJugador("Miniduke");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Miniduke");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Namex", nombreUsuario) == false) {
			jug = conexion.getJugador("Namex");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Namex");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Nji", nombreUsuario) == false) {
			jug = conexion.getJugador("Nji");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Nji");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Oscarinin", nombreUsuario) == false) {
			jug = conexion.getJugador("Oscarinin");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Oscarinin");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("whiteinn", nombreUsuario) == false) {
			jug = conexion.getJugador("whiteinn");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("whiteinn");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Plasma", nombreUsuario) == false) {
			jug = conexion.getJugador("Plasma");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Plasma");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Rafitta", nombreUsuario) == false) {
			jug = conexion.getJugador("Rafitta");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Rafitta");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Ronaldo", nombreUsuario) == false) {
			jug = conexion.getJugador("Ronaldo");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Ronaldo");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("RubiOo", nombreUsuario) == false) {
			jug = conexion.getJugador("RubiOo");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("RubiOo");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Sacre", nombreUsuario) == false) {
			jug = conexion.getJugador("Sacre");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Sacre");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("SendOo", nombreUsuario) == false) {
			jug = conexion.getJugador("SendOo");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("SendOo");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Skain", nombreUsuario) == false) {
			jug = conexion.getJugador("Skain");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Skain");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Supa", nombreUsuario) == false) {
			jug = conexion.getJugador("Supa");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Supa");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Th3Antonio", nombreUsuario) == false) {
			jug = conexion.getJugador("Th3Antonio");
			lJugadoresMercado.add(jug);
		} else {
			jug = conexion.getJugador("Th3Antonio");
			lJugadoresPlantilla.add(jug);
		}

		tablaMercado.setItems(lJugadoresMercado);
		tablaPlantilla.setItems(lJugadoresPlantilla);

	}

	public void cargaTablaPlantilla() throws ClassNotFoundException, IOException, SQLException {
		// Añadir jugadores a la tabla
		lJugadoresPlantilla = FXCollections.observableArrayList();
		// Establece los nombres de las columnas de la bbdd para enlazarlos a la tabla
		// Mercado
		this.c3Nombre.setCellValueFactory(new PropertyValueFactory("nombre"));
		this.c3Posicion.setCellValueFactory(new PropertyValueFactory("posicion"));
		this.c3Calidad.setCellValueFactory(new PropertyValueFactory("calidad"));
		this.c3Precio.setCellValueFactory(new PropertyValueFactory("precio"));
		;

		// Añada a la tabla plantilla o mercado según le corresponda
		if (conexion.estaEnPlantilla("Aesenar", nombreUsuario) == true) {
			jug = conexion.getJugador("Aesenar");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Alexx", nombreUsuario) == true) {
			jug = conexion.getJugador("Alexx");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Attila", nombreUsuario) == true) {
			jug = conexion.getJugador("Attila");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Baca", nombreUsuario) == true) {
			jug = conexion.getJugador("Baca");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("CarritosKami", nombreUsuario) == true) {
			jug = conexion.getJugador("CarritosKami");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Dreedy", nombreUsuario) == true) {
			jug = conexion.getJugador("Dreedy");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Efias", nombreUsuario) == true) {
			jug = conexion.getJugador("Efias");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Flakked", nombreUsuario) == true) {
			jug = conexion.getJugador("Flakked");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Fresskowy", nombreUsuario) == true) {
			jug = conexion.getJugador("Fresskowy");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Hadess", nombreUsuario) == true) {
			jug = conexion.getJugador("Hadess");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("kamilius", nombreUsuario) == true) {
			jug = conexion.getJugador("kamilius");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("knighter", nombreUsuario) == true) {
			jug = conexion.getJugador("knighter");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Koldo", nombreUsuario) == true) {
			jug = conexion.getJugador("Koldo");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Lebron", nombreUsuario) == true) {
			jug = conexion.getJugador("Lebron");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Miniduke", nombreUsuario) == true) {
			jug = conexion.getJugador("Miniduke");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Namex", nombreUsuario) == true) {
			jug = conexion.getJugador("Namex");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Nji", nombreUsuario) == true) {
			jug = conexion.getJugador("Nji");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Oscarinin", nombreUsuario) == true) {
			jug = conexion.getJugador("Oscarinin");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("whiteinn", nombreUsuario) == true) {
			jug = conexion.getJugador("whiteinn");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Plasma", nombreUsuario) == true) {
			jug = conexion.getJugador("Plasma");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Rafitta", nombreUsuario) == true) {
			jug = conexion.getJugador("Rafitta");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Ronaldo", nombreUsuario) == true) {
			jug = conexion.getJugador("Ronaldo");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("RubiOo", nombreUsuario) == true) {
			jug = conexion.getJugador("RubiOo");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Sacre", nombreUsuario) == true) {
			jug = conexion.getJugador("Sacre");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("SendOo", nombreUsuario) == true) {
			jug = conexion.getJugador("SendOo");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Skain", nombreUsuario) == true) {
			jug = conexion.getJugador("Skain");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Supa", nombreUsuario) == true) {
			jug = conexion.getJugador("Supa");
			lJugadoresPlantilla.add(jug);
		}
		if (conexion.estaEnPlantilla("Th3Antonio", nombreUsuario) == true) {
			jug = conexion.getJugador("Th3Antonio");
			lJugadoresPlantilla.add(jug);
		}

		tablaPlantillaJugar.setItems(lJugadoresPlantilla);

	}

	@FXML
	public void mouseEnteredMercado(MouseEvent e) {
		btnMercado.setStyle("-fx-background-color: #1E1F39");
	}

	@FXML
	public void mouseExitedMercado(MouseEvent e) {
		btnMercado.setStyle("-fx-background-color: #0F102C");
	}

	@FXML
	public void mouseEnteredJugar(MouseEvent e) {
		btnJugar.setStyle("-fx-background-color: #1E1F39");
	}

	@FXML
	public void mouseExitedJugar(MouseEvent e) {
		btnJugar.setStyle("-fx-background-color: #0F102C");
	}

	@FXML
	public void mouseEnteredClasificacion(MouseEvent e) {
		btnClasificacion.setStyle("-fx-background-color: #1E1F39");
	}

	@FXML
	public void mouseExitedClasificacion(MouseEvent e) {
		btnClasificacion.setStyle("-fx-background-color: #0F102C");
	}
}