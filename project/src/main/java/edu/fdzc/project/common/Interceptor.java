package edu.fdzc.project.common;

//import cn.hutool.json.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fdzc.project.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
@Slf4j
public class Interceptor implements HandlerInterceptor {

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String thoken = request.getHeader("Authorization");
//
//        if(thoken==null || JwtUtil.parseToken(thoken) == null){
//            response.setStatus(401);
//            response.setContentType("application/json:charset=utf-8");
//            Result<Object> result = Result.error(Code.UNAUTHORIZED,"请重新登录");
//
//            try (PrintWriter out = response.getWriter()) {
//                // 使用ObjectMapper将Result对象序列化为JSON字符串
//                ObjectMapper objectMapper = new ObjectMapper();
//                // 将JSON字符串写入响应输出流，发送给客户端
//                out.write(objectMapper.writeValueAsString(result));
//            }
//
//            return false;
//        }
//        return true;
//
//    }
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    // 处理OPTIONS预检请求
    if ("OPTIONS".equals(request.getMethod())) {
        response.setStatus(HttpServletResponse.SC_OK);
        return true;
    }

    // 获取请求URI
    String requestURI = request.getRequestURI();
    log.info("拦截到请求：{}", requestURI);

    // 获取Authorization头
    String token = request.getHeader("Authorization");

    // 如果没有token，返回401
    if (!StringUtils.hasText(token)) {
        log.warn("请求路径：{} 缺少Authorization头", requestURI);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\",\"data\":null}");
        return false;
    }

    try {
        // 真正验证JWT token
        if (validateToken(token)) {
            log.info("请求路径：{} 认证成功", requestURI);
            return true;
        } else {
            log.warn("请求路径：{} token验证失败", requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"登录已过期，请重新登录\",\"data\":null}");
            return false;
        }
    } catch (Exception e) {
        log.error("token验证异常：", e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"认证异常\",\"data\":null}");
        return false;
    }
}

    /**
     * 验证token的方法 - 修复版本
     */
    private boolean validateToken(String token) {
        try {
            // 使用JwtUtil真正解析和验证token
            return JwtUtil.parseToken(token) != null;
        } catch (Exception e) {
            log.warn("Token解析失败：{}", e.getMessage());
            return false;
        }
    }

}
