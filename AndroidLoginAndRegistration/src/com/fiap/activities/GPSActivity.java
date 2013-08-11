package com.fiap.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fiap.event.GPSEvent;
import com.fiap.model.Estatistica;
import com.fiap.service.GPSService;
import com.fiap.service.SocialService;

public class GPSActivity extends BaseActivity {
    
	//TAG used for logs
	private static final String TAG = "GPSActivity";
	
	private static final String ServletUrl="http://ec2-107-21-178-180.compute-1.amazonaws.com:8080/TesteServlet/TesteServlet";
	
	private GPSService gpsService;
	
	private Bundle bundle;
	
	private TextView txtVelocidade;
    private TextView txtVelocidadeMaxima;
    private TextView txtVelocidadeMedia;
    private TextView txtDistancia;
	private TextView txtCalorias;
	private TextView txtTempo;
	private Button btnIniciar;
	private Button btnConvidar;
	private TextView webServiceTextView;
	private TextView txtLocation;
	
	private Estatistica estatistica;

	private boolean calibrando = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SocialService.init(getApplicationContext());
		
		setContentView(R.layout.gps);
		bundle = new Bundle();
		
		gpsService = GPSService.getInstance();	
		
		prepararTela();
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		gpsService.setEvent(new GPSEvent() {
			
			public void onGPSUpdate(Location location) {
				if (calibrando) {
					calibrando = false;
					btnIniciar.setEnabled(true);
					txtVelocidade.setText("Pronto");
				} else {
					if (btnIniciar.getText().equals(getString(R.string.parar))) {
	
						estatistica.update(location.getLatitude(), location.getLongitude(), location.getSpeed());
						
						txtVelocidade.setText(String.format("%.2f km/h", estatistica.getVelocidade()));
						txtVelocidadeMaxima.setText(String.format("%.2f km/h", estatistica.getVelocidadeMaxima()));
						txtVelocidadeMedia.setText(String.format("%.2f km/h", estatistica.getVelocidadeMedia()));
						txtDistancia.setText(String.format("%.2f km", estatistica.getDistanciaTotal()));
						txtCalorias.setText(String.format("%.2f", estatistica.getCalorias()));
						txtTempo.setText(String.format("%.2f min", estatistica.getTempoTotal()));
						txtLocation.setText(gpsService.getLastLocation().getLatitude() + 
											";" +
											gpsService.getLastLocation().getLongitude()); 
					}
				}		
			}
		});
		gpsService.onActivityStarted();
		gpsService.start(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		gpsService.onActivityResumed();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		gpsService.onActivityPaused();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		gpsService.onActivityStopped();
	}
	
	private void prepararTela() {
		txtDistancia		= (TextView)findViewById(R.id.text_gps_distancia);
		txtVelocidade		= (TextView)findViewById(R.id.text_gps_velocidade);
    	txtVelocidadeMaxima	= (TextView)findViewById(R.id.text_gps_velocidadeMaxima);
    	txtVelocidadeMedia	= (TextView)findViewById(R.id.text_gps_velocidadeMedia);
    	txtCalorias			= (TextView)findViewById(R.id.text_gps_calorias);
    	txtTempo			= (TextView)findViewById(R.id.text_gps_tempo);
    	btnIniciar			= (Button)findViewById(R.id.button_gps_iniciar);
    	btnConvidar			= (Button)findViewById(R.id.button_gps_convidar);
    	webServiceTextView = (TextView) findViewById(R.id.text_web_service);
    	txtLocation = (TextView) findViewById(R.id.text_locaton_gps);
    	
    	btnIniciar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				if (btnIniciar.getText().equals(getString(R.string.parar))) {
					btnIniciar.setText(getString(R.string.iniciar));
					
					irParaTelaPostagem();
				} else {
					estatistica = new Estatistica(
							gpsService.getLastLocation().getLatitude(), 
							gpsService.getLastLocation().getLongitude());

					btnIniciar.setText(getString(R.string.parar));
				}
			}
			
		});
    	
    	btnConvidar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				irParaTelaConvite();
			}
			
		});
	}

	private void irParaTelaPostagem() {
		bundle.remove(Constants.PARAM_CONVITE);
		bundle.remove(Constants.PARAM_MENASGEM);
		
		bundle.putSerializable(Constants.PARAM_CONVITE, Boolean.FALSE);
		bundle.putSerializable(Constants.PARAM_MENASGEM, 
				String.format("Acabei de percorrer %.2f km em %.2f minutos, " +
						"com uma velocidade média de %.2f km/h.",
						estatistica.getDistanciaTotal(),
						estatistica.getTempoTotal(),
						estatistica.getVelocidadeMedia()));
		
		iniciarActivity(LoginFacebookActivity.class, bundle);
	}

	private void irParaTelaConvite() {
		bundle.remove(Constants.PARAM_CONVITE);
		bundle.remove(Constants.PARAM_MENASGEM);
		
		bundle.putSerializable(Constants.PARAM_CONVITE, Boolean.TRUE);
		
		iniciarActivity(ConviteActivity.class, bundle);
	}
	
	@Override
	protected void onDestroy() {
		gpsService.stop();
		super.onDestroy();
	}
	
	private class SendPositionTask extends AsyncTask<String, Void, String> {
	    @Override
	    protected String doInBackground(String... params) {
	    	String response = "";
	    	String url = params[0];
	    	String latitude = params[1];
	    	String longitude = params[2];
	    	String email = params[3];
	    	Log.w(TAG, "latitude from Location: " + latitude);
	    	Log.w(TAG, "longitude from Location: " + longitude);
		        
	    	url += "?";
	    			
	        List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
	        nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
	        nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
	        nameValuePairs.add(new BasicNameValuePair("email", email));
	        
	        String paramString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
	        		
	        url += paramString;
	        Log.w(TAG, "url = " + url);
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet(url);
	        
	        
	        try {
	            HttpResponse execute = client.execute(httpGet);
	            InputStream content = execute.getEntity().getContent();

	            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
	            String s = "";
	            while ((s = buffer.readLine()) != null) {
	            	response += s;
		        }

	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      return response;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	    	webServiceTextView.setText(result);
	    }
	  }
	
	public void sendPosition(Location location) {
		Log.w(TAG, "-LAT" + location.getLatitude());
		Log.w(TAG, "-LON" + location.getLongitude());
		
		DecimalFormat decimalFormatter = new DecimalFormat("0.######");

        String latitude = decimalFormatter.format(location.getLatitude());
        String longitude = decimalFormatter.format(location.getLongitude());
		Log.w(TAG, "--LAT" + latitude);
		Log.w(TAG, "--LON" + longitude);
		String email = "leandro.alsberg@gmail.com";
	    SendPositionTask task = new SendPositionTask();
	    task.execute(new String[] { ServletUrl, latitude, longitude, email });

	  }

	 public void goToMap (View v) {
		 	
//	    	Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
		 	Intent i  = new Intent(MainActivity.ACAO_EXIBIR_SAUDACAO);
		 	i.addCategory(MainActivity.CATEGORIA_SAUDACAO);
		 	i.putExtra(MainActivity.EXTRA_POSICAO_USUARIO, gpsService.getLastLocation().getLatitude() + 
		 																							   ";" +
		 													gpsService.getLastLocation().getLongitude());
		 	gpsService.stop();
			startActivity(i);
	    }
}
