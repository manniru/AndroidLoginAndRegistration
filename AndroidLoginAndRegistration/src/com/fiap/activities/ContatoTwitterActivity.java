package com.fiap.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.fiap.model.Contato;
import com.fiap.service.TwitterService;

public class ContatoTwitterActivity extends BaseActivity {
	
	private TwitterService twitterService = new TwitterService();
	
	private Bundle bundle;
	
	private List<ContatoView> contatosView;

	private Button btnEnviar;
	private Button btnCancelar;
	private ListView lstContatos;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_contato);
		
		bundle = getIntent().getExtras();
		contatosView = new ArrayList<ContatoView>();
		
		prepararTela();

		new CarregarContatosTask().execute();
	}

	private void prepararTela() {
		btnEnviar	= (Button)findViewById(R.id.button_lista_enviar);
		btnCancelar	= (Button)findViewById(R.id.button_lista_cancelar);

		lstContatos	= (ListView)findViewById(R.id.lista_escolha_contatos);
		
		btnEnviar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				new EnviarMensagemTask().execute();
			}
			
		});
		
		btnCancelar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				irParaTelaInicial();
			}
			
		});
		
	}

	private void irParaTelaInicial() {
		iniciarActivity(GPSActivity.class, bundle);
	}
    
	private class EnviarMensagemTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mostrarMensagemProgresso("Enviando mensagens. Aguarde...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<Contato> contatos = new ArrayList<Contato>();
			for (ContatoView contato : contatosView) {
				if (contato.isMarcado()) {
					contatos.add(contato.getContato());
				}
			}
			
			String mensagem = (String)bundle.getSerializable(Constants.PARAM_MENASGEM);
			
			twitterService.enviarMensagem(contatos, mensagem);
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			fecharMensagemProgresso();
			
			irParaTelaInicial();
		}

	}
	
	private class CarregarContatosTask extends AsyncTask<Void, Void, List<Contato>> {

		@Override
		protected void onPreExecute() {
			mostrarMensagemProgresso("Carregando contatos. Aguarde...");
		}

		@Override
		protected List<Contato> doInBackground(Void... params) {
			List<Contato> contatos = twitterService.getListaContatos();
			for (Contato contato : contatos) {
				contatosView.add(new ContatoView(contato));
			}
	    	
			return contatos;
		}

		@Override
		protected void onPostExecute(List<Contato> result) {
			
			lstContatos.setAdapter(new ContatoAdapter(ContatoTwitterActivity.this, contatosView));	    	
			
			fecharMensagemProgresso();
		}

	}
	
	private class ContatoAdapter extends ArrayAdapter<ContatoView> {

		public ContatoAdapter(Context context, List<ContatoView> contatos) {
			super(context, R.layout.contato_template, contatos);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater vi = ((Activity)getContext()).getLayoutInflater();
				convertView = vi.inflate(R.layout.contato_template, parent, false);
			}
			ContatoView contato = this.getItem(position);
			if (contato != null) {
				CheckBox chkMarcacao = (CheckBox) convertView.findViewById(R.id.check_contato_marcacao);
				TextView txtNome = (TextView) convertView.findViewById(R.id.text_contato_nome);

				chkMarcacao.setChecked(contato.isMarcado());
				chkMarcacao.setTag(contato);
				chkMarcacao.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton button, boolean isChecked) {
						ContatoView contato = (ContatoView) button.getTag();
						contato.setMarcado(isChecked);
					}
				});
                
				txtNome.setText(contato.getContato().getName());
			}
			return convertView;
		}
	
	}

	private class ContatoView {
		
		private Contato contato;
		private boolean marcado;
		
		public ContatoView(Contato contato) {
			this.contato = contato;
		}
		
		public Contato getContato() {
			return contato;
		}
		
		public boolean isMarcado() {
			return marcado;
		}
		
		public void setMarcado(boolean marcado) {
			this.marcado = marcado;
		}
	}
	
}