package modelo;

public class Jugador {
	private String nombre;
	private String posicion;
	private int calidad;
	private int precio;
	
	

	public Jugador(String nombre, String posicion, int calidad, int precio) {
		super();
		this.nombre = nombre;
		this.posicion = posicion;
		this.calidad = calidad;
		this.precio = precio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public int getCalidad() {
		return calidad;
	}

	public void setCalidad(int calidad) {
		this.calidad = calidad;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	@Override
	public String toString() {
		return "Jugador [nombre=" + nombre + ", posicion=" + posicion + ", calidad=" + calidad + ", precio=" + precio
				+ "]";
	}


}
