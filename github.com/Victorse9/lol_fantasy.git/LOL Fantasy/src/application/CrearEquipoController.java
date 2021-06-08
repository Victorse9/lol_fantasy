package application;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import connection.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CrearEquipoController {
	@FXML
	private ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12, img13, img14, img15,
			imgE1, imgE2, imgE3, imgE4, imgE5, imgE6, imgE7, imgE8, imgE9, imgE10, imgE11, imgE12, imgE13, imgE14,
			imgE15, imgAux;
	@FXML
	private TextField txtNombreEquipo;
	private String imgId, escudo, nombreUsuario, nombreEquipo;
	private Conexion conexion = new Conexion();
	@FXML
	private Button btnEmpezar;

	public CrearEquipoController(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	@FXML
	public void crearEquipo(ActionEvent e) throws Exception {
		nombreEquipo = txtNombreEquipo.getText();
		if (txtNombreEquipo.getText().length() < 1 || getSeleccion().equals("")) {
			JOptionPane.showMessageDialog(null, "Necesitas escoger un nombre para tu equipo y un escudo.");
		} else {

			try {
				// UPDATE A LA TABLA REGISTRO GUARDANDO ESCUDO Y NOMBRE EQUIPO
				conexion.creaEquipo(nombreEquipo, escudo, nombreUsuario);

				conexion.insertJugador(nombreUsuario, "Aesenar");
				conexion.insertJugador(nombreUsuario, "CarritosKami");
				conexion.insertJugador(nombreUsuario, "Hadess");
				conexion.insertJugador(nombreUsuario, "knighter");
				conexion.insertJugador(nombreUsuario, "SendOo");

				// CARGA SIGUIENTE VENTANA
				((Node) e.getSource()).getScene().getWindow().hide();
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("Principal.fxml"));
					PrincipalController principalController = new PrincipalController(nombreUsuario);
					loader.setController(principalController);
					Stage stage = new Stage();
					AnchorPane root = (AnchorPane) loader.load();
					Scene scene = new Scene(root, 1280, 690);
					stage.setScene(scene);
					stage.setResizable(false);
					// primaryStage.initStyle(StageStyle.UNDECORATED);
					stage.getIcons().add(new Image("/imagenes_varias/icono.JPG"));
					stage.show();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} catch (ClassNotFoundException | IOException | SQLException e1) {
				throw e1;
			}
		}

	}

	public String getSeleccion() {
		escudo = "";
		if (img1.isVisible()) {
			escudo = "betis";
		}
		if (img2.isVisible()) {
			escudo = "g2a";
		}
		if (img3.isVisible()) {
			escudo = "invictus";
		}
		if (img4.isVisible()) {
			escudo = "queso";
		}
		if (img5.isVisible()) {
			escudo = "t1";
		}
		if (img6.isVisible()) {
			escudo = "dwg";
		}
		if (img7.isVisible()) {
			escudo = "fpx";
		}
		if (img8.isVisible()) {
			escudo = "liquid";
		}
		if (img9.isVisible()) {
			escudo = "lsb";
		}
		if (img10.isVisible()) {
			escudo = "drx";
		}
		if (img11.isVisible()) {
			escudo = "emonkeyz";
		}
		if (img12.isVisible()) {
			escudo = "giants";
		}
		if (img13.isVisible()) {
			escudo = "omg";
		}
		if (img14.isVisible()) {
			escudo = "riders";
		}
		if (img15.isVisible()) {
			escudo = "ucam";
		}
		return escudo;
	}

	@FXML
	public void clickEscudo(MouseEvent e) {
		imgAux = (ImageView) ((Node) e.getSource());
		imgId = imgAux.getId();

		switch (imgId) {

		case "img1":
		case "imgE1":
			setMarcosInvisibles();
			img1.setVisible(true);
			break;
		case "img2":
		case "imgE2":
			setMarcosInvisibles();
			img2.setVisible(true);
			break;
		case "img3":
		case "imgE3":
			setMarcosInvisibles();
			img3.setVisible(true);
			break;
		case "img4":
		case "imgE4":
			setMarcosInvisibles();
			img4.setVisible(true);
			break;
		case "img5":
		case "imgE5":
			setMarcosInvisibles();
			img5.setVisible(true);
			break;
		case "img6":
		case "imgE6":
			setMarcosInvisibles();
			img6.setVisible(true);
			break;
		case "img7":
		case "imgE7":
			setMarcosInvisibles();
			img7.setVisible(true);
			break;
		case "img8":
		case "imgE8":
			setMarcosInvisibles();
			img8.setVisible(true);
			break;
		case "img9":
		case "imgE9":
			setMarcosInvisibles();
			img9.setVisible(true);
			break;
		case "img10":
		case "imgE10":
			setMarcosInvisibles();
			img10.setVisible(true);
			break;
		case "img11":
		case "imgE11":
			setMarcosInvisibles();
			img11.setVisible(true);
			break;
		case "img12":
		case "imgE12":
			setMarcosInvisibles();
			img12.setVisible(true);
			break;
		case "img13":
		case "imgE13":
			setMarcosInvisibles();
			img13.setVisible(true);
			break;
		case "img14":
		case "imgE14":
			setMarcosInvisibles();
			img14.setVisible(true);
			break;
		case "img15":
		case "imgE15":
			setMarcosInvisibles();
			img15.setVisible(true);
			break;
		}
	}

	/**
	 * Pone los marcos brillantes invisibles
	 */
	public void setMarcosInvisibles() {
		img1.setVisible(false);
		img2.setVisible(false);
		img3.setVisible(false);
		img4.setVisible(false);
		img5.setVisible(false);
		img6.setVisible(false);
		img7.setVisible(false);
		img8.setVisible(false);
		img9.setVisible(false);
		img10.setVisible(false);
		img11.setVisible(false);
		img12.setVisible(false);
		img13.setVisible(false);
		img14.setVisible(false);
		img15.setVisible(false);
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
	public void mouseEntered(MouseEvent e) {
		btnEmpezar.setStyle("-fx-background-color: #1E1F39");
	}
	
	@FXML
	public void mouseExited(MouseEvent e) { 	 
		btnEmpezar.setStyle("-fx-background-color: #0F102C");
	}
}