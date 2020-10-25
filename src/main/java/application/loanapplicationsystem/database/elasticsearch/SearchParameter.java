package application.loanapplicationsystem.database.elasticsearch;

import org.elasticsearch.index.query.BoolQueryBuilder;

public interface SearchParameter {

	public BoolQueryBuilder apply(BoolQueryBuilder qb);
}
