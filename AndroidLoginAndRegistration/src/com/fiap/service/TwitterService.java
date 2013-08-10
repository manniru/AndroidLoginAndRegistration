package com.fiap.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;

import com.fiap.model.Contato;
import com.fiap.service.exception.SocialServiceException;


public class TwitterService extends SocialService {

	private Twitter twitter;

	public TwitterService() {
		this.twitter = getApi(Twitter.class);
	}
	
	@Override
	public List<Contato> getListaContatos() {
		if (twitter != null) {
			List<TwitterProfile> references = twitter.friendOperations().getFriends();
			List<Contato> contatos = new ArrayList<Contato>();
			for (TwitterProfile reference : references) {
				contatos.add(new Contato(
						Long.toString(reference.getId()), 
						reference.getName(),
						reference.getScreenName()));
			}
			return contatos;
		} else {
			throw new SocialServiceException("Twitter desconectado");
		}
	}

	@Override
	public void enviarMensagem(List<Contato> contatos, String mensagem) {
		if (twitter != null) {
			for (Contato contato : contatos) {
				twitter.directMessageOperations().sendDirectMessage(contato.getScreenName(), mensagem); 
			}
		} else {
			throw new SocialServiceException("Twitter desconectado");
		}
	}

	@Override
	public void postarNoMural(String mensagem) {
		if (twitter != null) {
			twitter.timelineOperations().updateStatus(mensagem);
		} else {
			throw new SocialServiceException("Twitter desconectado");
		}
	}

}
