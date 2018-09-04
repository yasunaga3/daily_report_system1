package controllers.login;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// sessionスコープのフラッシュ情報を取得する
		HttpSession sessin = request.getSession();
		String flush = (String) sessin.getAttribute("flush");
		// request情報を設定する
		request.setAttribute("_token", sessin.getId());
		request.setAttribute("hasError", false);
		// sessionスコープのフラッシュ情報をrequestスコープへ移し替える
		if (flush != null && !flush.equals("")) {
			request.setAttribute("flush", flush);
			sessin.removeAttribute("flush");
		}
		// login.jspへフォワード
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
        rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				// セッションスコープとログインチェック用フラグの取得
		HttpSession session = request.getSession();
		Boolean check_result = false; // ログインチェック用フラグ
		// 従業員情報(コードとパスワード)の取得
        String code = request.getParameter("code");
        String plain_pass = request.getParameter("password");
        Employee e = null;
        // コードとパスワードの有無をチェック
        boolean hasCode = (code != null && !code.equals(""));
        boolean hasPlain_pass = (plain_pass != null && !plain_pass.equals(""));

        if ( hasCode && hasPlain_pass) {
        	EntityManager em = DBUtil.createEntityManager();
			String salt = (String)this.getServletContext().getAttribute("salt"); // 暗号化方式の取得
			String password = EncryptUtil.getPasswordEncrypt(plain_pass, salt); // 暗号化済パスワード
			try {
				e = em.createNamedQuery("checkLoginCodeAndPassword", Employee.class)
							.setParameter("code", code)
							.setParameter("pass", password)
							.getSingleResult();
			} catch (NoResultException  ex) {
				ex.printStackTrace();
			}
			em.close();
			// 該当者(コードとパスワードが一致)がいればフラグをtrue
            if(e != null) { check_result = true; }
		}

        // ログインチェック後の処理分岐
        if(!check_result) {
        	// リクエスト情報の設定
        	request.setAttribute("_token", session.getId());
            request.setAttribute("hasError", true); // エラーフラグを立てる
            // login.jspへ差し戻し
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
            rd.forward(request, response);
        } else {
            session.setAttribute("login_employee", e);
            // TopPageIndexServletへリダイレクト
            request.getSession().setAttribute("flush", "ログインしました。");
            response.sendRedirect(request.getContextPath() + "/");
        }
	}

}
