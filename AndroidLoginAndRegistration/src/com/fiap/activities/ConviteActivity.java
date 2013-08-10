package com.fiap.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConviteActivity extends BaseActivity {

	private Bundle bundle;
	
	private TextView txtMensagem;
	private Button btnConfirmar;
	private Button btnCancelar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.convite);
		
		bundle = getIntent().getExtras();
		
		prepararTela();
	}

	private void prepararTela() {
		txtMensagem = (TextView)findViewById(R.id.text_convite_mensagem);
		
		btnConfirmar	= (Button)findViewById(R.id.button_convite_continuar);
		btnCancelar		= (Button)findViewById(R.id.button_convite_cancelar);
		
		btnConfirmar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				irParaProximaTela();
			}
			
		});
		
		btnCancelar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				irParaTelaInicial();
			}
			
		});
	}

	private void irParaProximaTela() {
		bundle.putSerializable(Constants.PARAM_MENASGEM, txtMensagem.getText().toString());
		
		iniciarActivity(LoginFacebookActivity.class, bundle);
	}

	private void irParaTelaInicial() {
		iniciarActivity(GPSActivity.class, bundle);
	}
	
}