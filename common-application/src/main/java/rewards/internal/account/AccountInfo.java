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

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

/**
 * Account DTO object. Unlike the domain object, we do not have any direct
 * relationship with beneficiaries. We also (for now) support a single credit card number,
 * adapting the domain objects collection when interacting with it.
 *
 * @author djnorth
 */
public class AccountInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Entity ID for corresponding account (may be null)
     */
    private Integer entityId;

    /**
     * Account number
     */
    @NotNull
    @Pattern(regexp = "\\d{9,9}")
    private String number;

    /**
     * Account holder name
     */
    @NotNull
    @Size(min = 5)
    @Pattern(regexp = "[a-z]+[a-z \\.-]*[a-z\\.]", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String name;

    /**
     * Date of birth for account holder
     */
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    /**
     * E-mail address for marketing etc.
     * The pattern is not 100% water-tight, but is a reasonable start
     */
    @Pattern(regexp = "[a-z0-9\\._-]+@[a-z0-9\\._-]+[a-z]", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;

    /**
     * True if subscribed to electronic newsletter
     */
    private boolean receiveNewsletter;

    /**
     * True if subscribed to monthly e-mail with special offers
     */
    private boolean receiveMonthlyEmailUpdate;

    /**
     * related CreditCardInfo objects
     */
    private Map<String, CreditCardInfo> creditCardInfoMap = new HashMap<String, CreditCardInfo>();

    /**
     * Default constructor
     */
    public AccountInfo() {
    }

    /**
     * Constructor taking account object
     *
     * @param account
     */
    public AccountInfo(Account account) {
        this.entityId = account.getEntityId();
        this.number = account.getNumber();
        this.name = account.getName();
        this.dateOfBirth = account.getDateOfBirth();
        this.email = account.getEmail();
        this.receiveNewsletter = account.isReceiveNewsletter();
        this.receiveMonthlyEmailUpdate = account.isReceiveMonthlyEmailUpdate();

        for (CreditCard creditCard : account.getCreditCards()) {
            creditCardInfoMap.put(creditCard.getCreditCardNumber(), new CreditCardInfo(creditCard));
        }
    }

    /**
     * @return the entityId
     */
    public Integer getEntityId() {
        return entityId;
    }

    /**
     * @param entityId the entityId to set
     */
    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
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
     * Get credit card info objects as a set
     *
     * @return a set of credit card info objects
     */
    public Set<CreditCardInfo> getCreditCardInfo() {
        return new HashSet<CreditCardInfo>(creditCardInfoMap.values());
    }

    /**
     * Set credit card info map from set
     *
     * @param creditCardInfoSet
     */
    public void setCreditCardInfo(Set<CreditCardInfo> creditCardInfoSet) {
        creditCardInfoMap.clear();
        if (creditCardInfoSet != null) {
            for (CreditCardInfo creditCardInfo : creditCardInfoSet) {
                if (creditCardInfo.isActive()) {
                    creditCardInfoMap.put(creditCardInfo.getCreditCardNumber(), creditCardInfo);
                }
            }
        }
    }


    /**
     * Get credit card numbers
     *
     * @return credit card numbsrs
     */
    public Set<String> getCreditCardNumbers() {
        return new HashSet<String>(creditCardInfoMap.keySet());
    }


    /**
     * Set credit card numbers from array (dropping duplicates)
     *
     * @return credit card numbers
     */
    public void setCreditCardNumbers(Set<String> creditCardNumbers) {
        creditCardInfoMap.clear();
        for (String creditCardNumber : creditCardNumbers) {
            creditCardInfoMap.put(creditCardNumber, new CreditCardInfo(creditCardNumber));
        }
    }

    /**
     * Check if this account has a credit card with the supplied number
     *
     * @return true or false accordingly
     */
    public boolean hasCreditCardInfo(String creditCardNumber) {
        return (creditCardInfoMap.containsKey(creditCardNumber) && creditCardInfoMap.get(creditCardNumber).isActive());
    }


    /**
     * Get single creditCardNumber.
     * <ul>
     * <li>If there are none, return null</li>
     * <li>If there is exactly one, return it</li>
     * <li>If there are currently more than 1, return first number in keySet</li>
     * </ul>
     *
     * @return only credit card number
     */
    @NotNull
    @Pattern(regexp = "\\d{16}")
    public String getCreditCardNumber() {
        String creditCardNumber = null;
        if (!creditCardInfoMap.isEmpty()) {
            creditCardNumber = creditCardInfoMap.keySet().iterator().next();
        }

        return creditCardNumber;
    }

    /**
     * Set additional creditCardNumber. i.e. if the supplied number is not in our map, add it
     *
     * @param creditCardNumber
     */
    public void setCreditCardNumber(String creditCardNumber) {
        Assert.notNull(creditCardNumber, "creditCardNumber");

        if (!hasCreditCardInfo(creditCardNumber)) {
            Assert.state(creditCardInfoMap.size() <= 1);

            if (!creditCardInfoMap.isEmpty()) {
                creditCardInfoMap.clear();
            }
            CreditCardInfo creditCardInfo = new CreditCardInfo(creditCardNumber);
            creditCardInfoMap.put(creditCardInfo.getCreditCardNumber(), creditCardInfo);
        }
    }

    /**
     * Get creditCardInfo object for supplied creditCardNumber
     * 
     * @return creditCardInfo
     */
    public CreditCardInfo getCreditCard(String creditCardNumber) {
        if (!hasCreditCardInfo(creditCardNumber)) {
            throw new EmptyResultDataAccessException(
                    "No creditCardInfo exists with creditCardNumber='" + creditCardNumber + "' in account:" + this, 1);
        }

        return creditCardInfoMap.get(creditCardNumber);
    }

    /**
     * Get count of credit cards
     * 
     * @return count
     */
    public int getCreditCardInfoCount() {
        return creditCardInfoMap.size();
    }

    /**
     * Add a credit card info object
     *
     * @param creditCardInfo
     */
    public void addCreditCardInfo(CreditCardInfo creditCardInfo) {
        if (hasCreditCardInfo(creditCardInfo.getCreditCardNumber())) {
            throw new DuplicateAccountFieldsException(
                    "creditCard already exists on this account:" + this, Collections.singleton(
                    creditCardInfo.getCreditCardNumber()));
        }

        creditCardInfoMap.put(creditCardInfo.getCreditCardNumber(), creditCardInfo);
    }


    /**
     * Remove a credit card by name
     *
     * @param creditCardNumber
     */
    public void removeCreditCardInfo(String creditCardNumber) {
        if (!hasCreditCardInfo(creditCardNumber)) {
            throw new EmptyResultDataAccessException(
                    "No creditCardInfo exists with creditCardNumber='" + creditCardNumber + "' in account:" + this, 1);
        }

        creditCardInfoMap.remove(creditCardNumber);
    }

    /**
     * Create an account with equivalent values, except for entity ID
     *
     * @return account
     */
    public Account createAccount() {
        Account account = new Account(number, name, dateOfBirth, email,
                                      receiveNewsletter, receiveMonthlyEmailUpdate);

        for (String creditCardNumber : creditCardInfoMap.keySet()) {
            CreditCardInfo creditCardInfo = creditCardInfoMap.get(creditCardNumber);
            if (creditCardInfo.isActive()) {
                account.addCreditCard(creditCardInfo.createCreditCard());
            }
        }

        return account;
    }

    /**
     * Set corresponding properties in account object, except for entity ID and number
     * <p/>
     * Note that we don't update credit cards
     *
     * @param account
     */
    public void updateAccount(Account account) {
        if (getCreditCardInfoCount() < 1) {
            throw new AccountZeroCreditCardsException("No credit cards for account update:this=" + this);
        }

        account.setName(name);
        account.setDateOfBirth(dateOfBirth);
        account.setEmail(email);
        account.setReceiveNewsletter(receiveNewsletter);
        account.setReceiveMonthlyEmailUpdate(receiveMonthlyEmailUpdate);

        final Set<String> deletedCreditCardNumbers = new HashSet<String>(account.getCreditCardNumbers());
        deletedCreditCardNumbers.removeAll(creditCardInfoMap.keySet());

        for (String creditCardNumber : deletedCreditCardNumbers) {
            account.removeCreditCard(creditCardNumber);
        }

        final Set<String> newCreditCardNumbers = new HashSet<String>(creditCardInfoMap.keySet());
        newCreditCardNumbers.removeAll(account.getCreditCardNumbers());

        for (String creditCardNumber : newCreditCardNumbers) {
            final CreditCardInfo creditCardInfo = creditCardInfoMap.get(creditCardNumber);
            if (creditCardInfo.isActive()) {
                account.addCreditCard(creditCardInfo.createCreditCard());
            }
        }
    }

    /**
     * equals using all but entityId
     *
     * @param otherObj
     * @return
     */
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        if (!(otherObj instanceof AccountInfo)) {
            return false;
        }

        AccountInfo other = (AccountInfo) otherObj;

        if (receiveMonthlyEmailUpdate != other.receiveMonthlyEmailUpdate) {
            return false;
        }
        if (receiveNewsletter != other.receiveNewsletter) {
            return false;
        }
        if (dateOfBirth != null ? !dateOfBirth.equals(other.dateOfBirth) : other.dateOfBirth != null) {
            return false;
        }
        if (email != null ? !email.equals(other.email) : other.email != null) {
            return false;
        }
        if (name != null ? !name.equals(other.name) : other.name != null) {
            return false;
        }
        if (number != null ? !number.equals(other.number) : other.number != null) {
            return false;
        }
        if (creditCardInfoMap != null ? !creditCardInfoMap.equals(other.creditCardInfoMap) :
            other.creditCardInfoMap != null) {
            return false;
        }

        return true;
    }

    /**
     * hashCode using number alone
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        int result = number != null ? number.hashCode() : 0;
        return result;
    }

    /**
     * toString using all fields
     *
     * @return string representation of state
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("{entityId=").append(entityId);
        sb.append(", number='").append(number).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", dateOfBirth=").append(dateOfBirth);
        sb.append(", email='").append(email).append('\'');
        sb.append(", receiveNewsletter=").append(receiveNewsletter);
        sb.append(", receiveMonthlyEmailUpdate=").append(receiveMonthlyEmailUpdate);
        sb.append(", creditCardInfoMap=").append(creditCardInfoMap);
        sb.append('}');
        return sb.toString();
    }
}
