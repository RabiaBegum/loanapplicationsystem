package application.loanapplicationsystem.database;

import application.loanapplicationsystem.database.elasticsearch.ElasticSearchOperation;
import application.loanapplicationsystem.util.ApplicationConfig;

public class AbstractDBFactory {

	private static final String ELASTICSEARCH = "elasticsearch";

	public AbstractDBOperation getInstance() {
		String dbType = ApplicationConfig.getInstance().getDBType();
		if (ELASTICSEARCH.toLowerCase().equals(dbType.toLowerCase())) {
			return new ElasticSearchOperation();
		}
		return new ElasticSearchOperation();
	}
}
