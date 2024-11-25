package com.example.bez.userinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;
// import org.apache.commons.lang3.StringUtils;
import javax.naming.Name;

@Service
public class UserService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public MyUser findUserByUsername(String username) {
        return ldapTemplate.findOne(LdapQueryBuilder.query().where("uid").is(username), MyUser.class);
    }

    public String determineRole(String userDn) {
        String userDnString = userDn.toString().toLowerCase();
      
        if (userDnString.contains("ou=UVA-U,ou=8,dc=test,dc=bpab,dc=internal".toLowerCase()) ||
            userDnString.contains("ou=DK-U,ou=8,dc=test,dc=bpab,dc=internal".toLowerCase())) {
            return "ROLE_ADMIN";
        } else {
            return "ROLE_USER";
        }
    }
}