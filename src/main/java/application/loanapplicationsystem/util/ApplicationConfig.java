package application.loanapplicationsystem.util;

import java.util.StringJoiner;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ApplicationConfig {

	private static final String LOAN_APPLICATON_SYSTEM_KEY = "loanapplicationsystem";

	private static ApplicationConfig instance = null;

	private Config config;

	public static ApplicationConfig getInstance() {
		if (instance == null) {
			instance = new ApplicationConfig();
		}
		return instance;
	}

	private ApplicationConfig() {
		config = ConfigFactory.load();
	}

	protected ApplicationConfig(String fileName) {
		config = ConfigFactory.load(fileName);
	}

	public void loadConfig(String fileName) {
		config = ConfigFactory.load(fileName);
	}

	private static StringJoiner newApplicationConfigJoiner() {
		StringJoiner stringJoiner = new StringJoiner(".");
		return stringJoiner.add(LOAN_APPLICATON_SYSTEM_KEY);
	}

	private static StringJoiner createElasticSearchKey() {
		return newApplicationConfigJoiner().add("elasticsearch");
	}

	public String getElasticSearchHosts() {
		return config.getString(createElasticSearchKey().add("hosts").toString());
	}

	public String getElasticSearchPassword() {
		return config.getString(createElasticSearchKey().add("password").toString());
	}

	public int getElasticSearchPort() {
		return config.getInt(createElasticSearchKey().add("port").toString());
	}

	public String getElasticSearchDataFolder() {
		return config.getString(createElasticSearchKey().add("dataFolder").toString());
	}

	public String getElasticSearchClusterName() {
		return config.getString(createElasticSearchKey().add("cluster.name").toString());
	}

	public String getDBType() {
		return config.getString(newApplicationConfigJoiner().add("database.type").toString());
	}
}
