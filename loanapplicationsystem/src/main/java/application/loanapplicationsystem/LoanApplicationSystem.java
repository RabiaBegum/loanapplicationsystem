package application.loanapplicationsystem;

import java.io.IOException;

import application.loanapplicationsystem.database.elasticsearch.ElasticSearchClient;
import application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant;
import application.loanapplicationsystem.services.LoanService;

/**
 * Hello world!
 *
 */
public class LoanApplicationSystem {
	public static void main(String[] args) throws IOException {
//		ElasticSearchClient elasticSearchClient = ElasticSearchClient.getInstance();
//		elasticSearchClient.createIndexesIfNotExists(ElasticSearchConstant.INDEX_LOAN);
		System.out.println("Hello World!");
		LoanService loanService = new LoanService();
		HttpServer httpServer = new HttpServer(loanService);
		httpServer.start();
		System.err.println("safasf");
	}
}
