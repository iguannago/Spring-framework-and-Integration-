/*
 *
 *  * Copyright 2002-2011 the original author or authors, or Red-Black IT Ltd, as appropriate.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package rewards.internal.account;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.*;
import org.springframework.dao.EmptyResultDataAccessException;
import rewards.AccountContribution;
import rewards.AccountContribution.Distribution;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.*;

/**
 * An account for a member of the reward network. An account has one or more beneficiaries whose allocations must add
 * up
 * to 100%.
 * <p/>
 * An account can make contributions to its beneficiaries. Each contribution is distributed among the beneficiaries
 * based on an allocation.
 * <p/>
 * An entity. An aggregate.
 */
@Entity
@Table(name = "T_ACCOUNT")
public class Account {

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Integer entityId;

    @Version
    private int version;

    @Column(name = "NUMBER")
    private String number;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DATE_OF_BIRTH")
    private Date dateOfBirth;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "REWARDS_NEWSLETTER")
    @Type(type = "yes_no")
    private boolean receiveNewsletter;

    @Column(name = "MONTHLY_EMAIL_UPDATE")
    @Type(type = "yes_no")
    private boolean receiveMonthlyEmailUpdate;

    @MapKey(name = "name")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "ACCOUNT_ID")
    private Map<String, Beneficiary> beneficiaries = new HashMap<String, Beneficiary>();

    @MapKey(name = "creditCardNumber")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "ACCOUNT_ID")
    private Map<String, CreditCard> creditCards = new HashMap<String, CreditCard>();

    /**
     * Default constructor for Hibernate
     */
    protected Account() {
    }

    /**
     * Create a new account with its field values
     *
     * @param number
     * @param name
     * @param dateOfBirth
     * @param email
     * @param receiveNewsletter
     * @param receiveMonthlyEmailUpdate
     * @param creditCards
     */
    public Account(String number, String name,
                   Date dateOfBirth, String email,
                   boolean receiveNewsletter, boolean receiveMonthlyEmailUpdate, CreditCard... creditCards) {
        this.number = number;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.receiveNewsletter = receiveNewsletter;
        this.receiveMonthlyEmailUpdate = receiveMonthlyEmailUpdate;

        for (CreditCard creditCard : creditCards) {
            this.creditCards.put(creditCard.getCreditCardNumber(), creditCard);
        }
    }

    /**
     * Returns the entity identifier used to internally distinguish this entity among other entities of the same type
     * in
     * the system. Should typically only be called by privileged data access infrastructure code such as an Object
     * Relational Mapper (ORM) and not by application code.
     *
     * @return the internal entity identifier
     */
    public Integer getEntityId() {
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
     * Returns the number used to uniquely identify this account.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the number used to uniquely identify this account.
     *
     * @param number The number for this account
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Returns the beneficiaryName on file for this account.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the beneficiaryName on file for this account.
     *
     * @param name The beneficiaryName for this account
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the dateOfBirth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }


    /**
     * @param dateOfBirth the dateOfBirth to set
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the receiveNewsletter
     */
    public boolean isReceiveNewsletter() {
        return receiveNewsletter;
    }

    /**
     * @param receiveNewsletter the receiveNewsletter to set
     */
    public void setReceiveNewsletter(boolean receiveNewsletter) {
        this.receiveNewsletter = receiveNewsletter;
    }

    /**
     * @return the receiveMonthlyEmailUpdate
     */
    public boolean isReceiveMonthlyEmailUpdate() {
        return receiveMonthlyEmailUpdate;
    }

    /**
     * @param receiveMonthlyEmailUpdate the receiveMonthlyEmailUpdate to set
     */
    public void setReceiveMonthlyEmailUpdate(boolean receiveMonthlyEmailUpdate) {
        this.receiveMonthlyEmailUpdate = receiveMonthlyEmailUpdate;
    }

    /**
     * Returns a single account beneficiary. Callers should not attempt to hold on or modify the returned object. This
     * method should only be used transitively; for example, called to facilitate reporting or testing.
     *
     * @param beneficiaryName the beneficiaryName of the beneficiary e.g "Annabelle"
     * @return the beneficiary object
     */
    public Beneficiary getBeneficiary(String beneficiaryName) {
        Beneficiary beneficiary = beneficiaries.get(beneficiaryName);
        if (beneficiary == null) {
            throw new EmptyResultDataAccessException(
                    "No beneficiary exists with beneficiaryName='" + beneficiaryName + "' in account:" + this, 1);
        }

        return beneficiary;
    }

    /**
     * Set beneficiaries from set
     *
     * @param beneficiarySet
     */
    public void setBeneficiaries(Set<Beneficiary> beneficiarySet) {
        beneficiaries.clear();
        if (beneficiarySet != null) {
            for (Beneficiary beneficiary : beneficiarySet) {
                beneficiaries.put(beneficiary.getName(), beneficiary);
            }
        }
    }

    /**
     * Add a single beneficiary with a 0% allocation percentage.
     *
     * @param beneficiaryName the beneficiaryName of the beneficiary (should be unique)
     */
    public void addBeneficiary(String beneficiaryName) {
        addBeneficiary(beneficiaryName, Percentage.zero());
    }

    /**
     * Add a single beneficiary with the specified allocation percentage.
     *
     * @param beneficiaryName      the beneficiaryName of the beneficiary (should be unique)
     * @param allocationPercentage the beneficiary's allocation percentage within this account
     */
    public void addBeneficiary(String beneficiaryName, Percentage allocationPercentage) {
        if (beneficiaries.containsKey(beneficiaryName)) {
            throw new DuplicateBeneficiaryNameException(
                    "beneficiary already exists on this account:" + this, beneficiaryName);
        }
        beneficiaries.put(beneficiaryName, new Beneficiary(beneficiaryName, allocationPercentage));
    }

    /**
     * Removes a single beneficiary from this account.
     *
     * @param beneficiaryName the beneficiaryName of the beneficiary (should be unique)
     */
    public void removeBeneficiary(String beneficiaryName) {
        if (!beneficiaries.containsKey(beneficiaryName)) {
            throw new EmptyResultDataAccessException(
                    "No beneficiary exists with beneficiaryName='" + beneficiaryName + "' in account:" + this, 1);
        }

        beneficiaries.remove(beneficiaryName);
    }

    /**
     * Validation check that returns true only if the total beneficiary allocation l.e. 100%.
     *
     * @return true or false
     */
    @JsonIgnore
    public boolean isValid() {

        BigDecimal totalAllocation = BigDecimal.ZERO;
        for (Beneficiary beneficiary : beneficiaries.values()) {
            BigDecimal alloc = beneficiary.getAllocationPercentage()
                                          .asBigDecimal();
            totalAllocation = totalAllocation.add(alloc);
        }

        return (totalAllocation.compareTo(BigDecimal.ONE) <= 0);
    }

    /**
     * Make a monetary contribution to this account. The contribution amount is distributed among the account's
     * beneficiaries based on each beneficiary's allocation percentage.
     *
     * @param amount the total amount to contribute
     * @return the contribution summary
     */
    public AccountContribution makeContribution(MonetaryAmount amount) {
        if (!isValid()) {
            throw new IllegalStateException(
                    "Cannot make contributions to this account: it has invalid beneficiary allocations:" + this);
        }
        Set<Distribution> distributions = distribute(amount);
        return new AccountContribution(getNumber(), amount, distributions);
    }

    /**
     * Distribute the contribution amount among this account's beneficiaries.
     *
     * @param amount the total contribution amount
     * @return the individual beneficiary distributions
     */
    private Set<Distribution> distribute(MonetaryAmount amount) {
        Set<Distribution> distributions = new HashSet<Distribution>(beneficiaries.size());
        for (Beneficiary beneficiary : beneficiaries.values()) {
            MonetaryAmount distributionAmount = amount.multiplyBy(beneficiary.getAllocationPercentage());
            beneficiary.credit(distributionAmount);
            Distribution distribution = new Distribution(beneficiary.getName(), distributionAmount, beneficiary
                    .getAllocationPercentage(), beneficiary.getSavings());
            distributions.add(distribution);
        }
        return distributions;
    }

    /**
     * Returns the beneficiaries for this account. Callers should not attempt to hold on or modify the returned set.
     * This method should only be used transitively; for example, called to facilitate account reporting.
     *
     * @return the beneficiaries of this account
     */
    public Set<Beneficiary> getBeneficiaries() {
        return Collections.unmodifiableSet(new HashSet<Beneficiary>(beneficiaries.values()));
    }


    /**
     * Get associated credit cards as a set. Callers should not attempt to hold on or modify the returned set.
     *
     * @return set of credit cards
     */
    public Set<CreditCard> getCreditCards() {
        return Collections.unmodifiableSet(new HashSet<CreditCard>(creditCards.values()));
    }

    /**
     * Get set of credit card numbers for all associated creditCards
     *
     * @return set of strings
     */
    public Set<String> getCreditCardNumbers() {
        return new HashSet<String>(creditCards.keySet());
    }

    /**
     * Set associated credit cards
     *
     * @param creditCardSet
     */
    public void setCreditCards(Set<CreditCard> creditCardSet) {
        creditCards.clear();
        if (creditCardSet != null) {
            for (CreditCard creditCard : creditCardSet) {
                creditCards.put(creditCard.getCreditCardNumber(), creditCard);
            }
        }
    }

    /**
     * Check if this account has a credit card with the supplied number
     *
     * @return true or false accordingly
     */
    public boolean hasCreditCard(String creditCardNumber) {
        return creditCards.containsKey(creditCardNumber);
    }

    /**
     * Add a credit card object
     *
     * @param creditCard
     */
    public void addCreditCard(CreditCard creditCard) {
        if (hasCreditCard(creditCard.getCreditCardNumber())) {
            throw new DuplicateAccountFieldsException(
                    "creditCard already exists on this account:" + this, Collections.singleton(creditCard.getCreditCardNumber()));
        }
        creditCards.put(creditCard.getCreditCardNumber(), creditCard);
    }


    /**
     * Remove a credit card by name
     *
     * @param creditCardNumber
     */
    public void removeCreditCard(String creditCardNumber) {
        if (!hasCreditCard(creditCardNumber)) {
            throw new EmptyResultDataAccessException(
                    "No creditCard exists with number='" + creditCardNumber + "' in account:" + this, 1);
        }
        creditCards.remove(creditCardNumber);
    }

    /**
     * equals excluding primary key
     *
     * @param o
     * @return true or false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Account)) {
            return false;
        }

        Account account = (Account) o;

        if (receiveMonthlyEmailUpdate != account.receiveMonthlyEmailUpdate) {
            return false;
        }
        if (receiveNewsletter != account.receiveNewsletter) {
            return false;
        }
        if (beneficiaries != null ? !beneficiaries.equals(account.beneficiaries) : account.beneficiaries != null) {
            return false;
        }
        if (creditCards != null ? !creditCards.equals(account.creditCards) : account.creditCards != null) {
            return false;
        }
        if (dateOfBirth != null ? !dateOfBirth.equals(account.dateOfBirth) : account.dateOfBirth != null) {
            return false;
        }
        if (email != null ? !email.equals(account.email) : account.email != null) {
            return false;
        }
        if (name != null ? !name.equals(account.name) : account.name != null) {
            return false;
        }
        if (number != null ? !number.equals(account.number) : account.number != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = number != null ? number.hashCode() : 0;
        return result;
    }

    /**
     * toString, including DB PK
     *
     * @return
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("{entityId=").append(entityId);
        sb.append(", number='").append(number).append('\'');
        sb.append(", beneficiaryName='").append(name).append('\'');
        sb.append(", dateOfBirth=").append(dateOfBirth);
        sb.append(", email='").append(email).append('\'');
        sb.append(", receiveNewsletter=").append(receiveNewsletter);
        sb.append(", receiveMonthlyEmailUpdate=").append(receiveMonthlyEmailUpdate);
        sb.append(", beneficiaries=").append(beneficiaries);
        sb.append(", creditCards=").append(creditCards);
        sb.append('}');
        return sb.toString();
    }
}