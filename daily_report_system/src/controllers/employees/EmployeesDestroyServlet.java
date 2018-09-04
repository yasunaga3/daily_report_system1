package controllers.employees;

import java.io.IOException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;
import utils.DBUtil;

/**
 * Servlet implementation class EmployeesDestroyServlet
 */
@WebServlet("/employees/destroy")
public class EmployeesDestroyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesDestroyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		Integer emp_id = (Integer) session.getAttribute("employee_id");
		String _token = request.getParameter("_token");

		if (_token != null && _token.equals(session.getId())) {
			EntityManager em = DBUtil.createEntityManager();
			Employee e = em.find(Employee.class, emp_id);
            e.setDelete_flag(1); // 削除フラグを立てる
            e.setUpdated_at(new Timestamp(System.currentTimeMillis()));
            // 更新処理
            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();
            request.getSession().setAttribute("flush", "削除が完了しました。");
            // indexサーブレットへリダイレクト
            response.sendRedirect(request.getContextPath() + "/employees/index");
		}
	}

}
