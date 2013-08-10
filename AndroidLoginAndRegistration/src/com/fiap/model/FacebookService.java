//package com.fiap.model;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.social.facebook.api.Facebook;
//import org.springframework.social.facebook.api.Reference;
//
//import android.spring.social.service.SocialService;
//import android.spring.social.service.exception.SocialServiceException;
//
//public class FacebookService extends SocialService {
//
//	private Facebook facebook;
//
//	public FacebookService() {
//		this.facebook = getApi(Facebook.class);
//	}
//	
//	@Override
//	public List<Contato> getListaContatos() {
//		if (facebook != null) {
//			List<Reference> references = facebook.friendOperations().getFriends();
//			List<Contato> contatos = new ArrayList<Contato>();
//			for (Reference reference : references) {
//				contatos.add(new Contato(
//						reference.getId(), 
//						reference.getName(),
//						reference.getName()));
//			}
//			return contatos;
//		} else {
//			throw new SocialServiceException("Facebook desconectado");
//		}
//	}
//
//	@Override
//	public void enviarMensagem(List<Contato> contatos, String mensagem) {
//		throw new SocialServiceException("Facebook API não implementa mensagem privada");
//	}
//
//	@Override
//	public void postarNoMural(String mensagem) {
//		if (facebook != null) {
//			facebook.feedOperations().updateStatus(mensagem);
//		} else {
//			throw new SocialServiceException("Facebook desconectado");
//		}
//	}
//	
//}
