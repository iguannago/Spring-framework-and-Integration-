package rewards.internal.restaurant;

import javax.persistence.*;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.hibernate.annotations.Type;
import rewards.Dining;
import rewards.internal.account.Account;

/**
 * A restaurant establishment in the network. Like AppleBee's.
 * 
 * Restaurants calculate how much benefit may be awarded to an account for dining based on a availability policy and a
 * benefit percentage.
 */
@Entity
@Table(name = "T_RESTAURANT")
public class Restaurant {

	@Id
	@Column(name = "ID")
	private Integer entityId;

	@Column(name = "MERCHANT_NUMBER")
	private String number;

	@Column(name = "NAME")
	private String name;

	@Column(name = "BENEFIT_PERCENTAGE")
    @Type(type = "common.money.PercentageUserType")
	private Percentage benefitPercentage;

	@Column(name = "BENEFIT_AVAILABILITY_POLICY")
	private BenefitAvailabilityPolicy benefitAvailabilityPolicy;

    @Version
    private int version;

	protected Restaurant() {
	}

	/**
	 * Creates a new restaurant.
	 * @param number the restaurant's merchant number
	 * @param name the name of the restaurant
	 */
	public Restaurant(String number, String name) {
		this.number = number;
		this.name = name;
	}

	/**
	 * Returns the entity identifier used to internally distinguish this entity among other entities of the same type in
	 * the system. Should typically only be called by privileged data access infrastructure code such as an Object
	 * Relational Mapper (ORM) and not by application code.
	 * @return the internal entity identifier
	 */
	protected Integer getEntityId() {
		return entityId;
	}

	/**
	 * Sets the internal entity identifier - should only be called by privileged data access code (repositories that
	 * work with an Object Relational Mapper (ORM)). Should never be set by application code explicitly.
	 * @param entityId the internal entity identifier
	 */
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	/**
	 * Sets the percentage benefit to be awarded for eligible dining transactions.
	 * @param benefitPercentage the benefit percentage
	 */
	public void setBenefitPercentage(Percentage benefitPercentage) {
		this.benefitPercentage = benefitPercentage;
	}

	/**
	 * Sets the policy that determines if a dining by an account at this restaurant is eligible for benefit.
	 * @param benefitAvailabilityPolicy the benefit availability policy
	 */
	public void setBenefitAvailabilityPolicy(BenefitAvailabilityPolicy benefitAvailabilityPolicy) {
		this.benefitAvailabilityPolicy = benefitAvailabilityPolicy;
	}

	/**
	 * Returns the name of this restaurant.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the merchant number of this restaurant.
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Returns this restaurant's benefit percentage.
	 */
	public Percentage getBenefitPercentage() {
		return benefitPercentage;
	}

	/**
	 * Returns this restaurant's benefit availability policy.
	 */
	public BenefitAvailabilityPolicy getBenefitAvailabilityPolicy() {
		return benefitAvailabilityPolicy;
	}

	/**
	 * Calculate the benefit eligible to this account for dining at this restaurant.
	 * @param account the account that dined at this restaurant
	 * @param dining a dining event that occurred
	 * @return the benefit amount eligible for reward
	 */
	public MonetaryAmount calculateBenefitFor(Account account, Dining dining) {
		if (this.benefitAvailabilityPolicy.isBenefitAvailableFor(account, dining)) {
			return new MonetaryAmount(dining.getAmount()).multiplyBy(benefitPercentage);
		} else {
			return MonetaryAmount.zero();
		}
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant)) return false;

        Restaurant that = (Restaurant) o;

        if (benefitAvailabilityPolicy != that.benefitAvailabilityPolicy) return false;
        if (benefitPercentage != null ? !benefitPercentage.equals(that.benefitPercentage) : that.benefitPercentage != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = number != null ? number.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (benefitPercentage != null ? benefitPercentage.hashCode() : 0);
        result = 31 * result + (benefitAvailabilityPolicy != null ? benefitAvailabilityPolicy.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Restaurant");
        sb.append("{entityId=").append(entityId);
        sb.append(", number='").append(number).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", benefitPercentage=").append(benefitPercentage);
        sb.append(", benefitAvailabilityPolicy=").append(benefitAvailabilityPolicy);
        sb.append('}');
        return sb.toString();
    }
}