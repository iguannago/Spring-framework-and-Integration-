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

import java.util.List;

/**
 * Common base account management interface, with read-only methods for obtaining account object,
 * and checks on name and number availability.
 *
 * @author Dominic North
 */
public interface AccountManagerBase {
    /**
     * Get all accounts in the system
     *
     * @return all accounts
     */
    List<Account> getAllAccounts();

    /**
     * Find an account by its id, throw EmptyResultDataAccessException if not found
     *
     * @param id the account id
     * @return the account
     */
    Account getAccount(Integer id);

    /**
     * Check there is not already an account with the supplied name, so that it
     * can be used for creation
     *
     * @param accountName
     * @return true if it is available, false if it is already in use
     */
    boolean isAccountNameAvailableForCreate(String accountName);

    /**
     * Check there is not already a different account with the supplied name, so
     * that it can be used for updating the account with the specified ID
     *
     * @param id          of account to be updated
     * @param accountName
     * @return true if it is available, false if it is already in use for a different account
     */
    boolean isAccountNameAvailableForUpdate(Integer id,
                                            String accountName);

    /**
     * Check there is not already an account with the supplied number, so that
     * it can be used for creation
     *
     * @param accountNumber
     * @return true if it is available, false if it is already in use
     */
    boolean isAccountNumberAvailableForCreate(String accountNumber);

    /**
     * Check there is not already an account using the supplied credit card number, so that it
     * can be used for creation of a new account
     *
     * @param creditCardNumber
     * @return true if it is available, false if it is already in use
     */
    boolean isCreditCardAvailableForAccountCreate(final String creditCardNumber);

    /**
     * Check there is not already a different account using the supplied credit card number, so
     * that it can be used for updating the account with the specified ID. This is not intended to stop duplicate
     * entries of credit card on the same account.
     *
     * @param entityId         of account to be updated
     * @param creditCardNumber
     * @return true if it is available, false if it is already in use for a
     *         different account
     */
    boolean isCreditCardAvailableForAccountUpdate(Integer entityId, String creditCardNumber);
}
