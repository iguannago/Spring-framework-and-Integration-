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

package rewards.messaging;

import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.annotation.Transactional;
import rewards.RewardConfirmation;
import rewards.internal.account.Account;
import rewards.internal.account.AccountRepository;

/**
 * Service to create e-mail for reward
 */
public class RewardMailMessageCreator implements RewardMailCreator {

    /**
     * Repository bean
     */
	private AccountRepository accountRepository;

    /**
     * Constructor taking account repository
     *
     * @param accountRepository
     */
	public RewardMailMessageCreator(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

    /**
     * Create e-mail message for confirmation
     *
     * @param confirmation
     * @return mail message
     */
	@Override
    @Transactional(readOnly = true)
    public MailMessage createMail(RewardConfirmation confirmation) {
		String accountNumber = confirmation.getAccountContribution().getAccountNumber();
		Account account = accountRepository.findByAccountNumber(accountNumber);
		
		MailMessage message = new SimpleMailMessage();
		message.setFrom("rewardnetwork@example.com");
		message.setTo(account.getEmail());
		message.setSubject("New Reward");
		message.setText("You've been rewarded with " + confirmation.getAccountContribution().getAmount());
		
		return message;
	}
}
