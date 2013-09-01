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
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for beneficiaryInfo
 *
 * @author Dominic North
 */
public class BeneficiaryInfoTests {

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo();

        Assert.assertNull("name", beneficiaryInfo.getName());
        Assert.assertEquals("allocationPercentage", Percentage.zero(), beneficiaryInfo.getAllocationPercentage());
        Assert.assertEquals("savings", MonetaryAmount.zero(), beneficiaryInfo.getSavings());
    }

    /**
     * Test constructor
     */
    @Test
    public void testConstructorName() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0);

        Assert.assertEquals("name", AccountTestData.TEST_BENEFICIARY_NAME_0_0, beneficiaryInfo.getName());
        Assert.assertEquals("allocationPercentage", Percentage.zero(), beneficiaryInfo.getAllocationPercentage());
        Assert.assertEquals("savings", MonetaryAmount.zero(), beneficiaryInfo.getSavings());
    }

    /**
     * Test constructor taking name, allocation, savings
     */
    @Test
    public void testConstructorNameAllocationSavings() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        Assert.assertEquals("name", AccountTestData.TEST_BENEFICIARY_NAME_0_0, beneficiaryInfo.getName());
        Assert.assertEquals("allocationPercentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                            beneficiaryInfo.getAllocationPercentage());
        Assert.assertEquals("savings", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0, beneficiaryInfo.getSavings());
    }

    /**
     * Test constructor taking beneficiary
     */
    @Test
    public void testConstructorBeneficiary() {
        Beneficiary beneficiary = new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(beneficiary);

        Assert.assertEquals("name", AccountTestData.TEST_BENEFICIARY_NAME_0_0, beneficiaryInfo.getName());
        Assert.assertEquals("allocationPercentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                            beneficiaryInfo.getAllocationPercentage());
        Assert.assertEquals("savings", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0, beneficiaryInfo.getSavings());
    }

    /**
     * Test creation of beneficiary
     */
    @Test
    public void testCreateBeneficiary() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        Beneficiary beneficiary = beneficiaryInfo.createBeneficiary();
        Assert.assertEquals("name", AccountTestData.TEST_BENEFICIARY_NAME_0_0, beneficiary.getName());
        Assert.assertEquals("allocationPercentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                            beneficiary.getAllocationPercentage());
        Assert.assertEquals("savings", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0, beneficiary.getSavings());
    }

    /**
     * Test update of beneficiary changes allocation percentage only
     */
    @Test
    public void testUpdateBeneficiary() {
        Beneficiary beneficiary = new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        Beneficiary expectedBeneficiary = new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        expectedBeneficiary.credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);

        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(expectedBeneficiary);
        beneficiaryInfo.setAllocationPercentage(AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0);

        beneficiaryInfo.updateBeneficiary(beneficiary);

        Assert.assertEquals("name", AccountTestData.TEST_BENEFICIARY_NAME_0_0, beneficiaryInfo.getName());
        Assert.assertEquals("allocationPercentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                            beneficiary.getAllocationPercentage());
        Assert.assertEquals("savings", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0.add(
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0), beneficiary.getSavings());
    }

    /**
     * Test update of beneficiary with mis-match on name
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBeneficiaryDifferentName() {
        Beneficiary beneficiary = new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);

        beneficiaryInfo.updateBeneficiary(beneficiary);
    }


    /**
     * Test equals (same object)
     */
    @Test
    public void testEqualsWhenSame() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        Assert.assertTrue("equal when same", beneficiaryInfo.equals(beneficiaryInfo));
    }

    /**
     * Test equals (same values)
     */
    @Test
    public void testEqualsWhenEqual() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo otherBeneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                                   AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                                   AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        Assert.assertTrue("same values should be equal", beneficiaryInfo.equals(otherBeneficiaryInfo));
    }

    /**
     * Test (not) equals (null)
     */
    @Test
    public void testNotEqualsWhenOtherNull() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        Assert.assertFalse("not equal null", beneficiaryInfo.equals(null));
    }

    /**
     * Test (not) equals (different name)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentName() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo otherBeneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                                                                   AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                                   AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        Assert.assertFalse("name differs not equal", beneficiaryInfo.equals(otherBeneficiaryInfo));
    }

    /**
     * Test (not) equals (different allocation)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentAllocation() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo otherBeneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                                   AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                                                                   AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        Assert.assertFalse("allocation differs not equal", beneficiaryInfo.equals(otherBeneficiaryInfo));
    }

    /**
     * Test (not) equals (different savings)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentSavings() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo otherBeneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                                   AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                                   AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);
        Assert.assertFalse("savings differs not equal", beneficiaryInfo.equals(otherBeneficiaryInfo));
    }

    /**
     * Ensure correct behaviour in a set i.e. hashCode doesn't change when mutable fields modified except for name
     */
    @Test
    public void testHashSetBehaviour() {
        BeneficiaryInfo beneficiaryInfo = new BeneficiaryInfo(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        Set<BeneficiaryInfo> beneficiaryInfoSet = new HashSet<BeneficiaryInfo>();
        beneficiaryInfoSet.add(beneficiaryInfo);

        Assert.assertTrue("beneficiaryInfoSet.contains(beneficiaryInfo) should be true",
                          beneficiaryInfoSet.contains(beneficiaryInfo));

        beneficiaryInfo.setAllocationPercentage(AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0);
        Assert.assertTrue("beneficiaryInfoSet.contains(beneficiaryInfo) should be true after setAllocation",
                          beneficiaryInfoSet.contains(beneficiaryInfo));

        beneficiaryInfo.setName(AccountTestData.TEST_BENEFICIARY_NAME_1_0);
        Assert.assertFalse("beneficiaryInfoSet.contains(beneficiaryInfo) should be false after setName",
                          beneficiaryInfoSet.contains(beneficiaryInfo));
    }

}
