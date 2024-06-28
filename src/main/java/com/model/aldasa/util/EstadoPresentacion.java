package com.model.aldasa.util;

public enum EstadoPresentacion {
	
	PENDIENTE("Pendiente"),CONSUMIDO("Consumido"), DEVUELTO("Devuelto"),VENCIDO("Vencido"), ANULADO("Anulado");
	
	private String name;
	
	private EstadoPresentacion(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
}
