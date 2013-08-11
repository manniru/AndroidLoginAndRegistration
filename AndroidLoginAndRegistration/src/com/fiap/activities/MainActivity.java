package com.fiap.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fiap.event.GPSEvent;
import com.fiap.model.Estatistica;
import com.fiap.service.GPSService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity {
  public static final String EXTRA_POSICAO_USUARIO =
		  "com.fiap.EXTRA_POSICAO_USUARIO";

  public static final String ACAO_EXIBIR_SAUDACAO =
		  "helloandroid.ACAO_EXIBIR_SAUDACAO";
  
  public static final String CATEGORIA_SAUDACAO =
		  "helloandroid.CATEGORIA_SAUDACAO";

  static String[] arrayPosition = new String[2];
  static String position;
  static String latitude;
  static String longitude;
  static LatLng gpsUserPosition; 
  private GoogleMap map;
  
  private Estatistica estatistica;
  
  private GPSService gpsService = GPSService.getInstance();
  
  private boolean calibrando = true;
  
  private Marker actualMarker;
  private Marker lastMarker;
  private ArrayList<LatLng> arrayPositionsRoute; 

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
	Intent intent = getIntent();
	if (intent.hasExtra(EXTRA_POSICAO_USUARIO)) {
		position = intent.getStringExtra(EXTRA_POSICAO_USUARIO);
		arrayPosition = position.split(";");
		latitude = arrayPosition[0];
		longitude = arrayPosition[1];
		
		arrayPositionsRoute = new ArrayList<LatLng>();
				
		gpsUserPosition = new LatLng(Double.parseDouble(latitude), 
									 Double.parseDouble(longitude));
		
		arrayPositionsRoute.add(gpsUserPosition);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		        .getMap();
		
		actualMarker = map.addMarker(new MarkerOptions().position(gpsUserPosition)
			    .title("João")
			    .snippet("Runners porra!")
			    .icon(BitmapDescriptorFactory
			        .fromResource(R.drawable.personagem_mapa_001)));
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(gpsUserPosition, 15));
		
	    gpsService.setEvent(new GPSEvent() 
	    {
	    	public void onGPSUpdate(Location location) {
				if (calibrando) {
					calibrando = false;
				} else {
						if (!(actualMarker == null)){
							actualMarker.remove();
							LatLng actualPosition = new LatLng(gpsService.getLastLocation().getLatitude(), 
															   gpsService.getLastLocation().getLongitude());

							arrayPositionsRoute.add(actualPosition);
							
							actualMarker = map.addMarker(new MarkerOptions()
							.position(actualPosition)
							.title("João")
							.snippet("Runners porra!")
							.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.personagem_mapa_001)));
							
							map.moveCamera(CameraUpdateFactory.newLatLngZoom(actualPosition, 15));
							
							PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
							for(int i = 0 ; i < arrayPositionsRoute.size() ; i++) {          
								rectLine.add(arrayPositionsRoute.get(i));
								}
							
							map.addPolyline(rectLine);
						}
						
				}		
	    	}
	    });
	    
	    gpsService.start(this);
	} else {
		Toast toast = Toast.makeText(this, 
									 "Problemas durante o carregamento do mapa",				
									 Toast.LENGTH_SHORT);
		toast.show();
//		backToMenu();
	}
     
  }
  
  @Override
  protected void onStart() {
	super.onStart();
  }
  
  @Override
  protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
  }
  
  @Override
  protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
  }
  
  @Override
  protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
  }
  
  public void backToInfo (View v) {
	gpsService.stop();  
  	Intent i = new Intent(getApplicationContext(), GPSActivity.class);
  	startActivity(i);
  }
 
} 