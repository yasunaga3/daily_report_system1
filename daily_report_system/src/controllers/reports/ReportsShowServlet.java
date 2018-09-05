package controllers.reports;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsShowServlet
 */
@WebServlet("/reports/show")
public class ReportsShowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsShowServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 日報情報の取得
		Integer report_id = Integer.parseInt(request.getParameter("id"));
		EntityManager em = DBUtil.createEntityManager();
        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id")));
        em.close();
        // リクエスト情報の設定
        request.setAttribute("report", r);
        request.setAttribute("_token", request.getSession().getId());
        // views/reports/show.jspへディスパッチ
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/show.jsp");
        rd.forward(request, response);
	}

}
