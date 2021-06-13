package application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import connection.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EliminaLigaController {

	private String nombreUsuario;
	private Conexion conexion = new Conexion();
	@FXML
	private AnchorPane container;
	@FXML
	private Button btnSi, btnNo;

	/**
	 * Constructor
	 * 
	 * @param nombreUsuario
	 */
	public EliminaLigaController(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	@FXML
	public void initialize() {
		this.onDraggedScene(this.container);
	}
	
	@FXML
	public void mouseEnteredSi(MouseEvent e) {
		btnSi.setStyle("-fx-background-color: #FF4141");
	}

	@FXML
	public void mouseExitedSi(MouseEvent e) {
		btnSi.setStyle("-fx-background-color: RED");
	}
	
	@FXML
	public void mouseEnteredNo(MouseEvent e) {
		btnNo.setStyle("-fx-background-color: #FF4141");
	}

	@FXML
	public void mouseExitedNo(MouseEvent e) {
		btnNo.setStyle("-fx-background-color: RED");
	}

	@FXML
	public void opcionNO(ActionEvent e) {
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
			stage.getIcons().add(new Image("/imagenes_varias/icono.JPG"));
			stage.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	public void opcionSI(ActionEvent e) throws ClassNotFoundException, IOException, SQLException {

		conexion.eliminarLiga(nombreUsuario);
		((Node) e.getSource()).getScene().getWindow().hide();
		try {
			Stage primaryStage = new Stage();
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("Login.fxml"));
			Scene scene = new Scene(root, 1280, 690);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image("/imagenes_varias/icono.JPG"));
			primaryStage.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Permite arrastrar la ventana
	 * 
	 * @param panelFather
	 */
	public void onDraggedScene(AnchorPane panelFather) {
		AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
		AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

		panelFather.setOnMousePressed(e -> {
			Stage stage = (Stage) panelFather.getScene().getWindow();
			xOffset.set(stage.getX() - e.getScreenX());
			yOffset.set(stage.getY() - e.getScreenY());

		});

		panelFather.setOnMouseDragged(e -> {
			Stage stage = (Stage) panelFather.getScene().getWindow();
			stage.setX(e.getScreenX() + xOffset.get());
			stage.setY(e.getScreenY() + yOffset.get());
			panelFather.setStyle("-fx-cursor: CLOSED_HAND;");
		});

		panelFather.setOnMouseReleased(e -> panelFather.setStyle("-fx-cursor: DEFAULT;"));

	}
}
