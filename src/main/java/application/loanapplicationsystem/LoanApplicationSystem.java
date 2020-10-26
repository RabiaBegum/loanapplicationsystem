package application.loanapplicationsystem;

import application.loanapplicationsystem.services.LoanService;

/**
 * Rabia Hatapoğlu
 *
 */
public class LoanApplicationSystem {
	public static final int TIMEOUT = 5000;

	public static void main(String[] args) throws Exception {
		LoanService loanService = new LoanService();
		HttpServer httpServer = new HttpServer(loanService);
		httpServer.start();
		AbstractService.addDataForTest();
		System.err.println("started server");
	}
}
