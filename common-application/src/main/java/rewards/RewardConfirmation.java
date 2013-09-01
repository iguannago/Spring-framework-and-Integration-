package rewards;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * A summary of a confirmed reward transaction describing a contribution made to an account that was distributed among
 * the account's beneficiaries.
 */
public class RewardConfirmation implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String confirmationNumber;

    private AccountContribution accountContribution;

    /**
     * Creates a new reward confirmation.
     *
     * @param confirmationNumber  the unique confirmation number
     * @param accountContribution a summary of the account contribution that was made
     */
    @JsonCreator
    public RewardConfirmation(@JsonProperty("confirmationNumber") String confirmationNumber,
                              @JsonProperty("accountContribution") AccountContribution accountContribution) {
        this.confirmationNumber = confirmationNumber;
        this.accountContribution = accountContribution;
    }

    /**
     * Returns the confirmation number of the reward transaction. Can be used later to lookup the transaction record.
     */
    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    /**
     * Returns a summary of the monetary contribution that was made to an account. Note that the distributions may
     * not be filled in if the confirmation is re-constitued from the database
     *
     * @return the account contribution (the details of this reward)
     */
    public AccountContribution getAccountContribution() {
        return accountContribution;
    }

    /**
     * Normal equals for exact equality
     *
     * @param otherObj
     * @return true or false
     */
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        if (!(otherObj instanceof RewardConfirmation)) {
            return false;
        }

        RewardConfirmation other = (RewardConfirmation) otherObj;
        if (!hasEqualConfirmationNumber(other)) {
            return false;
        }

        if (accountContribution != null ? !accountContribution.equals(other.accountContribution) :
            other.accountContribution != null) {
            return false;
        }

        return true;
    }

    /**
     * Comparison for equal or duplicate (account contribution has no distributions)
     *
     * @param other
     * @return true or false
     */
    public boolean isEqualOrDuplicate(RewardConfirmation other) {
        return (other != null && hasEqualConfirmationNumber(other) &&
                (this.accountContribution == null ? other.accountContribution == null :
                 this.accountContribution.isEqualOrDuplicate(other.accountContribution)));
    }

    /**
     * Compare confirmationNumber
     *
     * @param other
     * @return true or false
     */
    private boolean hasEqualConfirmationNumber(RewardConfirmation other) {
        return (confirmationNumber != null ? confirmationNumber.equals(other.confirmationNumber) :
                other.confirmationNumber == null);
    }

    @Override
    public int hashCode() {
        int result = confirmationNumber != null ? confirmationNumber.hashCode() : 0;
        result = 31 * result + (accountContribution != null ? accountContribution.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RewardConfirmation");
        sb.append("{confirmationNumber='").append(confirmationNumber).append('\'');
        sb.append(", accountContribution=").append(accountContribution);
        sb.append('}');
        return sb.toString();
    }
}