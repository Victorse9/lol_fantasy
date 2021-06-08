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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RegistroController {
	@FXML
	private TextField txtUsuario, txtContraseña1, txtContraseña2;
	private Conexion conexion = new Conexion();
	@FXML
	private Label noticia1, noticia2;
	@FXML
	private Circle c1, c2, c3;
	@FXML
	private Button btnCrear;

	@FXML
	public void volver(MouseEvent event) throws Exception {
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
			throw e;
		}
	}

	@FXML
	public void cambiaNoticia(MouseEvent e) {
		switch (e.getSource().toString()) {

		case "Circle[id=c1, centerX=0.0, centerY=0.0, radius=14.0, fill=0xffffffff, stroke=0x000000ff, strokeWidth=1.0]":
			noticia1.setText("¡Bienvenido a LOL FANTASY!");
			noticia2.setText("LOL FANTASY juega contra los mejores equipos de europa");
			break;
		case "Circle[id=c2, centerX=0.0, centerY=0.0, radius=14.0, fill=0xffffffff, stroke=0x000000ff, strokeWidth=1.0]":
			noticia1.setText("Ficha a los mejores cracks");
			noticia2.setText("Revisa el mercado y ficha a los mejores jugadores");
			break;
		case "Circle[id=c3, centerX=0.0, centerY=0.0, radius=14.0, fill=0xffffffff, stroke=0x000000ff, strokeWidth=1.0]":
			noticia1.setText("¡Hazte con el título!");
			noticia2.setText("Acaba la temporada el primero y gana la liga");
			break;
		}

	}

	@FXML
	public void registrarUsuario(ActionEvent e) throws Exception {
		String nombreUsuario = txtUsuario.getText();
		String contraseña1 = txtContraseña1.getText();
		String contraseña2 = txtContraseña2.getText();

		// Comprueba que se completen bien los campos
		if (nombreUsuario.length() < 1) {
			JOptionPane.showMessageDialog(null, "Nombre no válido. Es demasiado corto.");
		} else if (contraseña1.length() < 1 || contraseña2.length() < 1) {
			JOptionPane.showMessageDialog(null, "Contraseña no válida. Es demasiado corta.");
		} else if (!contraseña1.equals(contraseña2)) {
			JOptionPane.showMessageDialog(null, "Error. Las contraseñas no coinciden.");
		} else {
			try {
				conexion.creaUsuario(nombreUsuario, contraseña1, contraseña2);
				((Node) e.getSource()).getScene().getWindow().hide();
				try {
					Stage primaryStage = new Stage();
					AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("Login.fxml"));
					Scene scene = new Scene(root, 1280, 690);
					primaryStage.setScene(scene);
					primaryStage.setResizable(false);
					// primaryStage.initStyle(StageStyle.UNDECORATED);
					// primaryStage.getIcons().add(new Image("/complementos/logo.png"));
					primaryStage.show();
				} catch (Exception ex) {
					throw ex;
				}
			} catch (ClassNotFoundException | IOException | SQLException e1) {
				throw e1;
			}
		}

	}
	
	@FXML
	public void mouseEnteredCrear(MouseEvent e) {
		btnCrear.setStyle("-fx-background-color: #1E1F39");
	}
	
	@FXML
	public void mouseExitedCrear(MouseEvent e) { 	 
		btnCrear.setStyle("-fx-background-color: #0F102C");
	}
	@FXML
	public void mouseEnteredAd1(MouseEvent e) {
		c1.setOpacity(0.5);
	}
	
	@FXML
	public void mouseExitedAd1(MouseEvent e) { 	 
		c1.setOpacity(0.9);
	}
	@FXML
	public void mouseEnteredAd2(MouseEvent e) {
		c2.setOpacity(0.5);
	}
	
	@FXML
	public void mouseExitedAd2(MouseEvent e) { 	 
		c2.setOpacity(0.9);
	}
	@FXML
	public void mouseEnteredAd3(MouseEvent e) {
		c3.setOpacity(0.5);
	}
	
	@FXML
	public void mouseExitedAd3(MouseEvent e) { 	 
		c3.setOpacity(0.9);
	}
}