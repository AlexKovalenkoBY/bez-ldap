package com.example.bez.userinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public MyUser findUserByUsername(String username) {
        return ldapTemplate.findOne(
                LdapQueryBuilder.query().where("uid").is(username),
                MyUser.class
        );
    }

    public String determineRole(String dn) {
        if (dn.contains("ou=UVA-U,OU=8,dc=test,dc=bpab,dc=internal") || dn.contains("OU=DK-U,OU=8,dc=test,dc=bpab,dc=internal")) {
            return "ADMIN";
        } else {
            return "USER";
        }
    }
}