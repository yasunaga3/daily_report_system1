package controllers.reports;

import java.io.IOException;
import java.sql.Date;
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
import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsCreateServlet
 */
@WebServlet("/reports/create")
public class ReportsCreateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsCreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Stringで受け取った日付を Date 型へ変換する処理
		// 日付欄をわざと未入力にした場合は当日の日付を入れる
		Date report_date = new Date(System.currentTimeMillis());
		String rd_str = request.getParameter("report_date");
		if(rd_str != null && !rd_str.equals("")) {
		    report_date = Date.valueOf(request.getParameter("report_date"));
		}

		// sessionとrequestスコープ内のsessionIDを取得する
		HttpSession session = request.getSession();
		String _token = (String) request.getParameter("_token");

		// sessionスコープ内のsessionIDとrequestスコープ内のsessionIDが一致すれば
		// 日報の登録処理を行う
		if (_token != null && _token.equals(session.getId())) {
			EntityManager em = DBUtil.createEntityManager();
			// 日報情報の取得
			Report r = new Report();
			r.setEmployee((Employee) session.getAttribute("login_employee"));
			r.setReport_date(report_date);
            r.setTitle(request.getParameter("title"));
            r.setContent(request.getParameter("content"));
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            r.setCreated_at(currentTime);
            r.setUpdated_at(currentTime);

            // バリデーション
            List<String> errors = ReportValidator.validate(r);
            if(errors.size() > 0) {
                em.close();
                // 	リクエストスコープへ情報を格納する
                request.setAttribute("_token", session.getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);
                // ディスパッチ
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/new.jsp");
                rd.forward(request, response);
            } else {
            	// バリデーションOKならばDBに追加する
                em.getTransaction().begin();
                em.persist(r);
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "登録が完了しました。");
                // ReportIndexServletへリダイレクト
                response.sendRedirect(request.getContextPath() + "/reports/index");
            }
		}
	}

}
