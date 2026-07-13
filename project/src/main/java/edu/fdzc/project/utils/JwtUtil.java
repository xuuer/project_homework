package edu.fdzc.project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类 - 修复版本
 */
public class JwtUtil {

    // 令牌标识符
    private static final String HEADER = "Authorization";

    // 令牌有效期（默认1天）
    private static final Long ACCESS_EXPIRE = 60L * 60 * 24;

    // 令牌密钥
    private static final String SECRET = "Diana_is_invited_to_tea_with_tragic_results";

    // 加密后的密钥
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 创建JWT令牌
     */
    public static String createToken(Long userId, String role) {
        return Jwts.builder()
                // header
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                // payload
                .claim("id", userId)
                .claim("role", role)
                .id(UUID.randomUUID().toString())
                .expiration(Date.from(Instant.now().plusSeconds(ACCESS_EXPIRE)))
                // signature
                .signWith(KEY, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌 - 修改为public方法
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            // 可以记录具体的解析错误
            System.err.println("JWT解析失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 获取JWT令牌
     */
    public static String getToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) {
            throw new RuntimeException("无法从当前线程获取属性");
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader(HEADER);
    }

    /**
     * 获取用户ID - 添加null检查
     */
    public static Long getUserId() {
        String token = getToken();
        if(token == null) {
            throw new RuntimeException("未携带token！或头属性名不为：" + HEADER);
        }
        Claims claims = parseToken(token);
        if(claims == null) {
            throw new RuntimeException("Token无效或已过期，请重新登录");
        }
        return Long.parseLong(String.valueOf(claims.get("id")));
    }

    /**
     * 获取用户角色 - 添加null检查
     */
    public static String getUserRole() {
        String token = getToken();
        if(token == null) {
            throw new RuntimeException("未携带token！或头属性名不为：" + HEADER);
        }
        Claims claims = parseToken(token);
        if(claims == null) {
            throw new RuntimeException("Token无效或已过期，请重新登录");
        }
        return String.valueOf(claims.get("role"));
    }
}