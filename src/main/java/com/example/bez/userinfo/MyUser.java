package com.example.bez.userinfo;

import javax.naming.Name;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entry(base = "ou=users,dc=test,dc=bpab,dc=internal", objectClasses = { "inetOrgPerson", "organizationalPerson", "person", "top" })
public class MyUser {

    @Id
    private Name dn;

    @Attribute(name = "uid")
    private String username;

    @Attribute(name = "userPassword")
    private String password;

    @Attribute(name = "cn")
    private String commonName;

    @Attribute(name = "sn")
    private String surname;

    @Attribute(name = "givenName")
    private String givenName;

    @Attribute(name = "displayName")
    private String displayName;

  
}