package com.example.bez;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;

@Service
public class UserService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public User findUserByUsername(String username) {
        return ldapTemplate.findOne(LdapQueryBuilder.query().where("uid").is(username), User.class);
    }

    public String determineRole(Name userDn) {
        String userDnString = userDn.toString().toLowerCase();
        if (userDnString.contains("ou=UVA-U,ou=8,dc=test,dc=bpab,dc=internal") ||
            userDnString.contains("ou=DK-U,ou=8,dc=test,dc=bpab,dc=internal")) {
            return "ROLE_ADMIN";
        } else {
            return "ROLE_USER";
        }
    }
}