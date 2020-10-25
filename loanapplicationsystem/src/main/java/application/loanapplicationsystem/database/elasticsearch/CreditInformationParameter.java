package application.loanapplicationsystem.database.elasticsearch;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;

import application.loanapplicationsystem.util.Tag;

public class CreditInformationParameter implements SearchParameter {

	private String identificationNo;

	public CreditInformationParameter(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public BoolQueryBuilder apply(BoolQueryBuilder qb) {
		TermsQueryBuilder identificationQuery = QueryBuilders.termsQuery(Tag.IDENTIFICATION_NO, getIdentificationNo());
		return qb.must(identificationQuery);
	}

}
