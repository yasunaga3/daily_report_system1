package controllers.employees;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;
import models.validators.EmployeeValidator;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class EmployeesUpdateServlet
 */
@WebServlet("/employees/update")
public class EmployeesUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// session_idとsessionの取得
		String _token = request.getParameter("_token");	// リクエストスコープ内のsession_id
		HttpSession session = request.getSession(); 	// sessionの取得
        Integer emp_id = (Integer)(session.getAttribute("employee_id"));
        // 従業員情報の取得
		String emp_code = request.getParameter("code");
		String emp_name = request.getParameter("name");
        String emp_pass = request.getParameter("password");
        int admin_flg = Integer.parseInt(request.getParameter("admin_flag"));

        // session_idが一致ならば従業員情報更新
        if(_token != null && _token.equals(session.getId())) {
            EntityManager em = DBUtil.createEntityManager();
            Employee e = em.find(Employee.class, emp_id);
            // 従業員コード重複チェック
            Boolean code_duplicate_check = true;
            if (e.getCode().equals(emp_code)) {
            	code_duplicate_check = false;
			} else {
				e.setCode(emp_code);
			}
            // パスワード変更チェック
            Boolean password_check_flag = true;
            if (emp_pass == null || emp_pass.equals("")) {
            	password_check_flag = false;
			} else {
				String salt = (String)this.getServletContext().getAttribute("salt"); // 暗号化方式の取得
				String encryptedPass = EncryptUtil.getPasswordEncrypt(emp_pass, salt); // 暗号化済パスワード
				e.setPassword(encryptedPass);
			}
            // 従業員情報の設定
            e.setName(emp_name);
            e.setAdmin_flag(admin_flg);
            e.setUpdated_at(new Timestamp(System.currentTimeMillis()));
            e.setDelete_flag(0);
            // バリデーション
            List<String> errors = EmployeeValidator.validate(e, code_duplicate_check, password_check_flag);
            if (errors.size() > 0) {
                em.close();
                // フラッシュ(失敗)用情報の設定
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("employee", e);
                request.setAttribute("errors", errors);
                // edit.jspへ差し戻し
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
                rd.forward(request, response);
			} else {
				// 従業員情報の更新
                em.getTransaction().begin();
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "更新が完了しました。");
                request.getSession().removeAttribute("employee_id");
                // indexサーブレットへリダイレクト
                response.sendRedirect(request.getContextPath() + "/employees/index");
			}
        }
	}

}
