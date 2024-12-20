package com.example.bez.config;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.example.bez.RequestLoggingFilter;
import com.example.bez.jwt.JwtRequestFilter;
import com.example.bez.userinfo.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private RequestLoggingFilter requestLoggingFilter;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/refresh-token").permitAll() // Разрешить доступ без проверки токена
               // .requestMatchers("/api/auth/user").hasAnyRole("ADMIN", "USER") // доступ для ролей admin и user
                .requestMatchers("/api/auth/**").hasAnyRole("ADMIN", "USER") // доступ для ролей admin и user
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Разрешить OPTIONS для всех запросов
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(ldapAuthenticationProvider())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint()) // Настройка обработки ошибок аутентификации
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(requestLoggingFilter, JwtRequestFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Добавляем CORS конфигурацию
        return http.build();
    }

    @Bean
    public LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {
        return (DirContextOperations dirContextOperations, String username) -> {
            String userDn = dirContextOperations.getNameInNamespace().toLowerCase();
            if ((userDn.contains("ou=UVA-U,OU=8,dc=test,dc=bpab,dc=internal".toLowerCase())
                    || userDn.contains("OU=DK-U,OU=8,dc=test,dc=bpab,dc=internal".toLowerCase()))) {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            }
        };
    }

    @Bean
    public LdapAuthenticationProvider ldapAuthenticationProvider() {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource());
        FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
            "", // Base DN for the search (empty string means search from the root)
            "(uid={0})", // Search filter
            contextSource()
        );
        authenticator.setUserSearch(userSearch);
        DefaultLdapAuthoritiesPopulator authoritiesPopulator = new DefaultLdapAuthoritiesPopulator(contextSource(), "ou=groups,dc=test,dc=bpab,dc=internal");
        return new LdapAuthenticationProvider(authenticator, authoritiesPopulator);
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource("ldap://127.0.0.1:8389");
        contextSource.setUserDn("uid=dkuser,ou=DK-U,ou=8,dc=test,dc=bpab,dc=internal");
        contextSource.setPassword("password");
        return contextSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public LdapTemplate ldapTemplate(LdapContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // Разрешить все источники
        configuration.addAllowedMethod("*"); // Разрешить все методы
        configuration.addAllowedHeader("*"); // Разрешить все заголовки
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}