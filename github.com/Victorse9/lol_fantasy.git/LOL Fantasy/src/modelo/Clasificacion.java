package modelo;

public class Clasificacion {
	private String equipo;
	private int n_partido;
	private int partidos_ganados;
	private int eliminaciones;
	public String getEquipo() {
		return equipo;
	}
	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}
	public int getN_partido() {
		return n_partido;
	}
	public void setN_partido(int n_partido) {
		this.n_partido = n_partido;
	}
	public int getPartidos_ganados() {
		return partidos_ganados;
	}
	public void setPartidos_ganados(int partidos_ganados) {
		this.partidos_ganados = partidos_ganados;
	}
	public int getEliminaciones() {
		return eliminaciones;
	}
	public void setEliminaciones(int eliminaciones) {
		this.eliminaciones = eliminaciones;
	}
	@Override
	public String toString() {
		return "Clasificacion [equipo=" + equipo + ", n_partido=" + n_partido + ", partidos_ganados=" + partidos_ganados
				+ ", eliminaciones=" + eliminaciones + "]";
	}



}
