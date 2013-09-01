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

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for Beneficiary
 *
 * @author Dominic North
 */
public class BeneficiaryTests {

    /**
     * Object under test
     */
    private Beneficiary beneficiary;

    /**
     * Test constructor (3 args) works
     */
    @Test
    public void testConstructorNameAllocSavings() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Assert.assertEquals("name", AccountTestData.TEST_BENEFICIARY_NAME_0_0, beneficiary.getName());
        Assert.assertEquals("allocationPercentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                            beneficiary.getAllocationPercentage());
        Assert.assertEquals("savings", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0, beneficiary.getSavings());
    }

    /**
     * Test constructor 2 args
     */
    @Test
    public void testConstructorNameAlloc() {
        Beneficiary beneficiary = new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0);
        Assert.assertNull("entityId is null", beneficiary.getEntityId());
        Assert.assertEquals("name", AccountTestData.TEST_BENEFICIARY_NAME_0_0, beneficiary.getName());
        Assert.assertEquals("allocationPercentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                            beneficiary.getAllocationPercentage());
        Assert.assertEquals("savings", MonetaryAmount.zero(), beneficiary.getSavings());
    }

    /**
     * Test credit
     */
    @Test
    public void testCredit() {
        Beneficiary beneficiary = createBeneficiary0_0();
        beneficiary.credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);
        Assert.assertEquals("savings credited", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0
                .add(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0), beneficiary.getSavings());
    }

    /**
     * Test equals when same
     */
    @Test
    public void testEqualsWhenSame() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Assert.assertTrue("same equal", beneficiary.equals(beneficiary));
    }

    /**
     * Test equals when same values including null entityId
     */
    @Test
    public void testEqualsWhenEqualIncludingNullId() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Beneficiary otherBeneficiary = createBeneficiary0_0();
        Assert.assertTrue("equal values equal", beneficiary.equals(otherBeneficiary));
        Assert.assertEquals("equal values equal hashCode", beneficiary.hashCode(), otherBeneficiary.hashCode());
    }

    /**
     * Test equals when same values including non entityId
     */
    @Test
    public void testEqualsWhenEqualIncludingNonNullId() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Beneficiary otherBeneficiary = createBeneficiary0_0();
        beneficiary.setEntityId(0);
        otherBeneficiary.setEntityId(0);
        Assert.assertTrue("equal values equal", beneficiary.equals(otherBeneficiary));
        Assert.assertEquals("equal values equal hashCode", beneficiary.hashCode(), otherBeneficiary.hashCode());
    }

    /**
     * Test equals when same values but different non-null entityIds
     */
    @Test
    public void testEqualsWhenEqualExcludingId() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Beneficiary otherBeneficiary = createBeneficiary0_0();
        beneficiary.setEntityId(0);
        Assert.assertTrue("equal values equal", beneficiary.equals(otherBeneficiary));
        Assert.assertTrue("equal values equal (reversed)", otherBeneficiary.equals(beneficiary));
        Assert.assertEquals("equal values equal hashCode", beneficiary.hashCode(), otherBeneficiary.hashCode());
    }

    /**
     * Test (not) equals when different name
     */
    @Test
    public void testNotEqualsWhenDifferentName() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Beneficiary otherBeneficiary = new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                                                       AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                       AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        Assert.assertFalse("not equal different name", beneficiary.equals(otherBeneficiary));
        Assert.assertFalse("not equal different name (reversed)", otherBeneficiary.equals(beneficiary));
    }

    /**
     * Test (not) equals when different allocation
     */
    @Test
    public void testNotEqualsWhenDifferentAllocation() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Beneficiary otherBeneficiary = new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                       AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                                                       AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        Assert.assertFalse("not equal different allocation", beneficiary.equals(otherBeneficiary));
    }

    /**
     * Test (not) equals when different savings
     */
    @Test
    public void testNotEqualsWhenDifferentSavings() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Beneficiary otherBeneficiary = new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                       AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                       AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);
        Assert.assertFalse("not equal different savings", beneficiary.equals(otherBeneficiary));
    }

    /**
     * Test (not) equals when null
     */
    @Test
    public void testNotEqualsWhenNull() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Assert.assertFalse("not equal null", beneficiary.equals(null));
    }

    /**
     * Ensure correct behaviour in a set i.e. hashCode doesn't change when mutable fields modified
     */
    @Test
    public void testHashSetBehaviour() {
        Beneficiary beneficiary = createBeneficiary0_0();
        Set<Beneficiary> beneficiarySet = new HashSet<Beneficiary>();
        beneficiarySet.add(beneficiary);

        Assert.assertTrue("beneficiarySet.contains(beneficiary) should be true", beneficiarySet.contains(beneficiary));

        beneficiary.setEntityId(100);
        Assert.assertTrue("beneficiarySet.contains(beneficiary) should be true after setEntityId",
                          beneficiarySet.contains(beneficiary));

        beneficiary.credit(new MonetaryAmount(10.0));
        Assert.assertTrue("beneficiarySet.contains(beneficiary) should be true after credit",
                          beneficiarySet.contains(beneficiary));

        beneficiary.setAllocationPercentage(AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0);
        Assert.assertTrue("beneficiarySet.contains(beneficiary) should be true after setAllocation",
                          beneficiarySet.contains(beneficiary));
    }

    /**
     * Test Jackson Json marshalling/unmarshalling for annotations in class What
     * goes in should come out!
     */
    @Test
    public void testJsonMarshallUnmarshall() throws Exception {
        Beneficiary beneficiary = createBeneficiary0_0();
        ObjectMapper mapper = new ObjectMapper();
        String marshalledVersion = mapper
                .writeValueAsString(beneficiary);

        Beneficiary unmarshalledBeneficiary = mapper.readValue(
                marshalledVersion, Beneficiary.class);
        Assert.assertEquals("original and unmarshalled beneficiary",
                            beneficiary, unmarshalledBeneficiary);
    }

    /**
     * Helper creating test beneficiary with AccountTestData.TEST_BENEFICIARY_*_0_0 values and null entityId
     *
     * @return new test beneficiary
     */
    private Beneficiary createBeneficiary0_0() {
        return new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                               new Percentage(AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0.asBigDecimal()),
                               new MonetaryAmount(AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0.asBigDecimal()));
    }
}
