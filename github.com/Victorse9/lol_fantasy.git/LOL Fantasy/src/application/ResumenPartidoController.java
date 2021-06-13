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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ResumenPartidoController {

	private Conexion conexion = new Conexion();
	private String nombreUsuario, escudo, escudoRival, ganador;
	private boolean victoria;
	private int killsE1, killsE2, jornada;
	@FXML
	private ImageView imgVictoria, imgDerrota, imgResumenE1, imgResumenE2, imgEscudoGanador;
	@FXML
	private Label lblKills1, lblKills2, lblOroGanado, lblMensajeGanador;
	@FXML
	private AnchorPane container;
	@FXML
	private Pane paneFinal, paneResumen;
	@FXML
	private Button btnSalir2, btnSalir1;
	
	/**
	 * Constructor
	 * 
	 * @param nombreUsuario
	 */
	public ResumenPartidoController(String nombreUsuario, String escudo, String escudoRival, boolean victoria,
			int killsE1, int killsE2, int jornada) {
		this.nombreUsuario = nombreUsuario;
		this.escudo = escudo;
		this.escudoRival = escudoRival;
		this.victoria = victoria;
		this.killsE1 = killsE1;
		this.killsE2 = killsE2;
		this.jornada = jornada;
	}

	public void initialize() {
		this.onDraggedScene(this.container);
		// Carga escudos
		imgResumenE1.setImage(new Image("/imagenes_escudos/" + escudo + ".png"));
		imgResumenE2.setImage(new Image("/imagenes_rival/" + escudoRival + ".png"));

		// Carga victoria o derrota y oro ganado
		if (victoria) {
			imgVictoria.setVisible(true);
			imgDerrota.setVisible(false);
			lblOroGanado.setText(String.valueOf(8000));
		} else {
			imgVictoria.setVisible(false);
			imgDerrota.setVisible(true);
			imgDerrota.setImage(new Image("/imagenes_varias/defeat.png"));
			lblOroGanado.setText(String.valueOf(3000));
		}

		// Carga eliminaciones
		lblKills1.setText(String.valueOf(killsE1));
		lblKills2.setText(String.valueOf(killsE2));
	}
	
	@FXML
	public void salir(ActionEvent e) {
		((Node) e.getSource()).getScene().getWindow().hide();
		try {
			paneResumen.setVisible(true);
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
	public void opcionSalir(ActionEvent e) throws ClassNotFoundException, IOException, SQLException {
		if(jornada < 9) {
			((Node) e.getSource()).getScene().getWindow().hide();
			try {
				paneResumen.setVisible(true);
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
		}else {
			paneResumen.setVisible(false);
			paneFinal.setVisible(true);
			ganador = conexion.getGanador(nombreUsuario);
			lblMensajeGanador.setText("¡ "+ganador+" ES EL NUEVO CAMPEON DE LA LEC !");
			
			switch(ganador) {
			case "SK Gaming":
				imgEscudoGanador.setImage(new Image("/imagenes_rival/sk.png"));
				break;
			case "Schalke04":
				imgEscudoGanador.setImage(new Image("/imagenes_rival/s04.png"));
				break;
			case "Vitality":
				imgEscudoGanador.setImage(new Image("/imagenes_rival/vitality.png"));
				break;
			case "Astralis":
				imgEscudoGanador.setImage(new Image("/imagenes_rival/astralis.png"));
				break;
			case "Misfits":
				imgEscudoGanador.setImage(new Image("/imagenes_rival/misfits.png"));
				break;
			case "Rogue":
				imgEscudoGanador.setImage(new Image("/imagenes_rival/rogue.png"));
				break;
			case "Fnatic":
				imgEscudoGanador.setImage(new Image("/imagenes_rival/fnatic.png"));
				break;
			case "G2 Esports":
				imgEscudoGanador.setImage(new Image("/imagenes_rival/g2.png"));
				break;
			case "MAD Lions":
				imgEscudoGanador.setImage(new Image("/imagenes_rival/mad.png"));
				break;
			default:
				imgEscudoGanador.setImage(new Image("/imagenes_escudos/"+escudo+".png"));
			}
		}
	}
	
	@FXML
	public void mouseEnteredSalir1(MouseEvent e) {
		btnSalir1.setStyle("-fx-background-color: #FF4141");
	}

	@FXML
	public void mouseExitedSalir1(MouseEvent e) {
		btnSalir1.setStyle("-fx-background-color: RED");
	}
	
	@FXML
	public void mouseEnteredSalir2(MouseEvent e) {
		btnSalir2.setStyle("-fx-background-color: #FF4141");
	}

	@FXML
	public void mouseExitedSalir2(MouseEvent e) {
		btnSalir2.setStyle("-fx-background-color: RED");
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
