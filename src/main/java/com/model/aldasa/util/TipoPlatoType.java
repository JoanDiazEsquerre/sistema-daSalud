package com.model.aldasa.util;

public enum TipoPlatoType {
	
	ENTRADA("Entrada"),MENU("Menu"), CARTA("Carta"), BEBIDA("Bebida");
	
	private String nombre;
	
	private TipoPlatoType(String nombre) {
		this.nombre=nombre;
	}
	
	public String getNombre() {
		return nombre;
	}
}
