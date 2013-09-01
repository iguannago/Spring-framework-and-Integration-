package rewards.internal.account;

import org.springframework.util.Assert;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * DTO class for credit card information
 *
 * @author Dominic North
 */
public class CreditCardInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Entity ID for corresponding creditCard (may be null)
     */
    private Integer id;

    @Pattern(regexp = "\\d{16}")
    private String creditCardNumber;


    /**
     * Default constructor for inactive card
     */
    public CreditCardInfo() {
        this((String) null);
    }

    /**
     * Constructor taking creditCardNumber
     *
     * @param creditCardNumber
     */
    public CreditCardInfo(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * Constructor taking creditCard object
     *
     * @param creditCard
     */
    public CreditCardInfo(CreditCard creditCard) {
        this.id = creditCard.getId();
        this.creditCardNumber = creditCard.getCreditCardNumber();
    }

    /**
     * Return the entity ID
     *
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the entityId
     *
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }


    /**
     * Get the credit card creditCardNumber
     *
     * @return creditCardNumber
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * Set the credit card creditCardNumber
     *
     * @param creditCardNumber
     */
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }


    /**
     * Return true if credit card is active
     *
     * @return true or false
     */
    public boolean isActive() {
        return (creditCardNumber != null);
    }


    /**
     * Create a new crediCard from our values
     *
     * @return creditCard
     */
    public CreditCard createCreditCard() {
        Assert.state(isActive(), "should be active");
        return new CreditCard(creditCardNumber);
    }

    /**
     * equals based on creditCardNumber alone
     *
     * @param otherObj
     * @return true or false as appropriate
     */
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        if (!(otherObj instanceof CreditCardInfo)) {
            return false;
        }

        CreditCardInfo other = (CreditCardInfo) otherObj;

        if (creditCardNumber != null ? !creditCardNumber.equals(other.creditCardNumber) : other.creditCardNumber != null) {
            return false;
        }

        return true;
    }

    /**
     * hashCode based on creditCardNumber alone
     *
     * @return code
     */
    @Override
    public int hashCode() {
        return creditCardNumber != null ? creditCardNumber.hashCode() : 0;
    }

    /**
     * toString based on all fields
     *
     * @return string representation of state
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CreditCardInfo");
        sb.append("{id=").append(id);
        if (isActive()) {
            sb.append(", creditCardNumber='").append(creditCardNumber).append('\'');
        } else {
            sb.append(" (inactive)");
        }
        sb.append('}');
        return sb.toString();
    }
}
