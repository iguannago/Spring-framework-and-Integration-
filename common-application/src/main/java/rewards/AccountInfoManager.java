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

package rewards;

import rewards.internal.account.Account;
import rewards.internal.account.AccountBeneficiaryInfo;
import rewards.internal.account.AccountInfo;

/**
 * Interface for account management using DTO/form-object classes, intended for human UI usage
 * e.g. with Spring MVC and Webflow.
 *
 * @author Dominic North
 */
public interface AccountInfoManager extends AccountManagerBase {

    /**
     * Get accountInfo by account entityId, throw EmptyResultDataAccessException if not found
     *
     * @param id
     * @return account DTO
     */
    AccountInfo getAccountInfo(Integer id);

    /**
     * Update account attributes from accountInfo DTO. This preserves beneficiary info.
     *
     * @param accountInfo object with new values, including appropriate entityId
     */
    void updateAccountInfo(AccountInfo accountInfo);

    /**
     * Create new account from supplied accountInfo, so with 0 beneficiaries
     *
     * @param accountInfo object with new values
     * @return new account
     */
    Account createAccountInfo(AccountInfo accountInfo);

    /**
     * Get accountBeneficiaryInfo created from account with supplied entityId.
     *
     * @param id
     * @return account
     */
    AccountBeneficiaryInfo getAccountBeneficiaryInfo(Integer id);

    /**
     * Update account beneficiaries from DTO. This preserves the account attributes
     *
     * @param accountBeneficiaryInfo with revise beneficaries
     */
    void updateAccountBeneficiaries(
            AccountBeneficiaryInfo accountBeneficiaryInfo);
}
