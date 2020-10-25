package application.loanapplicationsystem.services;

import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.INDEX_LOAN;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.TYPE_ENTITY;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class LoanServiceTest extends AbstractServiceTest {

	private static final String EXPECTED_BIGGER_THAN_500_RESULT = "{\"status\":200,\"message\":\"Loan information results returned successfully.\",\"result\":{\"creditResult\":\"approved\",\"creditLimit\":10000.0}}";
	private static final String EXPECTED_BIGGER_THAN_1000_RESULT = "{\"status\":200,\"message\":\"Loan information results returned successfully.\",\"result\":{\"creditResult\":\"approved\",\"creditLimit\":240000.0}}";
	private static final String EXPECTED_EQUALS_500_RESULT = "{\"status\":200,\"message\":\"Loan information results returned successfully.\",\"result\":{}}";
	private static final String EXPECTED_EQUALS_1000_RESULT = "{\"status\":200,\"message\":\"Loan information results returned successfully.\",\"result\":{\"creditResult\":\"approved\",\"creditLimit\":240000.0}}";
	private static final Object EXPECTED_BIGGERTHAN_500_And_MONTHLYINCOME_BIGGERTHAN_5000_RESULT = "{\"status\":200,\"message\":\"Loan information results returned successfully.\",\"result\":{}}";
	private static final Object EXPECTED_SMALLER_THAN_500_RESULT = "{\"status\":200,\"message\":\"Loan information results returned successfully.\",\"result\":{\"creditResult\":\"refuse\"}}";

	@Override
	public void createTestData() {
		try {
			addDataForTest();
			waitResults(TIMEOUT, 20, INDEX_LOAN, TYPE_ENTITY);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void creditScoreSmallerThan500() throws Exception {
		// setup..
		LoanService loanService = Mockito.spy(new LoanService());
		Double monthlyIncome = (double) 60000;
		UserInfoRequest userInfoRequest = new UserInfoRequest("1", "rabia", "hatapoglu", monthlyIncome, "05555555555");
		Mockito.doReturn(userInfoRequest).when(loanService).getUserInformation(mockRequest);
		// execute..
		Response response = loanService.calculate(mockRequest);
		// assert..
		assertEquals(200, response.getStatus());
		assertEquals(EXPECTED_SMALLER_THAN_500_RESULT, response.getEntity());
	}

	@Test
	public void creditScoreEquals500() throws Exception {
		// setup..
		LoanService loanService = Mockito.spy(new LoanService());
		Double monthlyIncome = (double) 60000;
		UserInfoRequest userInfoRequest = new UserInfoRequest("6", "rabia", "hatapoglu", monthlyIncome, "05555555555");
		Mockito.doReturn(userInfoRequest).when(loanService).getUserInformation(mockRequest);
		// execute..
		Response response = loanService.calculate(mockRequest);
		// assert..
		assertEquals(200, response.getStatus());
		assertEquals(EXPECTED_EQUALS_500_RESULT, response.getEntity());
	}

	@Test
	public void creditScoreBiggerThan500() throws Exception {
		// setup..
		LoanService loanService = Mockito.spy(new LoanService());
		Double monthlyIncome = (double) 60000;
		UserInfoRequest userInfoRequest = new UserInfoRequest("2", "rabia", "hatapoglu", monthlyIncome, "05555555555");
		Mockito.doReturn(userInfoRequest).when(loanService).getUserInformation(mockRequest);
		// execute..
		Response response = loanService.calculate(mockRequest);
		// assert..
		assertEquals(200, response.getStatus());
		assertEquals(EXPECTED_BIGGER_THAN_500_RESULT, response.getEntity());
	}

	@Test
	public void creditScoreBiggerThan500AndMonthlyIncomeBiggerThan5000() throws Exception {
		// setup..
		LoanService loanService = Mockito.spy(new LoanService());
		Double monthlyIncome = (double) 60000;
		UserInfoRequest userInfoRequest = new UserInfoRequest("3", "rabia", "hatapoglu", monthlyIncome, "05555555555");
		Mockito.doReturn(userInfoRequest).when(loanService).getUserInformation(mockRequest);
		// execute..
		Response response = loanService.calculate(mockRequest);
		// assert..
		assertEquals(200, response.getStatus());
		assertEquals(EXPECTED_BIGGERTHAN_500_And_MONTHLYINCOME_BIGGERTHAN_5000_RESULT, response.getEntity());
	}

	@Test
	public void creditScoreEquals1000() throws Exception {
		// setup..
		LoanService loanService = Mockito.spy(new LoanService());
		Double monthlyIncome = (double) 60000;
		UserInfoRequest userInfoRequest = new UserInfoRequest("4", "rabia", "hatapoglu", monthlyIncome, "05555555555");
		Mockito.doReturn(userInfoRequest).when(loanService).getUserInformation(mockRequest);
		// execute..
		Response response = loanService.calculate(mockRequest);
		// assert..
		assertEquals(200, response.getStatus());
		assertEquals(EXPECTED_EQUALS_1000_RESULT, response.getEntity());
	}

	@Test
	public void creditScoreBiggerThan1000() throws Exception {
		// setup..
		LoanService loanService = Mockito.spy(new LoanService());
		Double monthlyIncome = (double) 60000;
		UserInfoRequest userInfoRequest = new UserInfoRequest("5", "rabia", "hatapoglu", monthlyIncome, "05555555555");
		Mockito.doReturn(userInfoRequest).when(loanService).getUserInformation(mockRequest);
		// execute..
		Response response = loanService.calculate(mockRequest);
		// assert..
		assertEquals(200, response.getStatus());
		assertEquals(EXPECTED_BIGGER_THAN_1000_RESULT, response.getEntity());
	}
}
