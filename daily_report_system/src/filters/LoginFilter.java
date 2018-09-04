package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("/*")
public class LoginFilter implements Filter {

    /**
     * Default constructor.
     */
    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// パス情報の取得
        String context_path = ((HttpServletRequest)request).getContextPath();
        String servlet_path = ((HttpServletRequest)request).getServletPath();

        // cssフォルダの読み込みの場合を除外する
        if(!servlet_path.matches("/css.*")) {
            HttpSession session = ((HttpServletRequest)request).getSession();
            Employee e = (Employee)session.getAttribute("login_employee");
            // /login 以外のページにアクセスした場合、ログイン状態でなければ /login ページに強制的にリダイレクトさせます。
            if(!servlet_path.equals("/login")) {
                if(e == null) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/login");
                    return;
                }
                // 従業員管理（/emploees）のページにアクセスした場合、ログインしている従業員情報の admin_flag をチェックし、
                // 一般従業員であればトップページへリダイレクトさせるようにしています
                if(servlet_path.matches("/employees.*") && e.getAdmin_flag() == 0) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/");
                    return;
                }
            // ログインしているのにログインページを表示させる理由がないので、
            //そのようなアクセスがあった場合はトップページへリダイレクトさせます。
            } else {
                if(e != null) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/");
                    return;
                }
            }
        }
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
