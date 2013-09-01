package rewards;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Set;

/**
 * A summary of a monetary contribution made to an account that was distributed among the account's beneficiaries.
 * <p/>
 * A value object. Immutable.
 */
public class AccountContribution implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String accountNumber;

    private MonetaryAmount amount;

    private Set<Distribution> distributions;

    /**
     * Creates a new account contribution.
     *
     * @param accountNumber the number of the account the contribution was made
     * @param amount        the total contribution amount
     * @param distributions how the contribution was distributed among the account's beneficiaries
     */
    @JsonCreator
    public AccountContribution(@JsonProperty("accountNumber") String accountNumber,
                               @JsonProperty("amount") MonetaryAmount amount,
                               @JsonProperty("distributions") Set<Distribution> distributions) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.distributions = distributions;
    }

    /**
     * Returns the number of the account this contribution was made to.
     *
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Returns the total amount of the contribution.
     *
     * @return the contribution amount
     */
    public MonetaryAmount getAmount() {
        return amount;
    }

    /**
     * Returns how this contribution was distributed among the account's beneficiaries.
     *
     * @return the contribution distributions
     */
    public Set<Distribution> getDistributions() {
        return distributions;
    }

    /**
     * Returns how this contribution was distributed to a single account beneficiary.
     *
     * @param beneficiary the name of the beneficiary e.g "Annabelle"
     * @return a summary of how the contribution amount was distributed to the beneficiary
     */
    public Distribution getDistribution(String beneficiary) {
        for (Distribution d : distributions) {
            if (d.beneficiary.equals(beneficiary)) {
                return d;
            }
        }
        throw new IllegalArgumentException("No such distribution for '" + beneficiary + "'");
    }

    /**
     * Full-blown equals, comparing everything
     *
     * @param otherObj
     * @return true or false
     */
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        if (!(otherObj instanceof AccountContribution)) {
            return false;
        }

        AccountContribution other = (AccountContribution) otherObj;

        if (!hasEqualAttributes(other)) {
            return false;
        }
        if (distributions != null ? !distributions.equals(other.distributions) : other.distributions != null) {
            return false;
        }

        return true;
    }


    /**
     * Equals allowing for duplicates i.e. attributes equal and one of
     * <ul>
     * <li>other has equal distributions</li>
     * <li>other has null distributions</li>
     * </ul>
     *
     * @param other
     * @return true or false
     */
    public boolean isEqualOrDuplicate(AccountContribution other) {
        return (other != null && hasEqualAttributes(other) &&
                (other.distributions == null || this.distributions == null ||
                 this.distributions.equals(other.distributions)));
    }

    @Override
    public int hashCode() {
        int result = accountNumber != null ? accountNumber.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (distributions != null ? distributions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AccountContribution");
        sb.append("{accountNumber='").append(accountNumber).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", distributions=").append(distributions);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Helper comparing attributes alone
     *
     * @param other
     * @return true or false
     */
    private boolean hasEqualAttributes(AccountContribution other) {
        if (accountNumber != null ? !accountNumber.equals(other.accountNumber) : other.accountNumber != null) {
            return false;
        }

        if (amount != null ? !amount.equals(other.amount) : other.amount != null) {
            return false;
        }
        return true;
    }

    /**
     * A single distribution made to a beneficiary as part of an account contribution, summarizing the distribution
     * amount and resulting total beneficiary savings.
     * <p/>
     * A value object.
     */
    public static class Distribution implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private String beneficiary;

        private MonetaryAmount amount;

        private Percentage percentage;

        private MonetaryAmount totalSavings;

        /**
         * Creates a new distribution.
         *
         * @param beneficiary  the name of the account beneficiary that received a distribution
         * @param amount       the distribution amount
         * @param percentage   this distribution's percentage of the total account contribution
         * @param totalSavings the beneficiary's total savings amount after the distribution was made
         */
        @JsonCreator
        public Distribution(@JsonProperty("beneficiary") String beneficiary,
                            @JsonProperty("amount") MonetaryAmount amount,
                            @JsonProperty("percentage") Percentage percentage,
                            @JsonProperty("totalSavings") MonetaryAmount totalSavings) {
            this.beneficiary = beneficiary;
            this.percentage = percentage;
            this.amount = amount;
            this.totalSavings = totalSavings;
        }


        /**
         * Returns the name of the beneficiary.
         */
        public String getBeneficiary() {
            return beneficiary;
        }


        /**
         * Returns the amount of this distribution.
         */
        public MonetaryAmount getAmount() {
            return amount;
        }


        /**
         * Returns the percentage of this distribution relative to others in the contribution.
         */
        public Percentage getPercentage() {
            return percentage;
        }


        /**
         * Returns the total savings of the beneficiary after this distribution.
         */
        public MonetaryAmount getTotalSavings() {
            return totalSavings;
        }


        /**
         * Equals based on all fields
         *
         * @param o
         * @return true or false
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Distribution)) {
                return false;
            }

            Distribution that = (Distribution) o;

            if (amount != null ? !amount.equals(that.amount) : that.amount != null) {
                return false;
            }
            if (beneficiary != null ? !beneficiary.equals(that.beneficiary) : that.beneficiary != null) {
                return false;
            }
            if (percentage != null ? !percentage.equals(that.percentage) : that.percentage != null) {
                return false;
            }
            if (totalSavings != null ? !totalSavings.equals(that.totalSavings) : that.totalSavings != null) {
                return false;
            }

            return true;
        }


        /**
         * Hashcode based on all fields
         *
         * @return
         */
        @Override
        public int hashCode() {
            int result = beneficiary != null ? beneficiary.hashCode() : 0;
            result = 31 * result + (amount != null ? amount.hashCode() : 0);
            result = 31 * result + (percentage != null ? percentage.hashCode() : 0);
            result = 31 * result + (totalSavings != null ? totalSavings.hashCode() : 0);
            return result;
        }


        /**
         * toString
         *
         * @return our state as string
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Distribution");
            sb.append("{beneficiary='").append(beneficiary).append('\'');
            sb.append(", amount=").append(amount);
            sb.append(", percentage=").append(percentage);
            sb.append(", totalSavings=").append(totalSavings);
            sb.append('}');
            return sb.toString();
        }
    }
}