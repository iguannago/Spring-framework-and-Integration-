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

import java.util.List;

/**
 * Loads account aggregates. Called by the reward network to find and reconstitute Account entities from an external
 * form such as a set of RDBMS rows.
 * <p/>
 * Objects returned by this repository are guaranteed to be fully-initialized and ready to use.
 */
public interface AccountRepository {

    /**
     * Get all accounts in the system
     *
     * @return all accounts
     */
    List<Account> getAllAccounts();

    /**
     * Find an account by its id, or null if it doesn't exist.
     *
     * @param id the account id
     * @return the account
     */
    Account getAccount(Integer id);

    /**
     * Persist updated account attributes
     *
     * @param account object with new values, including appropriate entityId
     */
    void update(Account account);

    /**
     * Create new account from supplied DTO
     *
     * @param account object with new values
     */
    void create(Account account);

    /**
     * Check there is not already an account with the supplied name, so that it
     * can be used for creation
     *
     * @param accountName
     * @return true if it is available, false if it is already in use
     */
    boolean hasNoAccountWithName(final String accountName);
    /**
     * Check there is not already an account with the supplied number
     *
     * @param accountNumber
     * @return true if it is available, false if it is already in use
     */
    boolean hasNoAccountWithNumber(final String accountNumber);

    /**
     * Check there is not already an account using the supplied credit card number, so that it
     * can be used for creation of a new account
     *
     * @param creditCardNumber
     * @return true if it is un used, false if it is already in use
     */
    boolean hasNoAccountWithCreditCard(final String creditCardNumber);

    /**
     * Load an account by its account number.
     *
     * @param accountNumber the account number
     * @return the account object, or null, if not found
     */
    Account findByAccountNumber(String accountNumber);

    /**
     * Load an account by its credit card number.
     *
     * @param creditCardNumber the credit card number
     * @return the account object, or null, if not found
     */
    Account findByCreditCard(String creditCardNumber);

}