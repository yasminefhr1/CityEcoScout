package com.ensa.CityScout.security.oauth2;

import com.ensa.CityScout.entity.Utilisateurs;
import com.ensa.CityScout.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oauth2User.getAttributes());
        
        Utilisateurs user = utilisateurRepository.findByEmail(userInfo.getEmail())
            .orElseGet(() -> registerNewUser(userInfo));

        return oauth2User;
    }

    private Utilisateurs registerNewUser(GoogleOAuth2UserInfo userInfo) {
        Utilisateurs user = new Utilisateurs();
        
        // Splitting the name into first and last name
        String[] nameParts = userInfo.getName().split(" ");
        user.setPrenom(nameParts[0]);
        user.setNom(nameParts.length > 1 ? nameParts[1] : "");
        
        user.setEmail(userInfo.getEmail());
        user.setUsername(userInfo.getEmail()); // Using email as username
        user.setPays("Not Specified"); // Default value
        // Note: password is not needed for OAuth2 users
        
        return utilisateurRepository.save(user);
    }
}
