package rewards.internal.account;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.Set;

/**
 * Exception allowing multiple fields to be validated for duplication
 *
 * @author Dominic North
 */
public class DuplicateAccountFieldsException extends DataIntegrityViolationException {

    /**
     * For serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * Duplicate number value (or null if not duplicate)
     */
    private final String duplicateAccountNumber;

    /**
     * Duplicate name value (or null if not duplicate)
     */
    private final String duplicateAccountName;

    /**
     * Duplicate credit card number values (or null if no duplicates)
     */
    private final Set<String> duplicateCreditCardNumbers;

    /**
     * Constructor taking message, accountName, accountNumber and creditCards
     *
     * @param msg
     * @param duplicateAccountNumber
     * @param duplicateAccountName
     * @param duplicateCreditCardNumbers
     */
    public DuplicateAccountFieldsException(String msg,
                                           String duplicateAccountNumber,
                                           String duplicateAccountName,
                                           Set<String> duplicateCreditCardNumbers) {
        super(formatMsgForDuplicateFields(msg, duplicateAccountNumber, duplicateAccountName,
                                          duplicateCreditCardNumbers));

        this.duplicateAccountNumber = duplicateAccountNumber;
        this.duplicateAccountName = duplicateAccountName;

        if (duplicateCreditCardNumbers == null || duplicateCreditCardNumbers.isEmpty()) {
            this.duplicateCreditCardNumbers = null;
        } else {
            this.duplicateCreditCardNumbers = new HashSet<String>(duplicateCreditCardNumbers);
        }
    }


    /**
     * Constructor taking message, accountName and creditCards only
     *
     * @param msg
     * @param duplicateAccountName
     * @param duplicateCreditCardNumbers
     */
    public DuplicateAccountFieldsException(String msg,
                                           String duplicateAccountName,
                                           Set<String> duplicateCreditCardNumbers) {

        this(msg, null, duplicateAccountName, duplicateCreditCardNumbers);
    }


    /**
     * Constructor taking message and creditCards only
     *
     * @param msg
     * @param duplicateCreditCardNumbers
     */
    public DuplicateAccountFieldsException(String msg,
                                           Set<String> duplicateCreditCardNumbers) {

        this(msg, null, null, duplicateCreditCardNumbers);
    }


    /**
     * Test for duplicateAccountNumber
     *
     * @return true if duplicate
     */
    public boolean hasDuplicateAccountNumber() {
        return (duplicateAccountNumber != null);
    }


    /**
     * Test for duplicateAccountName
     *
     * @return true if duplicate
     */
    public boolean hasDuplicateAccountName() {
        return (duplicateAccountName != null);
    }


    /**
     * Test for duplicateCreditCardNumbers
     *
     * @return true if duplicate
     */
    public boolean hasDuplicateCreditCardNumbers() {
        return duplicateCreditCardNumbers != null;
    }

    /**
     * @return duplicateAccountNumber
     */
    public String getDuplicateAccountNumber() {
        return duplicateAccountNumber;
    }

    /**
     * @return duplicateAccountName
     */
    public String getDuplicateAccountName() {
        return duplicateAccountName;
    }

    /**
     * @return duplicateCreditCardNumbers
     */
    public Set<String> getDuplicateCreditCardNumbers() {
        return duplicateCreditCardNumbers;
    }

    /**
     * Helper to format message with duplicate field map
     *
     * @param msg
     * @param duplicateAccountNumber
     * @param duplicateAccountName
     * @param duplicateCreditCardNames
     */
    protected static String formatMsgForDuplicateFields(String msg,
                                                        String duplicateAccountNumber,
                                                        String duplicateAccountName,
                                                        Set<String> duplicateCreditCardNames) {

        StringBuilder msgsb = new StringBuilder();
        if (msg != null) {
            msgsb.append(msg).append(':');
        }

        if (duplicateAccountNumber != null) {
            msgsb.append("duplicate accountNumber='").append(duplicateAccountNumber).append("'");
        }

        if (duplicateAccountName != null) {
            msgsb.append("duplicate accountName='").append(duplicateAccountName).append("'");
        }

        if (duplicateCreditCardNames != null && !duplicateCreditCardNames.isEmpty()) {
            msgsb.append("duplicate creditCards=").append(duplicateCreditCardNames);
        }

        return msgsb.toString();

    }
}
