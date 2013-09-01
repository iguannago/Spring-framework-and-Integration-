package rewards.internal.account;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * A single beneficiary allocated to an account. Each beneficiary has a name (e.g. Annabelle) and a savings balance
 * tracking how much money has been saved for he or she to date (e.g. $1000).
 */
@Entity
@Table(name = "T_ACCOUNT_BENEFICIARY")
public class Beneficiary {

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Integer entityId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ALLOCATION_PERCENTAGE")
    @Type(type = "common.money.PercentageUserType")
    private Percentage allocationPercentage;

    @Column(name = "SAVINGS")
    @Type(type = "common.money.MonetaryAmountUserType")
    private MonetaryAmount savings = MonetaryAmount.zero();

    @Version
    private int version;

    /**
     * Default constructor for hibernate
     */
    protected Beneficiary() {
    }

    /**
     * Creates a new account beneficiary.
     *
     * @param name                 the name of the beneficiary
     * @param allocationPercentage the beneficiary's allocation percentage within its account
     */
    public Beneficiary(String name, Percentage allocationPercentage) {
        this.name = name;
        this.allocationPercentage = allocationPercentage;
    }

    /**
     * Creates a new account beneficiary. This constructor should be called by privileged objects responsible for
     * reconstituting an existing Account object from some external form such as a collection of database records.
     * Marked package-private to indicate this constructor should never be called by general application code.
     *
     * @param name                 the name of the beneficiary
     * @param allocationPercentage the beneficiary's allocation percentage within its account
     * @param savings              the total amount saved to-date for this beneficiary
     */
    Beneficiary(String name, Percentage allocationPercentage, MonetaryAmount savings) {
        this.name = name;
        this.allocationPercentage = allocationPercentage;
        this.savings = savings;
    }

    /**
     * Returns the entity identifier used to internally distinguish this entity among other entities of the same type in
     * the system. Should typically only be called by privileged data access infrastructure code such as an Object
     * Relational Mapper (ORM) and not by application code.
     *
     * @return the internal entity identifier
     */
    protected Integer getEntityId() {
        return entityId;
    }

    /**
     * Sets the internal entity identifier - should only be called by privileged data access code (repositories that
     * work with an Object Relational Mapper (ORM)). Should never be set by application code explicitly.
     *
     * @param entityId the internal entity identifier
     */
    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    /**
     * Returns the beneficiary name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the beneficiary's allocation percentage in this account.
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
     * Credit the amount to this beneficiary's saving balance.
     *
     * @param amount the amount to credit
     */
    public void credit(MonetaryAmount amount) {
        savings = savings.add(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Beneficiary)) {
            return false;
        }

        Beneficiary that = (Beneficiary) o;

        if (allocationPercentage != null ? !allocationPercentage.equals(that.allocationPercentage) :
            that.allocationPercentage != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (savings != null ? !savings.equals(that.savings) : that.savings != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        return result;
    }

    /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(" [entityId=");
        builder.append(entityId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", allocationPercentage=");
        builder.append(allocationPercentage);
        builder.append(", savings=");
        builder.append(savings);
        builder.append("]hashCode=").append(hashCode());
        return builder.toString();
    }


    /**
     * Protected method to set savings. This is for the use of helper classes like BeneficiaryInfo.
     *
     * @param savings
     */
    protected void setSavings(MonetaryAmount savings) {
        this.savings = savings;
    }
}