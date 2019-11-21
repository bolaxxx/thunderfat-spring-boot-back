package com.thunderfat.springboot.backend.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.thunderfat.springboot.backend.model.entity.Usuario;
import com.thunderfat.springboot.backend.model.service.IUserService;
@Component
public class InfoAdicionalToken implements TokenEnhancer{
@Autowired
private IUserService usuarioService;
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		// TODO Auto-generated method stub
		Usuario user =usuarioService.findByEmail(authentication.getName());
		Map<String, Object> info= new  HashMap<String, Object>();
		info.put("info andicional:", "esta es la info adicional del usuario "+authentication.getName());
		info.put("id_usuario", ""+user.getId());
		info.put("rol", ""+user.getRoles().toString());
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		return accessToken;
	}

}
