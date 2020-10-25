package application.loanapplicationsystem.database.elasticsearch;

import application.loanapplicationsystem.util.ApplicationConfig;

public class ElasticSearchConfig {

	/**
	 * {@link ApplicationConfig} instance.
	 */
	private static final ApplicationConfig APP_CONFIG = ApplicationConfig.getInstance();

	private int port;

	private String hosts = "";

	public ElasticSearchConfig(int port, String... hosts) {
		this.port = port;
		for (int i = 0; i < hosts.length; i++) {
			if (i == 0) {
				this.hosts += hosts[i];
			} else {
				this.hosts += "," + hosts[i];
			}
		}
	}
	
	public ElasticSearchConfig() {
		this(APP_CONFIG.getElasticSearchPort(), APP_CONFIG.getElasticSearchHosts());
	}

	public String getHosts() {
		return hosts;
	}

	public int getPort() {
		return port;
	}
}
