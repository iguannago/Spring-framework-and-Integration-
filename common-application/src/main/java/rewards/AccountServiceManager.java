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

/**
 * Interface for account management where original account objects are used, intended for accounts.web-services, integration etc.
 *
 * @author Dominic North
 */
public interface AccountServiceManager extends AccountManagerBase {

    /**
     * Update account from account object, including its beneficiaries.
     *
     * @param
     */
    void update(Account account);

    /**
     * Save complete new account object, including any beneficiaries
     *
     * @param account object with new values
     */
    void create(Account account);

    /**
     * Add account beneficiary
     *
     * @param accountEntityId for parent account
     * @param beneficiaryName
     */
    void addAccountBeneficiary(Integer accountEntityId, String beneficiaryName);

    /**
     * Remove account beneficiary
     *
     * @param accountEntityId for parent account
     * @param beneficiaryName
     */
    void removeAccountBeneficiary(Integer accountEntityId, String beneficiaryName);
}
