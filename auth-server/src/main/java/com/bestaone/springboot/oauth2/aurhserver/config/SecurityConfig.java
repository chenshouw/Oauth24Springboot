package com.bestaone.springboot.oauth2.aurhserver.config;

import com.bestaone.springboot.oauth2.aurhserver.config.smscode.SmsCodeAuthenticationSecurityConfig;
import com.bestaone.springboot.oauth2.aurhserver.config.validatecode.ValidateCodeSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig(){
        return new SmsCodeAuthenticationSecurityConfig();
    }

    @Bean
    public ValidateCodeSecurityConfig validateCodeSecurityConfig(){
        return new ValidateCodeSecurityConfig();
    }

    @Bean
    public UserDetailsService simpleUserDetailsService(){
        return new SimpleUserDetailsService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(simpleUserDetailsService());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.userDetailsService(userDetailsService());
        http.csrf().disable();

        http .apply(smsCodeAuthenticationSecurityConfig()); //开启短信登陆功能
        http .apply(validateCodeSecurityConfig());//开启验证码功能

        http.formLogin()
                .loginPage("/signin").loginProcessingUrl("/signin/form").defaultSuccessUrl("/index")
                .and()
                .logout().logoutUrl("/signout").logoutSuccessUrl("/signin")
                .and()
                .authorizeRequests()
                .antMatchers("/signin", "/mobile/signin/form","/code/image","/code/mobile").permitAll()
                .antMatchers("/oauth/**").permitAll()
                .antMatchers("/user/**").hasAnyRole("USER","ADMIN")
                .anyRequest().authenticated();

    }

}