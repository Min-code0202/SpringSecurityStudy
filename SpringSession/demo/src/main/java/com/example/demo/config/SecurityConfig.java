package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // SecurityConfig 클래스가 커스텀 설정 클래스로 등록됨
@EnableWebSecurity // SecurityConfig 클래스가 스프링 시큐리티에 의해 관리됨
public class SecurityConfig {
    /*
     * 스프링 시큐리티는 BCrypt Password Encoder를 제공하고 권장한다.
     * 해당 클래스는 단방향 해시 암호화이며 비밀번호가 자동으로 암호화된다.
     * */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        /*
        * 인가 동작은 상단부터 진행되기 때문에 순서에 유의
        * 접근 제한은 제한 범위가 큰 경우 부터 작은 경우로 순서대로 하도록 한다.
        * */
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/loginProc", "/join", "/joinProc").permitAll() // 로그인, 회원가입 페이지 모두 접근 허용
                        .requestMatchers("/").permitAll() // 메인 페이지 접근 허용
                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER") // my 페이지 ADMIN, USER 접근 가능
                        .requestMatchers("/admin").hasRole("ADMIN") // admin 페이지 관리자만 허용
                        .anyRequest().authenticated()); // 나머지 경로는 authenticated(로그인)한 사용자만 접근 허용

//        http
//                .csrf((auth) -> auth.disable()); // csrf 설정 비활성화

        http
                .formLogin((auth) -> auth
                        .loginPage("/login") // 로그인 페이지 경로, 로그인 페이지로 리다이렉션
                        .loginProcessingUrl("/loginProc").permitAll()); // 스프링 시큐리티가 해당 경로로 로그인 진행, default: "/login"

        /*
        * csrf를 enable하면 GET 방식으로 로그아웃이 불가능하다.
        * 하지만 아래와 같이 설정하면 GET 방식으로도 로그아웃이 가능함
        **/
        http
                .logout((auth) -> auth.logoutUrl("/logout")
                        .logoutSuccessUrl("/"));

        // 중복 로그인 설정
        http
                .sessionManagement((auth) -> auth
                        .maximumSessions(1) // 하나의 아이디에 대한 다중 로그인 허용 개수 : 현재는 1
                        .maxSessionsPreventsLogin(true)); // 다중 로그인 개수를 초과할 경우 처리 | true: 초과시 새로운 로그인 차단, false: 초과시 기존 세션 하나 삭제

        /*
        * none() : 로그인 시 세션 정보 변경 안함
          newSession() : 로그인 시 세션 새로 생성
          changeSessionId() : 로그인 시 동일한 세션에 대한 id 변경
        * */
        http
                .sessionManagement((auth) -> auth
                        .sessionFixation().changeSessionId());


        return http.build();
    }
}
