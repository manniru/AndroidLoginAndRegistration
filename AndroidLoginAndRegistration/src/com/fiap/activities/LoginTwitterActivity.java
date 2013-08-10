package com.fiap.activities;

import org.springframework.social.connect.Connection;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.fiap.service.SocialService;

public class LoginTwitterActivity extends BaseWebActivity {

	private static final String REQUEST_TOKEN_KEY = "request_token";
	private static final String REQUEST_TOKEN_SECRET_KEY = "request_token_secret";

	private Bundle bundle;

	private SharedPreferences twitterPreferences;

	private Boolean convite;
	private String mensagem;
	
	private TwitterConnectionFactory connectionFactory;

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		connectionFactory = (TwitterConnectionFactory) SocialService.getConnectionFactory(Twitter.class);
		
		this.twitterPreferences = getSharedPreferences("TwitterConnectPreferences", Context.MODE_PRIVATE);
	}

	@Override
	public void onStart() {
		super.onStart();
		Uri uri = getIntent().getData();
		if (uri != null) {
			String paramteroOAuth = uri.getQueryParameter("oauth_verifier");

			if (paramteroOAuth != null) {
				getWebView().clearView();
				new FinalizaConexaoTwitterTask().execute(paramteroOAuth);
			}
		} else {
			
			bundle = getIntent().getExtras();
			
			convite = (Boolean)bundle.getSerializable(Constants.PARAM_CONVITE);
			mensagem = (String)bundle.getSerializable(Constants.PARAM_MENASGEM);
			
			new InicializaConexaoTwitterTask().execute();
		}
	}

	private String getUrlRetorno() {
		return getString(R.string.twitter_oauth_callback_url);
	}

	private void mostrarTelaDeAutenticacao(OAuthToken tokenRequisicao) {
		// save for later use
		saveRequestToken(tokenRequisicao);

		// Generate the Twitter authorization URL to be used in the browser or web view
		String urlDeAutenticacao = connectionFactory.getOAuthOperations().buildAuthorizeUrl(
				tokenRequisicao.getValue(), OAuth1Parameters.NONE);

		// display the twitter authorization screen
		getWebView().loadUrl(urlDeAutenticacao);
	}

	private void saveRequestToken(OAuthToken requestToken) {
		SharedPreferences.Editor editor = this.twitterPreferences.edit();
		
		editor.putBoolean(Constants.PARAM_CONVITE, convite);
		editor.putString(Constants.PARAM_MENASGEM, mensagem);
		
		editor.putString(REQUEST_TOKEN_KEY, requestToken.getValue());
		editor.putString(REQUEST_TOKEN_SECRET_KEY, requestToken.getSecret());
		
		editor.commit();
	}

	private OAuthToken getTokenRequisicao() {
		convite = this.twitterPreferences.getBoolean(Constants.PARAM_CONVITE, false);
		mensagem = this.twitterPreferences.getString(Constants.PARAM_MENASGEM, null);
		
		bundle = new Bundle();
		bundle.putBoolean(Constants.PARAM_CONVITE, convite);
		bundle.putString(Constants.PARAM_MENASGEM, mensagem);
		
		String chave = this.twitterPreferences.getString(REQUEST_TOKEN_KEY, null);
		String senha = this.twitterPreferences.getString(REQUEST_TOKEN_SECRET_KEY, null);
		
		return new OAuthToken(chave, senha);
	}

	private void deletarTokenRequisicao() {
		this.twitterPreferences.edit().clear().commit();
	}

	private class InicializaConexaoTwitterTask extends AsyncTask<Void, Void, OAuthToken> {

		@Override
		protected void onPreExecute() {
			mostrarMensagemProgresso("Inicializando conexão Twitter...");
		}

		@Override
		protected OAuthToken doInBackground(Void... params) {
			return connectionFactory.getOAuthOperations().fetchRequestToken(getUrlRetorno(), null);
		}

		@Override
		protected void onPostExecute(OAuthToken requestToken) {
			fecharMensagemProgresso();
			mostrarTelaDeAutenticacao(requestToken);
		}

	}

	private class FinalizaConexaoTwitterTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			mostrarMensagemProgresso("Finalizando conexão Twitter...");
		}

		@Override
		protected Void doInBackground(String... params) {
			if (params.length <= 0) {
				return null;
			}

			final String parametroVerificador = params[0];

			OAuthToken tokenRequisicao = getTokenRequisicao();

			AuthorizedRequestToken autorizacao = new AuthorizedRequestToken(tokenRequisicao, parametroVerificador);

			OAuthToken tokenAcesso = connectionFactory.getOAuthOperations().exchangeForAccessToken(
					autorizacao, null);

			deletarTokenRequisicao();

			Connection<Twitter> conexao = connectionFactory.createConnection(tokenAcesso);

			SocialService.adicionarConexao(conexao);

			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			fecharMensagemProgresso();

			irParaProximaTela();
			
		}

	}
	
	private void irParaProximaTela() {
		if (convite) {
			iniciarActivity(ContatoTwitterActivity.class, bundle);
		} else {
			iniciarActivity(PostTwitterActivity.class, bundle);
		}
	}
	
}