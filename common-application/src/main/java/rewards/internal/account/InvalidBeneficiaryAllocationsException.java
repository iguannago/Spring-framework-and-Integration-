package rewards.internal.account;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * Specific exception for invalid beneficiary allocation total
 *
 * @author Dominic North
 */
public class InvalidBeneficiaryAllocationsException extends DataIntegrityViolationException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor just taking message
     *
     * @param msg
     */
    public InvalidBeneficiaryAllocationsException(String msg) {
        super(msg);
    }

    /**
     * Constructor taking message and causal exception
     *
     * @param msg
     * @param cause
     */
    public InvalidBeneficiaryAllocationsException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
