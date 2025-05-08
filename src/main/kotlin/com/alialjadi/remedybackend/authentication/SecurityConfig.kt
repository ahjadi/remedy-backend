package com.alialjadi.remedybackend.authentication

import com.alialjadi.remedybackend.authentication.device.DeviceApiKeyFilter
import com.alialjadi.remedybackend.authentication.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthenticationFilter,
    private val userDetailsService: UserDetailsService,
    private val deviceApiKeyFilter: DeviceApiKeyFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            // For HTTPS
            .requiresChannel { channel ->
                channel.anyRequest().requiresSecure()
            }

            .authorizeHttpRequests {
                it
                    // Swagger UI & OpenAPI
                    .requestMatchers(
                        "/v3/api-docs",
                      "/api-docs"
                    ).permitAll()

                    // Your existing permitted endpoints
                    .requestMatchers(
                        "/auth/login",
                        "/api/prescriber/create",
                        "/api/patient/create",
                        "/api-docs"
                    ).permitAll()

                    // Device endpoints - require ROLE_DEVICE
                    .requestMatchers("/api/device/**").hasRole("DEVICE")
                    .requestMatchers("/api/prescriber/**").hasRole("PRESCRIBER")
                    .requestMatchers("/api/patient/**").hasRole("PATIENT")

                    .anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(deviceApiKeyFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    // Your other beans remain the same
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }
}