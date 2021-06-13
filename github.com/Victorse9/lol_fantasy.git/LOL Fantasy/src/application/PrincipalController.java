package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.sun.prism.paint.Color;

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
import javafx.stage.StageStyle;
import javafx.util.Callback;
import modelo.Clasificacion;
import modelo.Jugador;

public class PrincipalController {
	private String nombreUsuario, nombreEquipo, escudo, escudoRival;
	private boolean victoria;
	private int killsJug = 0, killsRival = 0;

	@FXML
	private Label lblUsuario, lblEquipo, lblTop, lblJungla, lblMid, lblSupport, lblAdc, lblJornada, lblMedia, lblOro, lblEliminarLiga;
	@FXML
	private ImageView imgEscudo, imgEquipo1, imgEquipo2, imgEquipoMapa, imgEstrellas, imgFoto, imgEstrellasJug,
			imgClasifEscudo, imgClasifEstrellas;
	@FXML
	private Pane pMercado, pClasificacion, pJugar;
	private Conexion conexion = new Conexion();
	private String[] escudoEquipo;
	@FXML
	private TableView<Jugador> tablaMercado = new TableView<Jugador>();
	@FXML
	private TableColumn cNombre, cPosicion, cCalidad, cPrecio, c3Nombre, c3Posicion, c3Calidad, c3Precio, c2Nombre,
			clasifEquipo, clasifGanados, clasifEliminaciones, clasifJugados, c2Posicion, c2Calidad, c2Precio;
	@FXML
	private TableView<Jugador> tablaPlantilla = new TableView<Jugador>();
	@FXML
	private TableView<Jugador> tablaPlantillaJugar = new TableView<Jugador>();
	@FXML
	private TableView<Clasificacion> tablaClasificacion = new TableView<Clasificacion>();
	@FXML
	private ObservableList<Jugador> lJugadoresMercado;
	@FXML
	private ObservableList<Jugador> lJugadoresPlantilla;
	@FXML
	private ObservableList<Clasificacion> lClasificacion;
	private Jugador jug;
	@FXML
	private Button btnMercado, btnJugar, btnClasificacion, btnVender, btnComprar, btnSimular, btnAlinear;

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
			lblOro.setText(String.valueOf(conexion.getSaldo(nombreUsuario)));
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
		cargaEscudosJornada();
		cargaTablaClasificacion();
	}

	/**
	 * Simula el partido del jugador contra la máquina
	 * 
	 * @param rival
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void jugarPartido(String rival) throws ClassNotFoundException, IOException, SQLException {
		int resJug = 0, resRival = 0;

		int calidadJugador = conexion.getCalidadAlineados(nombreUsuario, lblTop.getText(), lblJungla.getText(),
				lblMid.getText(), lblAdc.getText(), lblSupport.getText());
		int calidadRival = conexion.getCalidadEquipo(rival);

		// Calcula la media de la calidad del equipo
		calidadJugador = calidadJugador / 5;
		calidadRival = calidadRival / 5;

		// Saca un resultado que no puedan ser iguales
		while (resJug == resRival) {
			resJug = (int) (1 + Math.random() * (12 + calidadJugador));
			resRival = (int) (1 + Math.random() * (10 + calidadRival));
		}
		// GANAS
		if (resJug > resRival) {
			killsJug = (int) (10 + Math.random() * 20);
			killsRival = (int) (5 + Math.random() * 20);
			// Update Jugador
			conexion.updateClasificacion(1, killsJug, nombreUsuario, nombreEquipo);
			// Update Rival
			conexion.updateClasificacion(0, killsRival, nombreUsuario, rival);
			victoria = true;
			conexion.actualizaSaldo(nombreUsuario, 8000);
			// PIERDES
		} else {
			killsRival = (int) (10 + Math.random() * 20);
			killsJug = (int) (5 + Math.random() * 20);
			// Update Jugador
			conexion.updateClasificacion(0, killsJug, nombreUsuario, nombreEquipo);
			// Update Rival
			conexion.updateClasificacion(1, killsRival, nombreUsuario, rival);
			victoria = false;
			conexion.actualizaSaldo(nombreUsuario, 3000);
		}

	}

	public void jugarPartidoOtros(String equipo1, String equipo2)
			throws ClassNotFoundException, IOException, SQLException {
		int resE1 = 0, resE2 = 0, killsE1 = 0, killsE2 = 0;

		int calidadEquipo1 = conexion.getCalidadEquipo(equipo1);
		int calidadEquipo2 = conexion.getCalidadEquipo(equipo2);

		// Calcula la media de la calidad del equipo
		calidadEquipo1 = calidadEquipo1 / 5;
		calidadEquipo2 = calidadEquipo2 / 5;

		// Saca un resultado que no puedan ser iguales
		while (resE1 == resE2) {
			resE1 = (int) (1 + Math.random() * (10 + calidadEquipo1));
			resE2 = (int) (1 + Math.random() * (10 + calidadEquipo2));
		}
		// GANA EQUIPO 1
		if (resE1 > resE2) {
			// Saca las eliminaciones del partido
			killsE1 = (int) (10 + Math.random() * 20);
			killsE2 = (int) (5 + Math.random() * 20);
			// Update Jugador
			conexion.updateClasificacion(1, killsE1, nombreUsuario, equipo1);
			// Update Rival
			conexion.updateClasificacion(0, killsE2, nombreUsuario, equipo2);
			// GANA EQUIPO 2
		} else {
			// Saca las eliminaciones del partido
			killsE2 = (int) (10 + Math.random() * 20);
			killsE1 = (int) (5 + Math.random() * 20);
			// Update Jugador
			conexion.updateClasificacion(0, killsE1, nombreUsuario, equipo1);
			// Update Rival
			conexion.updateClasificacion(1, killsE2, nombreUsuario, equipo2);
		}
	}

	@FXML
	public void simular(ActionEvent e) throws Exception {

		if (lblTop.getText().equals("TOP") || lblJungla.getText().equals("JUNGLA") || lblMid.getText().equals("MID")
				|| lblAdc.getText().equals("ADC") || lblSupport.getText().equals("SUPPORT")) {
			JOptionPane.showMessageDialog(null, "Debes alinear 5 jugadores para poder jugar.");
		} else {
			int jornada = conexion.getJornada(nombreUsuario, nombreEquipo);

			switch (jornada) {
			case 1:
				jugarPartido("SK Gaming");
				escudoRival = "sk";
				// Resto partidos
				jugarPartidoOtros("G2 Esports", "Fnatic");
				jugarPartidoOtros("Misfits", "Rogue");
				jugarPartidoOtros("Astralis", "MAD Lions");
				jugarPartidoOtros("Vitality", "Schalke04");
				break;
			case 2:
				jugarPartido("Schalke04");
				escudoRival = "s04";
				// Resto partidos
				jugarPartidoOtros("Fnatic", "MAD Lions");
				jugarPartidoOtros("Vitality", "Misfits");
				jugarPartidoOtros("Astralis", "G2 Esports");
				jugarPartidoOtros("SK Gaming", "Rogue");
				break;
			case 3:
				jugarPartido("Vitality");
				escudoRival = "vitality";
				// Resto partidos
				jugarPartidoOtros("Rogue", "Fnatic");
				jugarPartidoOtros("Astralis", "SK Gaming");
				jugarPartidoOtros("G2 Esports", "Schalke04");
				jugarPartidoOtros("Misfits", "MAD Lions");
				break;
			case 4:
				jugarPartido("Astralis");
				escudoRival = "astralis";
				// Resto partidos
				jugarPartidoOtros("Fnatic", "SK Gaming");
				jugarPartidoOtros("G2 Esports", "Rogue");
				jugarPartidoOtros("Misfits", "Schalke04");
				jugarPartidoOtros("Vitality", "MAD Lions");
				break;
			case 5:
				jugarPartido("Misfits");
				escudoRival = "misfits";
				// Resto partidos
				jugarPartidoOtros("Fnatic", "Astralis");
				jugarPartidoOtros("Rogue", "Schalke04");
				jugarPartidoOtros("Vitality", "SK Gaming");
				jugarPartidoOtros("G2 Esports", "MAD Lions");
				break;
			case 6:
				jugarPartido("Rogue");
				escudoRival = "rogue";
				// Resto partidos
				jugarPartidoOtros("Vitality", "Fnatic");
				jugarPartidoOtros("MAD Lions", "Schalke04");
				jugarPartidoOtros("Astralis", "Misfits");
				jugarPartidoOtros("SK Gaming", "G2 Esports");
				break;
			case 7:
				jugarPartido("Fnatic");
				escudoRival = "fnatic";
				// Resto partidos
				jugarPartidoOtros("Misfits", "G2 Esports");
				jugarPartidoOtros("SK Gaming", "MAD Lions");
				jugarPartidoOtros("Vitality", "Rogue");
				jugarPartidoOtros("Astralis", "Schalke04");
				break;
			case 8:
				jugarPartido("G2 Esports");
				escudoRival = "g2";
				// Resto partidos
				jugarPartidoOtros("Schalke04", "Fnatic");
				jugarPartidoOtros("Vitality", "Astralis");
				jugarPartidoOtros("Rogue", "MAD Lions");
				jugarPartidoOtros("Misfits", "SK Gaming");
				break;
			case 9:
				jugarPartido("MAD Lions");
				escudoRival = "mad";
				// Resto partidos
				jugarPartidoOtros("Misfits", "Fnatic");
				jugarPartidoOtros("Vitality", "G2 Esports");
				jugarPartidoOtros("SK Gaming", "Schalke04");
				jugarPartidoOtros("Astralis", "Rogue");

				// Deshabilitamos boton NO HAY MAS PARTIDOS
				lblJornada.setText("LIGA FINALIZADA");
				btnSimular.setDisable(true);
				break;
			}
			cargaEscudosJornada();

			// Ventana Resumen
			((Node) e.getSource()).getScene().getWindow().hide();
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ResumenPartido.fxml"));
				ResumenPartidoController resumenController = new ResumenPartidoController(nombreUsuario, escudo,
						escudoRival, victoria, killsJug, killsRival, jornada);
				loader.setController(resumenController);
				Stage stage = new Stage();
				AnchorPane root = (AnchorPane) loader.load();
				Scene scene = new Scene(root, 800, 500);
				stage.setScene(scene);
				stage.setResizable(false);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.getIcons().add(new Image("/imagenes_varias/icono.JPG"));
				stage.show();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	@FXML
	public void cargaEscudosJornada() throws ClassNotFoundException, IOException, SQLException {
		imgEquipo1.setImage(new Image("/imagenes_escudos/" + escudo + ".png"));
		int jornada = conexion.getJornada(nombreUsuario, nombreEquipo);
		switch (jornada) {
		case 1:
			imgEquipo2.setImage(new Image("/imagenes_rival/sk.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/sk.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/3.5estrellas.png"));
			lblJornada.setText("JORNADA " + jornada);
			break;
		case 2:
			imgEquipo2.setImage(new Image("/imagenes_rival/s04.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/s04.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/2.5estrellas.png"));
			lblJornada.setText("JORNADA " + jornada);
			break;
		case 3:
			imgEquipo2.setImage(new Image("/imagenes_rival/vitality.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/vitality.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/2.5estrellas.png"));
			lblJornada.setText("JORNADA " + jornada);
			break;
		case 4:
			imgEquipo2.setImage(new Image("/imagenes_rival/astralis.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/astralis.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/3estrellas.png"));
			lblJornada.setText("JORNADA " + jornada);
			break;
		case 5:
			imgEquipo2.setImage(new Image("/imagenes_rival/misfits.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/misfits.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/2.5estrellas.png"));
			lblJornada.setText("JORNADA " + jornada);
			break;
		case 6:
			imgEquipo2.setImage(new Image("/imagenes_rival/rogue.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/rogue.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/4estrellas.png"));
			lblJornada.setText("JORNADA " + jornada);
			break;
		case 7:
			imgEquipo2.setImage(new Image("/imagenes_rival/fnatic.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/fnatic.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/3.5estrellas.png"));
			lblJornada.setText("JORNADA " + jornada);
			break;
		case 8:
			imgEquipo2.setImage(new Image("/imagenes_rival/g2.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/g2.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/4.5estrellas.png"));
			lblJornada.setText("JORNADA " + jornada);
			break;
		case 9:
			imgEquipo2.setImage(new Image("/imagenes_rival/mad.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/mad.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/4.5estrellas.png"));
			lblJornada.setText("JORNADA " + jornada);
			break;
		case 10:
			lblJornada.setText("LIGA FINALIZADA");
			btnSimular.setDisable(true);
			imgEquipo2.setImage(new Image("/imagenes_rival/mad.png"));
			imgEquipoMapa.setImage(new Image("/imagenes_rival/mad.png"));
			imgEstrellas.setImage(new Image("/imagenes_varias/4.5estrellas.png"));
			lblJornada.setTextFill(javafx.scene.paint.Color.RED);
			;
			break;
		}
	}

	@FXML
	public void vender(ActionEvent e) throws Exception {
		Jugador jVendido = tablaPlantilla.getSelectionModel().getSelectedItem();
		if (jVendido == null) {
			JOptionPane.showMessageDialog(null, "Selecciona un jugador a vender.");
		} else {
			try {
				conexion.venderJugador(nombreUsuario, jVendido);
				conexion.actualizaSaldo(nombreUsuario, jVendido.getPrecio());
				cargaTablas();
			} catch (ClassNotFoundException | IOException | SQLException e1) {
				throw e1;
			}
		}
		lblOro.setText(String.valueOf(conexion.getSaldo(nombreUsuario)));
	}

	@FXML
	public void comprar(ActionEvent e) throws Exception {
		Jugador jComprado = tablaMercado.getSelectionModel().getSelectedItem();
		if (jComprado == null) {
			JOptionPane.showMessageDialog(null, "Selecciona un jugador a comprar.");
		} else {
			if (conexion.getSaldo(nombreUsuario) < jComprado.getPrecio()) {
				JOptionPane.showMessageDialog(null, "Tu saldo es insuficiente para comprar ese jugador");
			} else {
				try {
					conexion.comprarJugador(nombreUsuario, jComprado);
					conexion.actualizaSaldo(nombreUsuario, -jComprado.getPrecio());
					cargaTablas();
					lblOro.setText(String.valueOf(conexion.getSaldo(nombreUsuario)));
				} catch (ClassNotFoundException | IOException | SQLException e1) {
					throw e1;
				}
			}

		}

	}

	public void alinear() throws ClassNotFoundException, IOException, SQLException {
		Jugador jSel = tablaPlantillaJugar.getSelectionModel().getSelectedItem();
		if (jSel == null) {
			JOptionPane.showMessageDialog(null, "Selecciona un jugador para alinearlo.");
		} else {
			switch (jSel.getPosicion()) {

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
		muestraCalidadJugador();
	}

	public void muestraCalidadJugador() throws ClassNotFoundException, IOException, SQLException {
		double calidad = 0;
		double calidadTot = 0;
		calidad = (conexion.getCalidadAlineados(nombreUsuario, lblTop.getText(), lblJungla.getText(), lblMid.getText(),
				lblAdc.getText(), lblSupport.getText()));
		calidadTot = calidad / 5;
		lblMedia.setText(String.valueOf(calidadTot));
		// Cambia la imagen de las estrellas
		// 1 ESTRELLA
		if (calidadTot <= 1.4) {
			imgEstrellasJug.setImage(new Image("/imagenes_varias/1estrellas.png"));
			// 1.5 ESTRELLAS
		} else if (calidadTot > 1.4 && calidadTot < 2) {
			imgEstrellasJug.setImage(new Image("/imagenes_varias/1.5estrellas.png"));
			// 2 ESTRELLAS
		} else if (calidadTot >= 2 && calidadTot < 2.5) {
			imgEstrellasJug.setImage(new Image("/imagenes_varias/2estrellas.png"));
			// 2.5 ESTRELLAS
		} else if (calidadTot >= 2.5 && calidadTot < 3) {
			imgEstrellasJug.setImage(new Image("/imagenes_varias/2.5estrellas.png"));
			// 3 ESTRELLAS
		} else if (calidadTot >= 3 && calidadTot < 3.5) {
			imgEstrellasJug.setImage(new Image("/imagenes_varias/3estrellas.png"));
		}
		// 3.5 ESTRELLAS
		else if (calidadTot >= 3.5 && calidadTot < 4) {
			imgEstrellasJug.setImage(new Image("/imagenes_varias/3.5estrellas.png"));
		}
		// 4 ESTRELLAS
		else if (calidadTot >= 4 && calidadTot < 4.5) {
			imgEstrellasJug.setImage(new Image("/imagenes_varias/4estrellas.png"));
		}
		// 4.5 ESTRELLAS
		else if (calidadTot >= 4.5 && calidadTot < 5) {
			imgEstrellasJug.setImage(new Image("/imagenes_varias/4.5estrellas.png"));
		}
		// 5 ESTRELLAS
		else if (calidadTot == 5) {
			imgEstrellasJug.setImage(new Image("/imagenes_varias/5estrellas.png"));
		}
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
	public void eliminaLiga(MouseEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EliminaLiga.fxml"));
			EliminaLigaController eliminaLigaController = new EliminaLigaController(nombreUsuario);
			loader.setController(eliminaLigaController);
			Stage stage = new Stage();
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root, 800, 500);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.getIcons().add(new Image("/imagenes_varias/icono.JPG"));
			stage.show();
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
			cargaTablaClasificacion();
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

	public void cargaTablaClasificacion() throws ClassNotFoundException, IOException, SQLException {
		lClasificacion = FXCollections.observableArrayList();
		// Establece los nombres de las columnas de la bbdd para enlazarlos a la tabla
		// Mercado
		this.clasifEquipo.setCellValueFactory(new PropertyValueFactory("equipo"));
		this.clasifJugados.setCellValueFactory(new PropertyValueFactory("n_partido"));
		this.clasifGanados.setCellValueFactory(new PropertyValueFactory("partidos_ganados"));
		this.clasifEliminaciones.setCellValueFactory(new PropertyValueFactory("eliminaciones"));

		lClasificacion = conexion.getClasificacion(nombreUsuario);
		// Guardamos la lista clasificacion
		tablaClasificacion.setItems(lClasificacion);
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
	public void seleccionClasif(MouseEvent e) throws ClassNotFoundException, IOException, SQLException {
		double calidad = 0;
		double calidadTot = 0;
		String equipo = tablaClasificacion.getSelectionModel().getSelectedItem().getEquipo();
		switch (equipo) {
		case "SK Gaming":
			imgClasifEscudo.setImage(new Image("/imagenes_rival/sk.png"));
			imgClasifEstrellas.setImage(new Image("/imagenes_varias/3.5estrellas.png"));
			break;
		case "Schalke04":
			imgClasifEscudo.setImage(new Image("/imagenes_rival/s04.png"));
			;
			imgClasifEstrellas.setImage(new Image("/imagenes_varias/2.5estrellas.png"));
			break;
		case "Vitality":
			imgClasifEscudo.setImage(new Image("/imagenes_rival/vitality.png"));
			imgClasifEstrellas.setImage(new Image("/imagenes_varias/2.5estrellas.png"));
			break;
		case "Astralis":
			imgClasifEscudo.setImage(new Image("/imagenes_rival/astralis.png"));
			imgClasifEstrellas.setImage(new Image("/imagenes_varias/3estrellas.png"));
			break;
		case "Misfits":
			imgClasifEscudo.setImage(new Image("/imagenes_rival/misfits.png"));
			imgClasifEstrellas.setImage(new Image("/imagenes_varias/2.5estrellas.png"));
			break;
		case "Rogue":
			imgClasifEscudo.setImage(new Image("/imagenes_rival/rogue.png"));
			imgClasifEstrellas.setImage(new Image("/imagenes_varias/4estrellas.png"));
			break;
		case "Fnatic":
			imgClasifEscudo.setImage(new Image("/imagenes_rival/fnatic.png"));
			imgClasifEstrellas.setImage(new Image("/imagenes_varias/3.5estrellas.png"));
			break;
		case "G2 Esports":
			imgClasifEscudo.setImage(new Image("/imagenes_rival/g2.png"));
			imgClasifEstrellas.setImage(new Image("/imagenes_varias/4.5estrellas.png"));
			break;
		case "MAD Lions":
			imgClasifEscudo.setImage(new Image("/imagenes_rival/mad.png"));
			imgClasifEstrellas.setImage(new Image("/imagenes_varias/4.5estrellas.png"));
			break;
		default:
			imgClasifEscudo.setImage(new Image("/imagenes_escudos/" + escudo + ".png"));
			imgClasifEstrellas.setImage(null);
		}
	}

	@FXML
	public void cargaFotoPlantilla(MouseEvent e) {
		try {
			Jugador jPlantilla = tablaPlantilla.getSelectionModel().getSelectedItem();
			switch (jPlantilla.getNombre()) {
			case "Aesenar":
				imgFoto.setImage(new Image("/imagenes_jugadores/aesenar.png"));
				break;
			case "Alexx":
				imgFoto.setImage(new Image("/imagenes_jugadores/alexx.png"));
				break;
			case "Attila":
				imgFoto.setImage(new Image("/imagenes_jugadores/attila.png"));
				break;
			case "Baca":
				imgFoto.setImage(new Image("/imagenes_jugadores/baca.png"));
				break;
			case "CarritosKami":
				imgFoto.setImage(new Image("/imagenes_jugadores/kami.png"));
				break;
			case "Dreedy":
				imgFoto.setImage(new Image("/imagenes_jugadores/dreedy.png"));
				break;
			case "Efias":
				imgFoto.setImage(new Image("/imagenes_jugadores/efias.png"));
				break;
			case "Flakked":
				imgFoto.setImage(new Image("/imagenes_jugadores/flakked.png"));
				break;
			case "Fresskowy":
				imgFoto.setImage(new Image("/imagenes_jugadores/fresskowy.png"));
				break;
			case "Hadess":
				imgFoto.setImage(new Image("/imagenes_jugadores/hades.png"));
				break;
			case "kamilius":
				imgFoto.setImage(new Image("/imagenes_jugadores/kamilius.png"));
				break;
			case "knighter":
				imgFoto.setImage(new Image("/imagenes_jugadores/knigther.png"));
				break;
			case "Koldo":
				imgFoto.setImage(new Image("/imagenes_jugadores/koldo.png"));
				break;
			case "Lebron":
				imgFoto.setImage(new Image("/imagenes_jugadores/lebron.png"));
				break;
			case "Miniduke":
				imgFoto.setImage(new Image("/imagenes_jugadores/miniduke.png"));
				break;
			case "Namex":
				imgFoto.setImage(new Image("/imagenes_jugadores/namex.png"));
				break;
			case "Nji":
				imgFoto.setImage(new Image("/imagenes_jugadores/nji.png"));
				break;
			case "Oscarinin":
				imgFoto.setImage(new Image("/imagenes_jugadores/oscarinin.png"));
				break;
			case "Plasma":
				imgFoto.setImage(new Image("/imagenes_jugadores/plasma.png"));
				break;
			case "Rafitta":
				imgFoto.setImage(new Image("/imagenes_jugadores/raffita.png"));
				break;
			case "Ronaldo":
				imgFoto.setImage(new Image("/imagenes_jugadores/ronaldo.png"));
				break;
			case "RubiOo":
				imgFoto.setImage(new Image("/imagenes_jugadores/rubio.png"));
				break;
			case "Sacre":
				imgFoto.setImage(new Image("/imagenes_jugadores/sacre.png"));
				break;
			case "SendOo":
				imgFoto.setImage(new Image("/imagenes_jugadores/sendo.png"));
				break;
			case "Skain":
				imgFoto.setImage(new Image("/imagenes_jugadores/skain.png"));
				break;
			case "Supa":
				imgFoto.setImage(new Image("/imagenes_jugadores/supa.png"));
				break;
			case "Th3Antonio":
				imgFoto.setImage(new Image("/imagenes_jugadores/th3antonio.png"));
				break;
			case "whiteinn":
				imgFoto.setImage(new Image("/imagenes_jugadores/whiteinn.png"));
				break;
			}
		} catch (Exception ex) {
		}

	}

	@FXML
	public void cargaFotoMercado(MouseEvent e) {
		try {
			Jugador jMercado = tablaMercado.getSelectionModel().getSelectedItem();
			switch (jMercado.getNombre()) {
			case "Aesenar":
				imgFoto.setImage(new Image("/imagenes_jugadores/aesenar.png"));
				break;
			case "Alexx":
				imgFoto.setImage(new Image("/imagenes_jugadores/alexx.png"));
				break;
			case "Attila":
				imgFoto.setImage(new Image("/imagenes_jugadores/attila.png"));
				break;
			case "Baca":
				imgFoto.setImage(new Image("/imagenes_jugadores/baca.png"));
				break;
			case "CarritosKami":
				imgFoto.setImage(new Image("/imagenes_jugadores/kami.png"));
				break;
			case "Dreedy":
				imgFoto.setImage(new Image("/imagenes_jugadores/dreedy.png"));
				break;
			case "Efias":
				imgFoto.setImage(new Image("/imagenes_jugadores/efias.png"));
				break;
			case "Flakked":
				imgFoto.setImage(new Image("/imagenes_jugadores/flakked.png"));
				break;
			case "Fresskowy":
				imgFoto.setImage(new Image("/imagenes_jugadores/fresskowy.png"));
				break;
			case "Hadess":
				imgFoto.setImage(new Image("/imagenes_jugadores/hades.png"));
				break;
			case "kamilius":
				imgFoto.setImage(new Image("/imagenes_jugadores/kamilius.png"));
				break;
			case "knighter":
				imgFoto.setImage(new Image("/imagenes_jugadores/knigther.png"));
				break;
			case "Koldo":
				imgFoto.setImage(new Image("/imagenes_jugadores/koldo.png"));
				break;
			case "Lebron":
				imgFoto.setImage(new Image("/imagenes_jugadores/lebron.png"));
				break;
			case "Miniduke":
				imgFoto.setImage(new Image("/imagenes_jugadores/miniduke.png"));
				break;
			case "Namex":
				imgFoto.setImage(new Image("/imagenes_jugadores/namex.png"));
				break;
			case "Nji":
				imgFoto.setImage(new Image("/imagenes_jugadores/nji.png"));
				break;
			case "Oscarinin":
				imgFoto.setImage(new Image("/imagenes_jugadores/oscarinin.png"));
				break;
			case "Plasma":
				imgFoto.setImage(new Image("/imagenes_jugadores/plasma.png"));
				break;
			case "Rafitta":
				imgFoto.setImage(new Image("/imagenes_jugadores/raffita.png"));
				break;
			case "Ronaldo":
				imgFoto.setImage(new Image("/imagenes_jugadores/ronaldo.png"));
				break;
			case "RubiOo":
				imgFoto.setImage(new Image("/imagenes_jugadores/rubio.png"));
				break;
			case "Sacre":
				imgFoto.setImage(new Image("/imagenes_jugadores/sacre.png"));
				break;
			case "SendOo":
				imgFoto.setImage(new Image("/imagenes_jugadores/sendo.png"));
				break;
			case "Skain":
				imgFoto.setImage(new Image("/imagenes_jugadores/skain.png"));
				break;
			case "Supa":
				imgFoto.setImage(new Image("/imagenes_jugadores/supa.png"));
				break;
			case "Th3Antonio":
				imgFoto.setImage(new Image("/imagenes_jugadores/th3antonio.png"));
				break;
			case "whiteinn":
				imgFoto.setImage(new Image("/imagenes_jugadores/whiteinn.png"));
				break;
			}
		} catch (Exception ex) {
		}
	}
	
	@FXML
	public void mouseEnteredEliminar(MouseEvent e) {
		lblEliminarLiga.setTextFill(javafx.scene.paint.Color.web("#ff4f4f"));
		//#ff4f4f
	}

	@FXML
	public void mouseExitedEliminar(MouseEvent e) {
		lblEliminarLiga.setTextFill(javafx.scene.paint.Color.RED);
	}
	
	@FXML
	public void mouseEnteredAlinear(MouseEvent e) {
		btnAlinear.setStyle("-fx-background-color: #1E1F39");
	}

	@FXML
	public void mouseExitedAlinear(MouseEvent e) {
		btnAlinear.setStyle("-fx-background-color: #0F102C");
	}
	
	@FXML
	public void mouseEnteredSimular(MouseEvent e) {
		btnSimular.setStyle("-fx-background-color: #1E1F39");
	}

	@FXML
	public void mouseExitedSimular(MouseEvent e) {
		btnSimular.setStyle("-fx-background-color: #0F102C");
	}
	
	@FXML
	public void mouseEnteredComprar(MouseEvent e) {
		btnComprar.setStyle("-fx-background-color: #1E1F39");
	}

	@FXML
	public void mouseExitedComprar(MouseEvent e) {
		btnComprar.setStyle("-fx-background-color: #0F102C");
	}
	
	@FXML
	public void mouseEnteredVender(MouseEvent e) {
		btnVender.setStyle("-fx-background-color: #1E1F39");
	}

	@FXML
	public void mouseExitedVender(MouseEvent e) {
		btnVender.setStyle("-fx-background-color: #0F102C");
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