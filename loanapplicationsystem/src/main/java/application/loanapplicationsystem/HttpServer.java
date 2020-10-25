package application.loanapplicationsystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import application.loanapplicationsystem.services.LoanService;

public class HttpServer {

	private static final Logger logger = LogManager.getLogger(HttpServer.class);
	private static final int PORT = 8077;

	private static LoanService loanService;

	public HttpServer(LoanService loanService) {
		HttpServer.loanService = loanService;
	}

	/**
	 * Bu method serveri başlatır.
	 */
	public void start() {
		Server server;
		try {
			server = new Server();
			server.setHandler(prepareHandlers());
			ServerConnector connector = createConnector(server);
			logger.info(String.format("started: { port: %s }", connector.getPort()));
			server.start();
		} catch (Exception e) {
			logger.fatal("unexpected error occurred", e);
		}
	}

	private static HandlerList prepareHandlers() {
		HandlerList handlers = new HandlerList();
		handlers.addHandler(prepareServletContextHandler());
		return handlers;
	}

	public static final String JERSEY_SERVER_PROVIDER_CONFIG = "jersey.config.server.provider.packages";

	public static final String SERVICE_PACKAGE = "application.loanapplicationsystem.services";

	public static final String API_PATH = "/api/*";

	public static final String COM_SUN_JERSEY_API_JSON_POJO_MAPPING_FEATURE = "com.sun.jersey.api.json.POJOMappingFeature";

	private static ServletContextHandler prepareServletContextHandler() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		// jersey servlet holder definition
		ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, API_PATH);
//		jerseyServlet.setInitParameter("dirAllowed","true");
		jerseyServlet.setInitOrder(1);
		// jersey servislerinin aranacağı package
		jerseyServlet.setInitParameter(JERSEY_SERVER_PROVIDER_CONFIG, SERVICE_PACKAGE);
		// pojo classlarının servislerde kullanılmasını sağlayan constant
		jerseyServlet.setInitParameter(COM_SUN_JERSEY_API_JSON_POJO_MAPPING_FEATURE, "true");

		// default servlet holder definition
		ServletHolder defaultServletHolder = context.addServlet(DefaultServlet.class, "/*");
		defaultServletHolder.setInitParameter("gzip", "true");
		defaultServletHolder.setInitParameter("precompressed", "true");

		return context;
	}


	/**
	 * <p>
	 * Bir connector nesnesi yaratıp servera belirlenen porttan bağlanır.
	 * </p>
	 * 
	 * @param server connector nesnesinin bağlandığı serveri temsil eder.
	 * @return Dönen nesne servera bağlanan connector nesnesini temsil eder.
	 */
	private static ServerConnector createConnector(Server server) {
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(PORT);
		server.addConnector(connector);
		return connector;
	}
}
