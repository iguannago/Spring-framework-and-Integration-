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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;


@ContextConfiguration("test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class IdempotentRewardNetworkIntegrationTests {

	Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567890");

	@Autowired RewardNetwork rewardNetwork;
	@Autowired MessagingTemplate template;

	@Test
    @Ignore
	public void idempotenceFindThenReward() throws Exception {
		RewardConfirmation confirmation = mock(RewardConfirmation.class);
        when(rewardNetwork.findConfirmationFor(dining)).thenReturn(null);
        when(rewardNetwork.rewardAccountFor(dining)).thenReturn(confirmation);
		// we are relying on default null-returning behavior of rewardRepository mock here
		template.convertAndSend("dinings", dining);
		
		RewardConfirmation firstConfirmation = (RewardConfirmation) template.receiveAndConvert("confirmations");

        when(rewardNetwork.findConfirmationFor(dining)).thenReturn(firstConfirmation);

        template.convertAndSend("dinings", dining);
		
		RewardConfirmation secondConfirmation = (RewardConfirmation) template.receiveAndConvert("confirmations");
		
		assertThat(secondConfirmation, is(firstConfirmation));
        verify(rewardNetwork, times(2)).findConfirmationFor(dining);
        verify(rewardNetwork, times(1)).rewardAccountFor(dining);
	}

    @Test
    //@Ignore
    public void idempotenceRewardThenFind() throws Exception {
        RewardConfirmation confirmation = mock(RewardConfirmation.class);
        when(rewardNetwork.rewardAccountFor(dining)).thenReturn(confirmation);
        // we are relying on default null-returning behavior of rewardRepository mock here
        template.convertAndSend("dinings", dining);

        RewardConfirmation firstConfirmation = (RewardConfirmation) template.receiveAndConvert("confirmations");

        when(rewardNetwork.rewardAccountFor(dining)).thenThrow(new DuplicateKeyException("dining already processed"));
        when(rewardNetwork.findConfirmationFor(dining)).thenReturn(firstConfirmation);

        template.convertAndSend("dinings", dining);

        RewardConfirmation secondConfirmation = (RewardConfirmation) template.receiveAndConvert("confirmations");

        assertThat(secondConfirmation, is(firstConfirmation));
        verify(rewardNetwork, times(2)).rewardAccountFor(dining);
        verify(rewardNetwork, times(1)).findConfirmationFor(dining);
    }

}
