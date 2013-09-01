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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rewards.AccountServiceManager;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;

import java.net.URI;
import java.util.Calendar;
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
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public @ResponseBody List<Account> accountSummary() {
        return accountManager.getAllAccounts();
    }


    /**
     * Provide the details of an account with the given id.
     */
    @RequestMapping(value = "/accounts/{accountId}", method = RequestMethod.GET)
    public @ResponseBody Account accountDetails(@PathVariable("accountId") int id) {
        return accountManager.getAccount(id);
    }


    /**
     * Creates a new Account, setting its URL as the Location header on the
     * response.
     */
    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccount(@RequestBody Account newAccount,
                                                @Value("#{request.requestURL}") String requestUri) {
        accountManager.create(newAccount);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromUriString(requestUri)
                                                       .pathSegment("{accountId}")
                                                       .buildAndExpand(newAccount.getEntityId())
                                                       .toUri());
        return new ResponseEntity<Object>(headers, HttpStatus.CREATED);
    }


    /**
     * Returns the Beneficiary with the given name for the Account with the
     * given id.
     */
    @RequestMapping(value = "/accounts/{accountId}/beneficiaries/{beneficiaryName}", method = RequestMethod.GET)
    public ResponseEntity<Beneficiary> getBeneficiary(
            @PathVariable("accountId") int accountId,
            @PathVariable("beneficiaryName") String beneficiaryName) {
        Beneficiary beneficiary = accountManager.getAccount(accountId).getBeneficiary(
                beneficiaryName);

        HttpHeaders headers = new HttpHeaders();
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.HOUR, 1);
        headers.setExpires(expiry.getTimeInMillis());

        ResponseEntity<Beneficiary> beneficiaryResponse = new ResponseEntity<Beneficiary>(
                beneficiary, headers, HttpStatus.OK);
        return beneficiaryResponse;
    }


    /**
     * Adds a Beneficiary with the given name to the Account with the given id,
     * setting its URL as the Location header on the response.
     */
    @RequestMapping(value = "/accounts/{accountId}/beneficiaries", method = RequestMethod.POST)
    public ResponseEntity<Object> addBeneficiary(@PathVariable("accountId") int accountId,
                                                 @RequestBody String beneficiaryName,
                                                 @Value("#{request.requestURL}") String requestUri) {
        accountManager.addAccountBeneficiary(accountId, beneficiaryName);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                ServletUriComponentsBuilder.fromUriString(requestUri).pathSegment(beneficiaryName).build().toUri());
        return new ResponseEntity<Object>(headers, HttpStatus.CREATED);
    }


    /**
     * Alternative signature to add a Beneficiary with the given name (from URL)
     * to the Account with the given id, setting its URL as the Location header
     * on the response.
     */
    @RequestMapping(value = "/accounts/{accountId}/beneficiaries/{beneficiaryName}", method = RequestMethod.POST)
    public ResponseEntity<Object> createBeneficiary(@PathVariable("accountId") int accountId,
                                                    @PathVariable("beneficiaryName") String beneficiaryName,
                                                    @Value("#{new java.net.URI(request.requestURL)}") URI locationUri) {
        accountManager.addAccountBeneficiary(accountId, beneficiaryName);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(locationUri);
        return new ResponseEntity<Object>(headers, HttpStatus.CREATED);
    }


    /**
     * Removes the Beneficiary with the given name from the Account with the
     * given id.
     */
    @RequestMapping(value = "/accounts/{accountId}/beneficiaries/{beneficiaryName}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBeneficiary(@PathVariable("accountId") int accountId,
                                  @PathVariable("beneficiaryName") String beneficiaryName) {
        accountManager.removeAccountBeneficiary(accountId, beneficiaryName);
    }


    /**
     * Maps EmptyResultDataAccessException, IllegalArgumentExceptions to a 404 Not Found HTTP status code.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public @ResponseBody String handleNotFound(EmptyResultDataAccessException ex) {
        return ex.getMessage();
    }


    /**
     * Maps DataIntegrityViolationException to a 409 Conflict HTTP status code.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public @ResponseBody String handleAlreadyExists(DataIntegrityViolationException ex) {
        return ex.getMessage();
    }

}