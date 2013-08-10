package com.fiap.service;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.fiap.event.GPSEvent;
import com.fiap.service.exception.GPSServiceException;

public class GPSService {
	
	private static final int MIN_TIME = 500;
	private static final int MIN_DISTANCE = 0;
	
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	private GPSEvent event = null;
	private Location ultimaLocalizacao;
	
	public GPSService() {	
		this.locationListener = new LocationListener() {
			
			public void onLocationChanged(final Location location) {
				if (GPSService.this.event != null) {
					GPSService.this.ultimaLocalizacao = location;
					GPSService.this.event.onGPSUpdate(location);
				}
			}
			
			public void onProviderDisabled(final String provider) {}
			public void onProviderEnabled(final String provider) {}
			public void onStatusChanged(final String provider, final int status, final Bundle extras) {}
			
		};
	}
	
	public void setEvent(GPSEvent event) {
		this.event = event;
	}
	
	public Location getUltimaLocalizacao() {
		return ultimaLocalizacao;
	}
	
	public static double calcularDistanciaEntre(final double lat1, final double lon1, final double lat2, final double lon2) {
		double distance = 0.0;
		
		try {
			final float[] results = new float[3];
			
			Location.distanceBetween(lat1, lon1, lat2, lon2, results);
			
			distance = results[0];
		} catch (final Exception ex) {
			distance = 0.0;
		}
		
		return distance;
	}

	
	public void start(final Context context) {
		if (this.locationManager == null) {
			this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}
		
		final Criteria criteria = new Criteria();
		
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(true);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		
		final String bestProvider = this.locationManager.getBestProvider(criteria, true);
		
		if (bestProvider != null && bestProvider.length() > 0) {
			this.locationManager.requestLocationUpdates(bestProvider, GPSService.MIN_TIME,
					GPSService.MIN_DISTANCE, this.locationListener);
		} else {
			final List<String> providers = this.locationManager.getProviders(true);
			
			for (final String provider : providers) {
				this.locationManager.requestLocationUpdates(provider, GPSService.MIN_TIME,
						GPSService.MIN_DISTANCE, this.locationListener);
			}
		}
	}
	
	public void stop() {
		try {
			if (this.locationManager != null && this.locationListener != null) {
				this.locationManager.removeUpdates(this.locationListener);
			}
			
			this.locationManager = null;
			this.event = null;
		} catch (final Exception e) {
			throw new GPSServiceException("Erro ao tentar parar serviço de GPS", e);
		}
	}
}
