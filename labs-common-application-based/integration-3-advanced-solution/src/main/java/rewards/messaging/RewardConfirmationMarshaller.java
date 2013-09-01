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

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import rewards.AccountContribution;
import rewards.RewardConfirmation;

public class RewardConfirmationMarshaller implements Marshaller {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(RewardConfirmation.class);
	}

	@Override
	public void marshal(Object graph, Result result) throws XmlMappingException, IOException {
		RewardConfirmation rewardConfirmation = (RewardConfirmation) graph;
        AccountContribution contribution = rewardConfirmation.getAccountContribution();

		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (Throwable e) {
			throw new MarshallingFailureException("Can't create new Document", e);
		}
		Element elConfirmation = (Element) doc.appendChild(doc.createElement("reward-confirmation"));
        elConfirmation.setAttribute("confirmation-number", rewardConfirmation.getConfirmationNumber());
        
        Element elContribution = (Element) elConfirmation.appendChild(doc.createElement("account-contribution"));
        elContribution.setAttribute("account-number", contribution.getAccountNumber());
        elContribution.setAttribute("amount", contribution.getAmount().asBigDecimal().toString());

        Set<AccountContribution.Distribution> distributions = contribution.getDistributions();
        Element elDistributions = (Element) elContribution.appendChild(doc.createElement("distributions"));
        if (distributions != null) {
            for (AccountContribution.Distribution distribution : distributions) {
                Element elDistribution = (Element) elDistributions.appendChild(doc.createElement("distribution"));
                elDistribution.setAttribute("beneficiary", distribution.getBeneficiary());
                elDistribution.setAttribute("amount", distribution.getAmount().asBigDecimal().toString());
                elDistribution.setAttribute("percentage", distribution.getPercentage().asBigDecimal().toString());
                elDistribution.setAttribute("total-savings", distribution.getTotalSavings().asBigDecimal().toString());
            }
        }

		if (result instanceof DOMResult) {
			((DOMResult) result).setNode(doc);
		} else {
			throw new IllegalArgumentException("Got instance of "
				+ result.getClass().getName()
				+ " which is not supported");
		}

	}
}
