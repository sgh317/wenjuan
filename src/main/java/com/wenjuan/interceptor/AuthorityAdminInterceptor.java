package com.wenjuan.interceptor;

import com.wenjuan.controller.AdminController;
import com.wenjuan.dao.AdminRecordMapper;
import com.wenjuan.model.AdminRecord;
import com.wenjuan.model.User;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

public class AuthorityAdminInterceptor implements HandlerInterceptor {

    /**
     * 检测用户登录访问权限
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setCharacterEncoding("UTF-8");
        User user = (User) request.getSession().getAttribute(AdminController.SESSION_KEY_ADMIN_USER);
        // User user = User.getCurrentUser();
        if (user == null || !user.isAdmin()) {
            String redirectURL = request.getRequestURI();
            String queryString = request.getQueryString();
            if (!StringUtils.isEmpty(queryString)) {
                redirectURL += "?" + queryString;
            }
            response.sendRedirect("/vcbbs/page/Login.jsp?redirectURL=" + URLEncoder.encode(redirectURL));
            return false;
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
