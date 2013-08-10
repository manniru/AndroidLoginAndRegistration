package com.fiap.activities;
//Teste Git!
// Teste 2
// teste 3
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class BaseActivity extends Activity {

	private ProgressDialog progressDialog;
	private boolean destroyed;

	public void iniciarActivity(Class<? extends Activity> activity, Bundle bundle) {
		Intent i = new Intent(this, activity);
		if (bundle != null) {
			i.putExtras(bundle);
		}
		
		
		this.startActivity(i);
		
		
	}
	
	public void mostrarMensagem(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.destroyed = true;
	}
	
	public void mostrarMensagemProgresso(CharSequence message) {
		this.progressDialog = new ProgressDialog(this);
		this.progressDialog.setIndeterminate(true);
		this.progressDialog.setMessage(message);
		this.progressDialog.show();
	}

	public void fecharMensagemProgresso() {
		if (this.progressDialog != null && !this.destroyed) {
			this.progressDialog.dismiss();
		}
	}
	
}
