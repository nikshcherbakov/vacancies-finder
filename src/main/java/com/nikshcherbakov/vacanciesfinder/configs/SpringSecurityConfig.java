package com.nikshcherbakov.vacanciesfinder.configs;

import com.nikshcherbakov.vacanciesfinder.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final DataSource dataSource;

    private final UserService userService;

    public SpringSecurityConfig(BCryptPasswordEncoder bCryptPasswordEncoder, DataSource dataSource, UserService userService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.dataSource = dataSource;
        this.userService = userService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/", "/index", "/signup", "/accountconfirm",
                            "/css/**", "/images/**", "/js/**", "/static/favicon.ico").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .defaultSuccessUrl("/account", true)
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll()
                .and()
                    .exceptionHandling().accessDeniedPage("/accessForbidden");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication().dataSource(dataSource)
                    .usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username=?")
                    .authoritiesByUsernameQuery("SELECT username, name\n" +
                            "FROM users\n" +
                            "INNER JOIN users_roles ON users.id = users_roles.users_id\n" +
                            "INNER JOIN roles ON users_roles.roles_id = roles.id\n" +
                            "WHERE username = ?")
                    .passwordEncoder(bCryptPasswordEncoder)
                .and()
                    .userDetailsService(userService);

    }

}
