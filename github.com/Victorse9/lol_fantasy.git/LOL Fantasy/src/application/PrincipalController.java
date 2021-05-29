package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import modelo.Jugador;

public class PrincipalController {
	private String nombreUsuario, nombreEquipo, escudo;
	@FXML
	private Label lblUsuario, lblEquipo;
	@FXML
	private ImageView imgEscudo;
	@FXML
	private Pane pMercado, pClasificacion, pJugar;
	private Conexion conexion = new Conexion();
	private String[] escudoEquipo;
	@FXML
	private TableView<Jugador> tablaMercado = new TableView<Jugador>();
	/**
	 * Constructor
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
			imgEscudo.setImage(new Image("/imagenes_escudos/"+escudo+".png"));
		} catch (ClassNotFoundException | IOException | SQLException e) {
			throw e;
		}
		
		Jugador j1 = conexion.getJugador("Aesenar");
//		Jugador j2 = conexion.getJugador("Alexx");
//		Jugador j3 = conexion.getJugador("Attila");
//		Jugador j4 = conexion.getJugador("Baca");
//		Jugador j5 = conexion.getJugador("CarritosKami");
//		Jugador j6 = conexion.getJugador("Dreedy");
//		Jugador j7 = conexion.getJugador("Efias");
//		Jugador j8 = conexion.getJugador("Flakked");
//		Jugador j9 = conexion.getJugador("Fresskowy");
//		Jugador j10 = conexion.getJugador("Hadess");
//		Jugador j11 = conexion.getJugador("kamilius");
//		Jugador j12 = conexion.getJugador("knighter");
//		Jugador j13 = conexion.getJugador("Koldo");
//		Jugador j14 = conexion.getJugador("Lebron");
//		Jugador j15 = conexion.getJugador("Miniduke");
//		Jugador j16 = conexion.getJugador("Namex");
//		Jugador j17 = conexion.getJugador("Nji");
//		Jugador j18 = conexion.getJugador("Oscarinin");
//		Jugador j19 = conexion.getJugador("Plasma");
//		Jugador j20 = conexion.getJugador("Rafitta");
//		Jugador j21 = conexion.getJugador("Ronaldo");
//		Jugador j22 = conexion.getJugador("RubiOo");
//		Jugador j23 = conexion.getJugador("Sacre");
//		Jugador j24 = conexion.getJugador("SendOo");
//		Jugador j25 = conexion.getJugador("Skain");
//		Jugador j26 = conexion.getJugador("Supa");
//		Jugador j27 = conexion.getJugador("Th3Antonio");
//		Jugador j28 = conexion.getJugador("whiteinn");
		
		ObservableList<Jugador> j = FXCollections.observableArrayList(j1);
		System.out.println(j.toString());
		
		tablaMercado.setItems(j);

		
		tablaMercado.refresh();
		
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
	public void cambiaPanel(ActionEvent e) {
		Button botonMenu = (Button) ((Node) e.getSource());
		String eleccion = botonMenu.getText();

		switch(eleccion){
			case "JUGAR":
				pJugar.setVisible(true);
				pClasificacion.setVisible(false);
				pMercado.setVisible(false);
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
}