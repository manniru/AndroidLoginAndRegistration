package com.fiap.activities;

import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fiap.service.SocialService;

public class LoginFacebookActivity extends BaseWebActivity {

	private Bundle bundle;
	private FacebookConnectionFactory connectionFactory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		bundle = getIntent().getExtras();
		
		prepararTela();

		connectionFactory = (FacebookConnectionFactory) SocialService.getConnectionFactory(Facebook.class);
	}

	@Override
	public void onStart() {
		super.onStart();

		// display the Facebook authorization page
		getWebView().loadUrl(getURLAutorizacao());
	}
	
	private void prepararTela() {
		getWebView().getSettings().setJavaScriptEnabled(true);

		getWebView().setWebViewClient(new FacebookOAuthWebViewClient());
	}

	private String getURLAutorizacao() {
		String urlRetorno = getString(R.string.facebook_oauth_callback_url);
		String escopo = getString(R.string.facebook_scope);

		OAuth2Parameters parametros = new OAuth2Parameters();
		parametros.setRedirectUri(urlRetorno);
		parametros.setScope(escopo);
		parametros.add("display", "touch");
		
		return connectionFactory.getOAuthOperations().buildAuthorizeUrl(GrantType.IMPLICIT_GRANT, parametros);
	}

	private class FacebookOAuthWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Uri uri = Uri.parse(url);
			String uriFragment = uri.getFragment();
			
			if (uriFragment != null && uriFragment.startsWith("access_token=")) {
				
				String[] parametros = uriFragment.split("&");
				String[] paramteroTokenAcesso = parametros[0].split("=");
				String tokenAcesso = paramteroTokenAcesso[1];

				AccessGrant autorizacoesAcesso = new AccessGrant(tokenAcesso);

				Connection<Facebook> conexao = connectionFactory.createConnection(autorizacoesAcesso);

				SocialService.adicionarConexao(conexao);
				
				irParaProximaTela();
				
			}

			if (uri.getQueryParameter("error") != null) {

				String erro = uri.getQueryParameter("error_description").replace("+", " ");
				
				mostrarMensagem(erro);
				
				irParaTelaInicial();

			}
		}
	}

	private void irParaProximaTela() {
		iniciarActivity(PostFacebookActivity.class, bundle);
	}

	private void irParaTelaInicial() {
		iniciarActivity(GPSActivity.class, null);
	}

}