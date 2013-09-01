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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DTO for Account beneficiary information
 *
 * @author djnorth
 */
public class AccountBeneficiaryInfo implements Serializable {

    /**
     * 100 as BigDecimal
     */
    public static final BigDecimal BIG_DECIMAL_100 = new BigDecimal(100);

    /**
     * Parent account entityId
     */
    private final Integer accountEntityId;

    /**
     * Parent account name
     */
    private final String accountName;

    /**
     * Parent account number
     */
    private final String accountNumber;

    /**
     * Beneficiaries on account
     */
    private final Map<String, BeneficiaryInfo> beneficiaryMap = new HashMap<String, BeneficiaryInfo>();

    /**
     * Constructor taking mandatory fields
     *
     * @param accountEntityId
     * @param accountName
     * @param accountNumber
     */
    public AccountBeneficiaryInfo(Integer accountEntityId, String accountName,
                                  String accountNumber) {
        this.accountEntityId = accountEntityId;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }

    /**
     * Constructor taking account
     *
     * @param account
     */
    public AccountBeneficiaryInfo(Account account) {
        this.accountEntityId = account.getEntityId();
        this.accountName = account.getName();
        this.accountNumber = account.getNumber();

        for (Beneficiary beneficiary : account.getBeneficiaries()) {
            beneficiaryMap.put(beneficiary.getName(), new BeneficiaryInfo(
                    beneficiary));
        }
    }

    /**
     * @return the accountEntityId
     */
    public Integer getAccountEntityId() {
        return accountEntityId;
    }

    /**
     * @return the accountName
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Add a BeneficaryInfo object to our map
     *
     * @param beneficiaryInfo
     */
    public void addBeneficiary(BeneficiaryInfo beneficiaryInfo) {
        final String beneficiaryName = beneficiaryInfo.getName();
        if (beneficiaryMap.containsKey(beneficiaryName)) {
            throw new DuplicateBeneficiaryNameException("new beneficiary="
                                               + beneficiaryInfo + ":existing beneficiary="
                                               + beneficiaryMap.get(beneficiaryName), beneficiaryName);
        }
        beneficiaryMap.put(beneficiaryInfo.getName(), beneficiaryInfo);
    }

    /**
     * Create and a BeneficaryInfo object with supplied name to our map
     *
     * @param beneficiaryName
     */
    public void addBeneficiary(String beneficiaryName) {
        addBeneficiary(new BeneficiaryInfo(beneficiaryName));
    }

    /**
     * Get beneficaryInfo with supplied name
     *
     * @param beneficiaryName
     * @return benefiaryInfo (or null if not found)
     */
    public BeneficiaryInfo getBeneficiary(String beneficiaryName) {
        BeneficiaryInfo beneficiaryInfo = beneficiaryMap.get(beneficiaryName);
        if (beneficiaryInfo == null) {
            throw new EmptyResultDataAccessException(
                    "No beneficiaryInfo exists with beneficiaryName='" + beneficiaryName + "' in account:" + this, 1);
        }

        return beneficiaryInfo;
    }

    /**
     * Remove beneficary with supplied name
     *
     * @param beneficiaryName
     * @return benefiaryInfo that was removed (or null if not found)
     */
    public BeneficiaryInfo removeBeneficiary(String beneficiaryName) {
        BeneficiaryInfo beneficiaryInfo = beneficiaryMap.remove(beneficiaryName);
        if (beneficiaryInfo == null) {
            throw new EmptyResultDataAccessException(
                    "No beneficiaryInfo exists with beneficiaryName='" + beneficiaryName + "' in account:" + this, 1);
        }

        return beneficiaryInfo;
    }

    /**
     * Return beneficiaries as set
     *
     * @return the beneficiaries
     */
    public Set<BeneficiaryInfo> getBeneficiaries() {
        return new HashSet<BeneficiaryInfo>(beneficiaryMap.values());
    }

