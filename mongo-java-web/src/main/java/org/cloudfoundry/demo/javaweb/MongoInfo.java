package org.cloudfoundry.demo.javaweb;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.MongoServiceInfo;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class MongoInfo extends HttpServlet {

	private static String TITLE = "Mongo Info";
	
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println("<head>");

        out.println("<title>" + TITLE + "</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h1>" + TITLE + "</h1>");

        String vcapServices = System.getenv().get("VCAP_SERVICES");
        out.println(vcapServices);
        out.println("<br/>");

        CloudEnvironment cloudEnvironment = new CloudEnvironment();

        for (MongoServiceInfo mongoSvcInfo : cloudEnvironment.getServiceInfos(MongoServiceInfo.class)) {
            out.println("<h3>" + mongoSvcInfo.getServiceName() + "</h3>");
            out.println("Host: " + mongoSvcInfo.getHost() + "<br/>");
    		out.println("Port: " + mongoSvcInfo.getPort() + "<br/>");
    		out.println("Db: " + mongoSvcInfo.getDatabase() + "<br/>");
			out.println("User: " + mongoSvcInfo.getUserName() + "<br/>");
			
			out.println("Collections:<br/>");
			Mongo mongo = new Mongo(mongoSvcInfo.getHost(), mongoSvcInfo.getPort());
			DB db = mongo.getDB(mongoSvcInfo.getDatabase());
			if (db.authenticate(mongoSvcInfo.getUserName(), mongoSvcInfo.getPassword().toCharArray())) {
				for (String collectionName : db.getCollectionNames()) {
		    		out.println("- " + collectionName + "<br/>");
				}
			}
			else {
				out.println("Unable to authenticate!");
			}			
        }
        out.println("</body>");
        out.println("</html>");
 	}
	
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException {
        doGet(request, response);
    }

}
