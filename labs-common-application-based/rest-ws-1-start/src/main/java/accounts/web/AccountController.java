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

package accounts.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriTemplate;
import rewards.AccountServiceManager;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * A controller handling requests for CRUD operations on Accounts and their
 * Beneficiaries.
 */
@Controller
public class AccountController {

    private AccountServiceManager accountManager;

    /**
     * Creates a new AccountController with a given account manager.
     */
    @Autowired
    public AccountController(AccountServiceManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * Provide a list of all accounts.
     */
    // TODO 01: Complete this method by adding the appropriate annotations to respond
    //          to a GET to /accounts and return a List<Account> to be converted
    public List<Account> accountSummary() {
        return accountManager.getAllAccounts();
    }

    /**
     * Provide the details of an account with the given id.
     */
    // TODO 03: Complete this method by adding the appropriate annotations to respond
    //          to a GET to /accounts/{accountId} and return an Account to be converted
    public Account accountDetails(int id) {
        return accountManager.getAccount(id);
    }


    /**
     * Creates a new Account, setting its URL as the Location header on the
     * response.
     */
    // TODO 05: Complete this method by adding the appropriate annotations to get the account and URL
    public ResponseEntity<Object> createAccount(Account newAccount, String requestUri) {
        accountManager.create(newAccount);
        // TODO 06: Set the Location header to the location of the created account, and set 201 created status

        final ResponseEntity<Object> responseEntity = null;
        return responseEntity;
    }


    /**
     * Returns the Beneficiary with the given name for the Account with the
     * given id.
     */
    // TODO 09: Complete this method by adding the appropriate annotations and code
    public Beneficiary getBeneficiary(int accountId, String beneficiaryName) {
        Beneficiary beneficiary = accountManager.getAccount(accountId).getBeneficiary(beneficiaryName);
        return beneficiary;
    }


    /**
     * Adds a Beneficiary with the given name to the Account with the given id,
     * setting its URL as the Location header on the response.
     */
    // TODO 11: Complete this method by adding the appropriate annotations and code
    public ResponseEntity<Object> addBeneficiary(int accountId, String beneficiaryName, String requestUri) {
        accountManager.addAccountBeneficiary(accountId, beneficiaryName);
        // TODO 12: Set the Location header on the Response to the location of the created beneficiary

        final ResponseEntity<Object> responseEntity = null;
        return responseEntity;
    }


    /**
     * Removes the Beneficiary with the given name from the Account with the
     * given id.
     */
    // TODO 14: Complete this method by adding the appropriate annotations and code
    public void removeBeneficiary(int accountId, String beneficiaryName) {
        accountManager.removeAccountBeneficiary(accountId, beneficiaryName);
    }


    /**
     * Maps EmptyResultDataAccessException, IllegalArgumentExceptions to a 404 Not Found HTTP status code.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EmptyResultDataAccessException.class})
    public @ResponseBody String handleNotFound(Exception ex) {
        return ex.getMessage();
    }


    // TODO 21: Add a new exception-handling method that maps
    // DataIntegrityViolationExceptions to a 409 Conflict status code, with exception message in response body
}