    /**
     * Get total allocations as a BigDecimal
     *
     * @return total
     */
    public BigDecimal getTotalAllocation() {
        BigDecimal totalAllocation = BigDecimal.ZERO;

        for (BeneficiaryInfo beneficiary : beneficiaryMap.values()) {
            BigDecimal alloc = beneficiary.getAllocationPercentage()
                                          .asBigDecimal();
            totalAllocation = totalAllocation.add(alloc);
        }

        return totalAllocation;
    }


    /**
     * Get count of beneficiaries
     *
     * @return count
     */
    public int getBeneficiaryCount() {
        return (beneficiaryMap == null ? 0 : beneficiaryMap.size());
    }

    /**
     * Update account beneficiaries from what we have with the same name.
     * <p/>
     * <ul>
     * <li>For a beneficiary matching one of our names, we updateAccountInfo it (allocation
     * percentage only)</li>
     * <li>We remove any non-matching beneficiaries</li>
     * <li>We add new beneficiaries for any we have that the account does not</li>
     * </ul>
     *
     * @param account
     */
    public void updateAccountBeneficiaries(Account account) {

        validateAllocations();

        Set<String> newBeneficiaryNames = new HashSet<String>(
                beneficiaryMap.keySet());
        Set<String> deletedBeneficiaryNames = new HashSet<String>();

        for (Beneficiary beneficiary : account.getBeneficiaries()) {
            final String beneficiaryName = beneficiary.getName();
            if (beneficiaryMap.containsKey(beneficiaryName)) {
                beneficiaryMap.get(beneficiaryName).updateBeneficiary(
                        beneficiary);
                newBeneficiaryNames.remove(beneficiaryName);
            } else {
                deletedBeneficiaryNames.add(beneficiaryName);
            }
        }

        for (String beneficiaryName : newBeneficiaryNames) {
            account.addBeneficiary(beneficiaryName);
            beneficiaryMap.get(beneficiaryName).updateBeneficiary(account.getBeneficiary(beneficiaryName));
        }

        for (String beneficiaryName : deletedBeneficiaryNames) {
            account.removeBeneficiary(beneficiaryName);
        }
    }

    /**
     * validate allocations, and throw InvalidBeneficiaryAllocationsException exception if invalid
     */
    public void validateAllocations() {
        if (!isValidAllocationTotal()) {
            BigDecimal allocationsTotalPc = getTotalAllocation().multiply(BIG_DECIMAL_100);
            throw new InvalidBeneficiaryAllocationsException(
                    "total allocations gt 100%:total=" + allocationsTotalPc + ":this=" + this);
        }
    }

    /**
     * Check that allocations are valid
     *
     * @return true if le 100%, false if gt 100%
     */
    public boolean isValidAllocationTotal() {
        return (getTotalAllocation().compareTo(BigDecimal.ONE) <= 0);
    }

    /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#hashCode()
      */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((accountName == null) ? 0 : accountName.hashCode());
        result = prime * result
                 + ((accountNumber == null) ? 0 : accountNumber.hashCode());
        return result;
    }

    /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#equals(java.lang.Object)
      */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AccountBeneficiaryInfo)) {
            return false;
        }
        AccountBeneficiaryInfo other = (AccountBeneficiaryInfo) obj;
        if (accountName == null) {
            if (other.accountName != null) {
                return false;
            }
        } else if (!accountName.equals(other.accountName)) {
            return false;
        }
        if (accountNumber == null) {
            if (other.accountNumber != null) {
                return false;
            }
        } else if (!accountNumber.equals(other.accountNumber)) {
            return false;
        }
        if (beneficiaryMap == null) {
            if (other.beneficiaryMap != null) {
                return false;
            }
        } else if (!beneficiaryMap.equals(other.beneficiaryMap)) {
            return false;
        }
        return true;
    }

    /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#toString()
      */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[accountEntityId=");
        builder.append(accountEntityId);
        builder.append(", accountName=");
        builder.append(accountName);
        builder.append(", accountNumber=");
        builder.append(accountNumber);
        builder.append(", beneficiaries=");
        builder.append(beneficiaryMap);
        builder.append("]");
        return builder.toString();
    }

}
