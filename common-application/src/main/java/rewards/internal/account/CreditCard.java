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

import javax.persistence.*;

/**
 * Class representing a creditCard
 *
 * @author Dominic North
 */
@Entity
@Table(name = "T_ACCOUNT_CREDIT_CARD")
public class CreditCard {

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Integer id;

    @Column(name = "NUMBER")
    private String creditCardNumber;

    @Version
    private int version;

    /**
     * Default constructor for Hibernate
     */
    private CreditCard() {
    }

    /**
     * Constructor taking creditCardNumber
     *
     * @param creditCardNumber
     */
    public CreditCard(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }


    /**
     * @return entityId
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the entity ID
     *
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return credit card creditCardNumber
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * equals based on creditCardNumber
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreditCard)) {
            return false;
        }

        CreditCard that = (CreditCard) o;

        if (creditCardNumber != null ? !creditCardNumber.equals(that.creditCardNumber) :
            that.creditCardNumber != null) {
            return false;
        }

        return true;
    }

    /**
     * hashCode based on creditCardNumber
     *
     * @return
     */
    @Override
    public int hashCode() {
        return creditCardNumber != null ? creditCardNumber.hashCode() : 0;
    }

    /**
     * toString for everything
     *
     * @return string representation of object
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CreditCard");
        sb.append("{id=").append(id);
        sb.append(", creditCardNumber='").append(creditCardNumber).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
