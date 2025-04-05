/*This was required because Spring Security enforced CSRF protection by default, which is not required at this stage
where we have stateless APIs (we are not handling sessions in stage 2)
So we need this class to modify that behaviour
 */
package com.bazaar.inventory_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()  // Require auth for all endpoints
                )
                .httpBasic(basic -> {})           // Enable Basic Auth
                .csrf(csrf -> csrf.disable());    // Disable CSRF for APIs

        return http.build();
    }
}