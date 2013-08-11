package com.fiap.model;

import com.fiap.service.GPSService;

public class Estatistica {

	private static final double MIN_MULTIPLIER = 60000.00;
	private static final double HOUR_MULTIPLIER = 3600.00;
	private static final double KM_MULTIPLIER = 0.001;
	private static final double CAL_KM_H = 1.67;
	private static final double PESO_MEDIO = 0.70;
	
	private double latitude = 0;
	private double longitude = 0;
	private long milisegundos = 0;

	private double velocidade = 0.00;
	private double velocidadeTotal = 0.00;
	private double velocidadeMaxima = 0.00;
	private double velocidadeMedia = 0.00;
	private double distanciaTotal = 0.00;    
	private double calorias = 0.00;    
	private double tempoTotal = 0.00;    
	private long medicoes = 0;
	
	public Estatistica(double latitudeAtual, double longitudeAtual) {
		this.latitude = latitudeAtual;
		this.longitude = longitudeAtual;
		this.milisegundos = System.currentTimeMillis();
	}

	public void update(double latitudeAtual, double longitudeAtual, double velocidadeAtual) {
			
    	medicoes++;

    	long milisegundosAtual = System.currentTimeMillis();	
		
    	distanciaTotal += Math.abs(GPSService.calcDistanceBetween(latitude, longitude, latitudeAtual, longitudeAtual) * KM_MULTIPLIER); 

		velocidade = (velocidadeAtual * HOUR_MULTIPLIER) * KM_MULTIPLIER;
    	velocidadeMaxima = velocidade > velocidadeMaxima ? velocidade : velocidadeMaxima;
    	velocidadeTotal += velocidade;
    	velocidadeMedia = velocidadeTotal / medicoes;
    	
    	double intervaloTempo = (milisegundosAtual - milisegundos) / MIN_MULTIPLIER;
    	tempoTotal += intervaloTempo;
    	
    	calorias += PESO_MEDIO * intervaloTempo * CAL_KM_H * velocidade; 

		latitude = latitudeAtual;
		longitude = longitudeAtual;
    	milisegundos = milisegundosAtual;
		
	}

	public double getVelocidade() {
		return velocidade;
	}
	
	public double getVelocidadeMaxima() {
		return velocidadeMaxima;
	}

	public double getVelocidadeMedia() {
		return velocidadeMedia;
	}

	public double getDistanciaTotal() {
		return distanciaTotal;
	}

	public double getCalorias() {
		return calorias;
	}

	public double getTempoTotal() {
		return tempoTotal;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
}
