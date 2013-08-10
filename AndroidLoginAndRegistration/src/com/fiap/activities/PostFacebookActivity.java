package com.fiap.activities;

import android.os.AsyncTask;
import android.os.Bundle;

import com.fiap.service.FacebookService;

public class PostFacebookActivity extends BaseActivity {

	private FacebookService facebookService = new FacebookService();
	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		
		bundle = getIntent().getExtras();
		
		new PostTweetTask().execute();
	}
	
	private class PostTweetTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mostrarMensagemProgresso("Postando no mural...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			String mensagem = (String)bundle.getSerializable(Constants.PARAM_MENASGEM);
			
			facebookService.postarNoMural(mensagem);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			fecharMensagemProgresso();
			
			irParaProximaTela();
		}

	}

	private void irParaProximaTela() {
		iniciarActivity(LoginTwitterActivity.class, bundle);
	}
	
}
