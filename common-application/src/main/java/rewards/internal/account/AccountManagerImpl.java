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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rewards.AccountManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class managing accounts and their dependent entities.
 */
@Service("accountManager")
public class AccountManagerImpl implements AccountManager {

    private AccountRepository accountRepository;

    /**
     * Creates a new Hibernate account manager.
     *
     * @param accountRepository
     */
    public AccountManagerImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /**
     * Get all accounts
     *
     * @return list of accounts
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.getAllAccounts();
    }


    /**
     * Get account by ID, throw EmptyResultDataAccessException if not found
     *
     * @param id
     * @return account
     */
    @Override
    @Transactional(readOnly = true)
    public Account getAccount(Integer id) {
        Account account = accountRepository.getAccount(id);
        if (account == null) {
            throw new EmptyResultDataAccessException("no account found for id=" + id, 1);
        }
        return account;
    }


    /**
     * Save complete new account object
     *
     * @param account object with new values
     */
    @Override
    @Transactional
    public void create(Account account) {
        final String duplicateAccountNumber = (isAccountNumberAvailableForCreate(account.getNumber()) ?
                                               null : account.getNumber());
        final String duplicateAccountName = (isAccountNameAvailableForCreate(account.getName()) ?
                                             null : account.getName());
        final Set<String> duplicateCardNumbers = getDuplicateCardNumbers(account.getCreditCardNumbers());

        if (duplicateAccountNumber != null || duplicateAccountName != null || duplicateCardNumbers != null) {
            throw new DuplicateAccountFieldsException("account=" + account,
                                                      duplicateAccountNumber,
                                                      duplicateAccountName,
                                                      duplicateCardNumbers);
        }

        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(account);
        accountBeneficiaryInfo.validateAllocations();

        accountRepository.create(account);
    }


    /**
     * Update account from account object, including the beneficiaries.
     *
     * @param account
     */
    @Override
    @Transactional
    public void update(Account account) {
        Account storedAccount = getAccount(account.getEntityId());

        final String duplicateAccountName = (isAccountNameAvailableForUpdateInternal(account.getName(),
                                                                                     storedAccount) ?
                                             null : account.getName());

        final Set<String> newCreditCardNumbers = account.getCreditCardNumbers();
        newCreditCardNumbers.removeAll(storedAccount.getCreditCardNumbers());

        final Set<String> duplicateCardNumbers = getDuplicateCardNumbers(newCreditCardNumbers);

        if (duplicateAccountName != null || duplicateCardNumbers != null) {
            throw new DuplicateAccountFieldsException("account=" + account + ":storedAccount=" + storedAccount,
                                                      duplicateAccountName,
                                                      duplicateCardNumbers);
        }

        AccountInfo accountInfo = new AccountInfo(account);
        AccountBeneficiaryInfo accountbeneficiaryInfo = new AccountBeneficiaryInfo(account);

        accountInfo.updateAccount(storedAccount);
        accountbeneficiaryInfo.updateAccountBeneficiaries(storedAccount);

        accountRepository.update(storedAccount);
    }


    /**
     * Add account beneficiary
     *
     * @param accountEntityId for parent account
     * @param beneficiaryName
     */
    @Override
    @Transactional
    public void addAccountBeneficiary(Integer accountEntityId, String beneficiaryName) {
        Account account = getAccount(accountEntityId);
        account.addBeneficiary(beneficiaryName);
        accountRepository.update(account);
    }


    /**
     * Remove account beneficiary
     *
     * @param accountEntityId for parent account
     * @param beneficiaryName
     */
    @Override
    @Transactional
    public void removeAccountBeneficiary(Integer accountEntityId, String beneficiaryName) {
        Account account = getAccount(accountEntityId);
        account.removeBeneficiary(beneficiaryName);
        accountRepository.update(account);
    }


    /**
     * Get accountInfo by account entityId, throw EmptyResultDataAccessException if not found
     *
     * @param id
     * @return account DTO
     */
    @Override
    @Transactional(readOnly = true)
    public AccountInfo getAccountInfo(Integer id) {
        return new AccountInfo(getAccount(id));
    }


    /**
     * Update account from supplied accountInfo. We first check for duplicate
     * name, to avoid difficult parsing of integrity violation exceptions.
     * The supplied accountInfo should have a valid entityId.
     * <ul>
     * <li>We throw EmptyResultDataAccessException if the account does not exist</li>
     * <li>We throw DuplicateAccountName if the account does not exist</li>
     * </ul>
     *
     * @param accountInfo
     */
    @Transactional
    @Override
    public void updateAccountInfo(AccountInfo accountInfo) {
        Account storedAccount = getAccount(accountInfo.getEntityId());

        final String duplicateAccountName = (isAccountNameAvailableForUpdateInternal(accountInfo.getName(),
                                                                                     storedAccount) ?
                                             null : accountInfo.getName());

        final Set<String> newCreditCardNumbers = new HashSet<String>(accountInfo.getCreditCardNumbers());
        newCreditCardNumbers.removeAll(storedAccount.getCreditCardNumbers());

        final Set<String> duplicateCardNumbers = getDuplicateCardNumbers(newCreditCardNumbers);

        if (duplicateAccountName != null || duplicateCardNumbers != null) {
            throw new DuplicateAccountFieldsException("accountInfo=" + accountInfo + ":storedAccount=" + storedAccount,
                                                      duplicateAccountName,
                                                      duplicateCardNumbers);
        }

        accountInfo.updateAccount(storedAccount);
        accountRepository.update(storedAccount);
    }


