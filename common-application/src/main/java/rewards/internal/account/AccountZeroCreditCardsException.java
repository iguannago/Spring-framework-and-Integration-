package rewards.internal.account;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * @author Dominic North
 */
public class AccountZeroCreditCardsException extends DataIntegrityViolationException {

    /**
     * For serialization
     */
    private static final long serialVersionUID = 1L;

    public AccountZeroCreditCardsException(String msg) {
        super(msg);
    }

    public AccountZeroCreditCardsException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
