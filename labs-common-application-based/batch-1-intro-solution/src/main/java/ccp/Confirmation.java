package ccp;

public class Confirmation {
	private final String transactionId;

	public Confirmation(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}
}
