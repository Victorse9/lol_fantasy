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

public class LoginController {
	@FXML
	private Button btnEntrar;
	@FXML
	private TextField txtUsuario, txtContraseña;
	@FXML
	private Label noticia1, noticia2;
	@FXML
	private Circle c1, c2, c3;

	private Conexion conexion = new Conexion();

	@FXML
	public void initialize() {

	}

	@FXML
	public void entrar(ActionEvent event) throws Exception {
		String nombreUsuario = txtUsuario.getText();
		String contraseña = txtContraseña.getText();
		Boolean existeUsuario;

		try {
			// Comprueba que exista el usuario
			existeUsuario = conexion.compruebaRegistro(nombreUsuario, contraseña);

			if (existeUsuario == false) {
				JOptionPane.showMessageDialog(null, "El usuario no existe o las credenciales son incorrectas");
			} else {
				((Node) event.getSource()).getScene().getWindow().hide();
				// SI EL EQUIPO ESTA CREADO LOGEA AL PRINCIPAL SI NO LOGEA A CREACION DE EQUIPO
				if (conexion.existeEquipo(nombreUsuario) == true) {
					// CARGA SIGUIENTE VENTANA
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
					} catch (Exception e) {
						e.printStackTrace();
					}
					// Si no existe el equipo salta la ventana de creacion de equipo
				} else {
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("CrearEquipo.fxml"));
						CrearEquipoController crearEquipoController = new CrearEquipoController(nombreUsuario);
						loader.setController(crearEquipoController);
						Stage stage = new Stage();
						AnchorPane root = (AnchorPane) loader.load();
						Scene scene = new Scene(root, 1280, 690);
						stage.setScene(scene);
						stage.setResizable(false);
						// primaryStage.initStyle(StageStyle.UNDECORATED);
						stage.getIcons().add(new Image("/imagenes_varias/icono.JPG"));
						stage.show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (ClassNotFoundException | IOException | SQLException e1) {
			throw e1;
		}
	}

	@FXML
	public void goRegistro(ActionEvent event) {
		((Node) event.getSource()).getScene().getWindow().hide();
		try {
			Stage primaryStage = new Stage();
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("Registro.fxml"));
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

}