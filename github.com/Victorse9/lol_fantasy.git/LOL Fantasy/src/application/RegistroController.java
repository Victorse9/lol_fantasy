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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RegistroController {
	@FXML
	private TextField txtUsuario, txtContraseña1, txtContraseña2;
	private Conexion conexion = new Conexion();

	@FXML
	public void volver(ActionEvent event) throws Exception {
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
}