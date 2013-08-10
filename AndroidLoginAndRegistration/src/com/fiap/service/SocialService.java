package com.fiap.service;

import java.util.List;

import org.springframework.security.crypto.encrypt.AndroidEncryptors;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import android.content.Context;

import com.fiap.activities.MainApplication;
import com.fiap.activities.R;
import com.fiap.model.Contato;

public abstract class SocialService {

    private static ConnectionFactoryRegistry connectionFactoryRegistry;
    private static SQLiteConnectionRepository connectionRepository; 

	public abstract List<Contato> getListaContatos();
	
	public abstract void enviarMensagem(List<Contato> contatos, String mensagem);
	
	public abstract void postarNoMural(String mensagem);
	
	public static void init(Context context) {
		connectionFactoryRegistry = new ConnectionFactoryRegistry();
		connectionFactoryRegistry.addConnectionFactory(new FacebookConnectionFactory(
				MainApplication.getContext().getString(R.string.facebook_app_id), 
				MainApplication.getContext().getString(R.string.facebook_app_secret)));
		connectionFactoryRegistry.addConnectionFactory(new TwitterConnectionFactory(
				MainApplication.getContext().getString(R.string.twitter_consumer_key), 
				MainApplication.getContext().getString(R.string.twitter_consumer_key_secret)));

		SQLiteConnectionRepositoryHelper repositoryHelper = new SQLiteConnectionRepositoryHelper(context);
		connectionRepository = new SQLiteConnectionRepository(repositoryHelper,
				connectionFactoryRegistry, AndroidEncryptors.text("password", "5c0744940b5c369b"));
	}

	public static <E> void adicionarConexao(Connection<E> connection) {
		try {
			connectionRepository.addConnection(connection);
		} catch (DuplicateConnectionException e) {
			// connection already exists in repository!
		}
	}

	public static <E> E getApi(Class<E> apiType) {
		return connectionRepository.findPrimaryConnection(apiType).getApi();
	}
	
	public static <E> ConnectionFactory<E> getConnectionFactory(Class<E> apiType) {
		return (ConnectionFactory<E>) connectionFactoryRegistry.getConnectionFactory(apiType);
	}
	
}
