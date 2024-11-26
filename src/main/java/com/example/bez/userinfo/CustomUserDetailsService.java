package com.example.bez.userinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Загрузка пользователя из LDAP
        com.example.bez.userinfo.MyUser user = ldapTemplate.findOne(
                LdapQueryBuilder.query().where("uid").is(username),
                com.example.bez.userinfo.MyUser.class
        );

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Определение ролей пользователя
        String role = determineRole(user.getDn().toString());
        return new User(user.getUsername(), user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
    }

    private String determineRole(String dn) {
        if (dn.contains("ou=UVA-U,OU=8,dc=test,dc=bpab,dc=internal") || dn.contains("OU=DK-U,OU=8,dc=test,dc=bpab,dc=internal")) {
            return "ADMIN";
        } else {
            return "USER";
        }
    }
}