package com.fiap.activities;

import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class BaseWebActivity extends BaseActivity {

	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		this.webView = new WebView(this);
		setContentView(webView);

		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				BaseWebActivity.this.setTitle("Carregando...");
				BaseWebActivity.this.setProgress(progress * 100);
				if (progress == 100) {
					BaseWebActivity.this.setTitle(R.string.tela_inicial);
				}
			}
		});
	}
	
	public WebView getWebView() {
		return webView;
	}
	
}
