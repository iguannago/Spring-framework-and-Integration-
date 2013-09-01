/**
 * 
 */
package rewards.internal.account;

import org.springframework.dao.DuplicateKeyException;

/**
 * Exception for duplicate beneficiary name
 *
 * @author djnorth
 */
public class DuplicateBeneficiaryNameException extends DuplicateKeyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor taking message
	 * 
	 * @param msg
	 */
	public DuplicateBeneficiaryNameException(String msg) {
		super(msg);
	}

	/**
	 * Constructor taking message and beneficiaryName
	 * 
	 * @param msg
	 * @param beneficiaryName
	 */
	public DuplicateBeneficiaryNameException(String msg, String beneficiaryName) {
		super(formatMsgWithName(msg, beneficiaryName));
	}

	/**
	 * Constructor taking message and root cause
	 * 
	 * @param msg
	 * @param cause
	 */
	public DuplicateBeneficiaryNameException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Constructor taking message, beneficiaryName, and root cause
	 * 
	 * @param msg
	 * @param beneficiaryName
	 * @param cause
	 */
	public DuplicateBeneficiaryNameException(String msg, String beneficiaryName, Throwable cause) {
		super(formatMsgWithName(msg, beneficiaryName), cause);
	}

	/**
	 * Static helper to format message with beneficiaryName
	 * 
	 * @param msg
	 * @param beneficiaryName
	 */
	protected static String formatMsgWithName(String msg, String beneficiaryName) {
		if (beneficiaryName == null) {
			return msg;
		}
		else {
			return (msg == null ? "" : msg + ":" ) + "duplicate beneficiaryName='" + beneficiaryName + "'";
		}
	}
}
