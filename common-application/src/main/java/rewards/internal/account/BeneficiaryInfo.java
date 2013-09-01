package rewards.internal.account;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Beneficiary DTO.
 * <p/>
 * Note that we keep the savings immutable for now, except when setting from a
 * beneficiary object. Furthermore, we only use name to createAccountInfo a new beneficiary.
 *
 * @author djnorth
 */
public class BeneficiaryInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Beneficiary name
     */
    @NotNull
    @Pattern(regexp = "[a-z][a-z \\.-]*[a-z\\.]", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String name;

    /**
     * Allocation percentage
     */
    @NumberFormat(style = Style.PERCENT)
    private Percentage allocationPercentage = Percentage.zero();

    /**
     * Savings
     */
    @NumberFormat(style = Style.CURRENCY)
    private MonetaryAmount savings = MonetaryAmount.zero();

    /**
     * Default constructor
     */
    public BeneficiaryInfo() {
    }

    /**
     * Constructor taking name
     *
     * @param name
     */
    public BeneficiaryInfo(String name) {
        this.name = name;
    }

    /**
     * Constructor taking all fields
     *
     * @param name
     * @param allocationPercentage
     * @param savings
     */
    public BeneficiaryInfo(String name, Percentage allocationPercentage,
                           MonetaryAmount savings) {
        this.name = name;
        this.allocationPercentage = allocationPercentage;
        this.savings = savings;
    }

    /**
     * Constructor taking accountId and beneficiary object
     *
     * @param beneficiary
     */
    public BeneficiaryInfo(Beneficiary beneficiary) {
        this.name = beneficiary.getName();
        this.allocationPercentage = beneficiary.getAllocationPercentage();
        this.savings = beneficiary.getSavings();
    }


    /**
     * Returns the beneficiary name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the beneficiary name, which should be unique among beneficiaries on a
     * given account
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the beneficiary's allocation percentage in this account.
     *
     * @return allocation percentage
     */
    public Percentage getAllocationPercentage() {
        return allocationPercentage;
    }

    /**
     * Sets the beneficiary's allocation percentage in this account.
     *
     * @param allocationPercentage The new allocation percentage
     */
    public void setAllocationPercentage(Percentage allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }

    /**
     * Returns the amount of savings this beneficiary has accrued.
     */
    public MonetaryAmount getSavings() {
        return savings;
    }

    /**
     * Create a new beneficiary from our attributes, but with 0
     * savings
     *
     * @return new beneficiary
     */
    public Beneficiary createBeneficiary() {
        return new Beneficiary(name, allocationPercentage, savings);
    }

    /**
     * Update an existing beneficiary from our allocationPercentage and savings
     *
     * @param beneficiary to updateAccountInfo
     */
    public void updateBeneficiary(Beneficiary beneficiary) {
        if (!beneficiary.getName().equals(getName())) {
            throw new IllegalArgumentException("beneficiary should have same name:beneficiary=" + beneficiary +
                                               ":this=" + this);
        }
        beneficiary.setAllocationPercentage(allocationPercentage);
        beneficiary.setSavings(savings);
    }


    /**
     * Unlike Beneficiary, hashCode based on all fields
     *
     * @return hash code
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = (name == null) ? 0 : name.hashCode();
        return result;
    }

    /**
     * Unlike Beneficiary, equals compares all fields
     *
     * @return result
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BeneficiaryInfo)) {
            return false;
        }
        BeneficiaryInfo other = (BeneficiaryInfo) obj;
        if (allocationPercentage == null) {
            if (other.allocationPercentage != null) {
                return false;
            }
        } else if (!allocationPercentage.equals(other.allocationPercentage)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (savings == null) {
            if (other.savings != null) {
                return false;
            }
        } else if (!savings.equals(other.savings)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BeneficiaryInfo [name=");
        builder.append(name);
        builder.append(", allocationPercentage=");
        builder.append(allocationPercentage);
        builder.append(", savings=");
        builder.append(savings);
        builder.append("]");
        return builder.toString();
    }

}