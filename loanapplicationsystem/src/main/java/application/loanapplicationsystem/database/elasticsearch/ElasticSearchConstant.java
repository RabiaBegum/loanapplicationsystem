package application.loanapplicationsystem.database.elasticsearch;

import org.elasticsearch.common.unit.TimeValue;

public class ElasticSearchConstant {

	/**
	 * One Hour {@link TimeValue} instance to keep alive search context.
	 */
	public static final TimeValue KEEP_ALIVE_ONE_HOUR = new TimeValue(60000 * 60);
	
	public static final String COMMA = ",";

	public static final String INDEX_LOAN = "loan";
	
	public static final String TYPE_ENTITY = "entity";

	public static final String PROPERTIES = "properties";

	public static final String TYPE = "type";

	public static final String KEYWORD = "keyword";

	public static final String TEXT = "text";

	public static final String ANALYZER = "analyzer";

	public static final String CUSTOM_LOWERCASE_STEMMED = "custom_lowercase_stemmed";

	public static final String DOUBLE = "double";
	
	public static final String BOOLEAN = "boolean";
}
