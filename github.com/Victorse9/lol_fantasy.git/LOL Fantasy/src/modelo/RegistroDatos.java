package modelo;

public class RegistroDatos {
	private String usuario;
	private String contraseña;

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}

	@Override
	public String toString() {
		return "Register [usuario=" + usuario + ", contraseña=" + contraseña + "]";
	}

}
