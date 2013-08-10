package com.fiap.model;

public class Contato {

	private String id; 
    private String name;
    private String screenName;
    
	public Contato(String id, String name, String screenName) {
		this.id = id;
		this.name = name;
		this.screenName = screenName;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getScreenName() {
		return screenName;
	}
    
}
