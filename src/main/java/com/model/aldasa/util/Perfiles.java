package com.model.aldasa.util;

public enum Perfiles {
	
	ADMINISTRADOR("Administrador",1),
	TECNICO("TÉCNICO FARMACIA",2);
	
	private String name;
	private int id;
	
	private Perfiles(String name,int id) {
		this.name=name;
		this.id=id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}

}