    /**
     * Create new account from supplied accountInfo. We first check for
     * duplicate name or number (or both), to avoid difficult parsing of
     * integrity violation exceptions.
     *
     * @param accountInfo object with new values
     * @return new account
     */
    @Override
    @Transactional
    public Account createAccountInfo(AccountInfo accountInfo) {

        Account newAccount = accountInfo.createAccount();
        create(newAccount);

        return newAccount;
    }


    /**
     * Check there is not already an account with the supplied name, so that it
     * can be used for creation
     *
     * @param accountName
     * @return true if it is available, false if it is already in use
     * @see rewards.AccountManager#isAccountNameAvailableForCreate(String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAccountNameAvailableForCreate(final String accountName) {
        return accountRepository.hasNoAccountWithName(accountName);
    }


    /**
     * Check there is not already a different account with the supplied name, so
     * that it can be used for updating the account with the specified ID
     *
     * @param id          of account to be updated
     * @param accountName
     * @return true if it is available, false if it is already in use for a
     *         different account
     * @see rewards.AccountManager#isAccountNameAvailableForUpdate(Integer,
     *      String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAccountNameAvailableForUpdate(Integer id, String accountName) {
        return isAccountNameAvailableForUpdateInternal(accountName, getAccount(id));
    }


    /**
     * Check there is not already an account with the supplied number, so that
     * it can be used for creation
     *
     * @param accountNumber
     * @return true if it is available, false if it is already in use
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAccountNumberAvailableForCreate(
            final String accountNumber) {
        return accountRepository.hasNoAccountWithNumber(accountNumber);
    }


    /**
     * Check there is not already a different account using the supplied credit card number, so
     * that it can be used for updating the account with the specified ID. This is not intended to stop duplicate
     * entries of credit card on the same account, which can only occur and be detected on the client.
     *
     * @param entityId         of account to be updated
     * @param creditCardNumber
     * @return true if it is available, false if it is already in use for a
     *         different account
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isCreditCardAvailableForAccountUpdate(Integer entityId, String creditCardNumber) {
        return isCreditCardAvailableForAccountUpdateInternal(creditCardNumber, getAccount(entityId));
    }


    /**
     * Check there is not already an account using the supplied credit card number, so that it
     * can be used for creation of a new account
     *
     * @param creditCardNumber
     * @return true if it is available, false if it is already in use
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isCreditCardAvailableForAccountCreate(String creditCardNumber) {
        return accountRepository.hasNoAccountWithCreditCard(creditCardNumber);
    }


    /**
     * Get accountBeneficiaryInfo created from account with supplied entityId.
     *
     * @param id
     * @return account
     */
    @Override
    @Transactional(readOnly = true)
    public AccountBeneficiaryInfo getAccountBeneficiaryInfo(Integer id) {
        return new AccountBeneficiaryInfo(getAccount(id));
    }


    /**
     * Update account beneficiaries, throw EmptyResultDataAccessException if Account not found
     *
     * @param accountBeneficiaryInfo with entityId for account, revised beneficaries
     */
    @Override
    @Transactional
    public void updateAccountBeneficiaries(
            AccountBeneficiaryInfo accountBeneficiaryInfo) {
        Account account = getAccount(accountBeneficiaryInfo
                                             .getAccountEntityId());
        accountBeneficiaryInfo.updateAccountBeneficiaries(account);
        accountRepository.update(account);
    }


    /**
     * Internal helper checking name availability for given account
     *
     * @param accountName
     * @param account
     * @return true or false
     */
    private boolean isAccountNameAvailableForUpdateInternal(String accountName, Account account) {
        boolean isAvailable = account.getName().equals(accountName);
        if (!isAvailable) {
            isAvailable = accountRepository.hasNoAccountWithName(accountName);
        }
        return isAvailable;
    }


    /**
     * Get set of duplicate card numbers
     *
     * @param newCreditCardNumbers all creditCardNumbers not already associated with the account
     *                             where we wish to use them
     * @return set of strings (or null if none)
     */
    private Set<String> getDuplicateCardNumbers(Set<String> newCreditCardNumbers) {
        Set<String> usedCardNumbers = null;
        for (String creditCardNumber : newCreditCardNumbers) {
            if (!accountRepository.hasNoAccountWithCreditCard(creditCardNumber)) {
                if (usedCardNumbers == null) {
                    usedCardNumbers = new HashSet<String>();
                }
                usedCardNumbers.add(creditCardNumber);
            }
        }
        return usedCardNumbers;
    }


    /**
     * Internal helper checking creditCardNumber availability for given account
     *
     * @param creditCardNumber
     * @param account
     * @return
     */
    private boolean isCreditCardAvailableForAccountUpdateInternal(String creditCardNumber, Account account) {
        boolean isAvailable = account.hasCreditCard(creditCardNumber);
        if (!isAvailable) {
            isAvailable = accountRepository.hasNoAccountWithCreditCard(creditCardNumber);
        }
        return isAvailable;
    }

}