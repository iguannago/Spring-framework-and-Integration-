/**
 *
 */
package rewards.internal.account;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author djnorth
 */
public class AccountBeneficiaryInfoTests {

    static final private MonetaryAmount MA_ZERO = MonetaryAmount.zero();
    static final private Percentage     PC_ZERO = Percentage.zero();

    /**
     * Test method for
     * {@link rewards.internal.account.AccountBeneficiaryInfo#AccountBeneficiaryInfo(Integer, String, String)}
     * .
     */
    @Test
    public void testAccountBeneficiaryInfoIntegerStringString() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_NUMBER_0);

        AccountTestData.verifyAccountBeneficiaryInfo0(accountBeneficiaryInfo, 0);
    }

    /**
     * Test method for
     * {@link rewards.internal.account.AccountBeneficiaryInfo#AccountBeneficiaryInfo(Account)}
     * .
     */
    @Test
    public void testAccountBeneficiaryInfoAccount() {
        Account account = AccountTestData.createTestAccount0();

        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);

        AccountTestData.verifyAccountBeneficiaryInfo0(accountBeneficiaryInfo, 2);
        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
    }

    /**
     * Test {@link AccountBeneficiaryInfo#getBeneficiary(String)} with valid name
     */
    @Test
    public void testGetBeneficiaryInfoExists() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        BeneficiaryInfo expectedBeneficiaryInfo = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        accountBeneficiaryInfo.addBeneficiary(expectedBeneficiaryInfo);

        BeneficiaryInfo beneficiaryInfo =
                accountBeneficiaryInfo.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);

        Assert.assertEquals("returned beneficiaryInfo", beneficiaryInfo, expectedBeneficiaryInfo);
    }

    /**
     * Test method for
     * {@link rewards.internal.account.AccountBeneficiaryInfo#addBeneficiary(BeneficiaryInfo)}
     * .
     */
    @Test
    public void testAddBeneficiaryBeneficiaryInfo() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);

        AccountTestData.verifyAccountBeneficiaryInfo0(accountBeneficiaryInfo, 1);
        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        BeneficiaryInfo beneficiaryInfo1 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo1);

        AccountTestData.verifyAccountBeneficiaryInfo0(accountBeneficiaryInfo, 2);
        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
    }


    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#addBeneficiary(BeneficiaryInfo)}
     * .
     */
    @Test
    public void testAddBeneficiaryBeneficiaryInfoDuplicateName() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);
        BeneficiaryInfo beneficiaryInfo1 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);

        try {
            accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo1);
            Assert.fail("expected duplicate name for beneficiaryInfo1="
                        + beneficiaryInfo1);
        } catch (DuplicateBeneficiaryNameException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include beneficiary name:"
                              + exmsg,
                              exmsg.contains("'" + AccountTestData.TEST_BENEFICIARY_NAME_0_0 + "'"));
        }
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#addBeneficiary(String)}.
     */
    @Test
    public void testAddBeneficiaryString() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        accountBeneficiaryInfo
                .addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);

        AccountTestData.verifyAccountBeneficiaryInfo0(accountBeneficiaryInfo, 1);
        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_0, PC_ZERO, MA_ZERO);

        accountBeneficiaryInfo
                .addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1);

        AccountTestData.verifyAccountBeneficiaryInfo0(accountBeneficiaryInfo, 2);
        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_0, PC_ZERO, MA_ZERO);
        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_1, PC_ZERO, MA_ZERO);
    }


    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#addBeneficiary(BeneficiaryInfo)}
     * .
     */
    @Test
    public void testAddBeneficiaryBeneficiaryStringDuplicateName() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        accountBeneficiaryInfo.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);

        try {
            accountBeneficiaryInfo.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);
            Assert.fail("expected duplicate name for "
                        + AccountTestData.TEST_BENEFICIARY_NAME_0_0);
        } catch (DuplicateBeneficiaryNameException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include beneficiary name:"
                              + exmsg,
                              exmsg.contains("'" + AccountTestData.TEST_BENEFICIARY_NAME_0_0 + "'"));
        }
    }

    /**
     * Test {@link AccountBeneficiaryInfo#getBeneficiary(String)} with invalid name, account has 1 beneficiary
     */
    @Test
    public void testGetBeneficiaryInfoNotExist1Beneficiary() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        BeneficiaryInfo expectedBeneficiaryInfo = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        accountBeneficiaryInfo.addBeneficiary(expectedBeneficiaryInfo);

        try {
            BeneficiaryInfo beneficiaryInfo =
                    accountBeneficiaryInfo.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1);
            Assert.fail("expected EmptyResultDataAccessException for non-existent name:accountBeneficiaryInfo=" +
                        accountBeneficiaryInfo);
        } catch (EmptyResultDataAccessException ex) {
            final String msg = "beneficiaryName='" + AccountTestData.TEST_BENEFICIARY_NAME_0_1 + "'";
            Assert.assertTrue("exception message should include diagnostic '" + msg + "':" + ex.getMessage(), ex.getMessage().contains(msg));
        }
    }

    /**
     * Test {@link AccountBeneficiaryInfo#getBeneficiary(String)} with invalid name, account has 0 beneficiaries
     */
    @Test
    public void testGetBeneficiaryInfoNotExist0Beneficiaries() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        try {
            BeneficiaryInfo beneficiaryInfo =
                    accountBeneficiaryInfo.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);
            Assert.fail("expected EmptyResultDataAccessException for non-existent name:accountBeneficiaryInfo=" +
                        accountBeneficiaryInfo);
        } catch (EmptyResultDataAccessException ex) {
            final String msg = "beneficiaryName='" + AccountTestData.TEST_BENEFICIARY_NAME_0_0 + "'";
            Assert.assertTrue("exception message should include diagnostic '" + msg + "':" + ex.getMessage(), ex.getMessage().contains(msg));
        }
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#removeBeneficiary(String)} valid name
     */
    @Test
    public void testRemoveBeneficiary() {
        Account account = AccountTestData.createTestAccount0();

        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);

        accountBeneficiaryInfo
                .removeBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);
        AccountTestData.verifyAccountBeneficiaryInfo0(accountBeneficiaryInfo, 1);
        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#removeBeneficiary(String)} invalid name
     */
    @Test
    public void testRemoveBeneficiaryNameNonExistent() {
        Account account = AccountTestData.createTestAccount0();

        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);

        try {
            accountBeneficiaryInfo.removeBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0);
            Assert.fail("expected EmptyResultDataAccessException for non-existent name:accountBeneficiaryInfo=" +
                        accountBeneficiaryInfo);
        } catch (EmptyResultDataAccessException ex) {
            final String msg = "beneficiaryName='" + AccountTestData.TEST_BENEFICIARY_NAME_1_0 + "'";
            Assert.assertTrue("exception message should include diagnostic '" + msg + "':" + ex.getMessage(), ex.getMessage().contains(msg));
        }
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#getBeneficiaries()}.
     */
    @Test
    public void testGetBeneficiaries() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo beneficiaryInfo1 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo1);
        Set<BeneficiaryInfo> beneficiariesSet = accountBeneficiaryInfo
                .getBeneficiaries();
        Assert.assertEquals("beneficiariesSet.size", 2, beneficiariesSet.size());

        Assert.assertTrue("beneficiariesSet includes "
                          + AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                          beneficiariesSet.contains(beneficiaryInfo0));
        Assert.assertTrue("beneficiariesSet includes "
                          + AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                          beneficiariesSet.contains(beneficiaryInfo1));
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#getBeneficiaryCount()} with 0 beneficiaries
     */
    @Test
    public void testGetBeneficiaryCount0Beneficiaries() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        Assert.assertEquals("beneficiary count", 0, accountBeneficiaryInfo.getBeneficiaryCount());
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#getBeneficiaryCount()} with 2 beneficiaries
     */
    @Test
    public void testGetBeneficiaryCount1Beneficiary() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);

        Assert.assertEquals("beneficiary count", 1, accountBeneficiaryInfo.getBeneficiaryCount());
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#getBeneficiaryCount()} with 2 beneficiaries
     */
    @Test
    public void testGetBeneficiaryCount2Beneficiaries() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo beneficiaryInfo1 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo1);

        Assert.assertEquals("beneficiary count", 2, accountBeneficiaryInfo.getBeneficiaryCount());
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#getTotalAllocation} with 0 beneficiaries
     */
    @Test
    public void testGetBeneficiaryTotalAllocation0Beneficiaries() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        BigDecimal expectedTotalAllocation = BigDecimal.ZERO;

        Assert.assertEquals("total allocation", expectedTotalAllocation, accountBeneficiaryInfo.getTotalAllocation());
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#getTotalAllocation} with 2 beneficiaries
     */
    @Test
    public void testGetBeneficiaryTotalAllocation1Beneficiary() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);

        BigDecimal expectedTotalAllocation = AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0.asBigDecimal();

        Assert.assertEquals("total allocation", expectedTotalAllocation, accountBeneficiaryInfo.getTotalAllocation());
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#getTotalAllocation} with 2 beneficiaries
     */
    @Test
    public void testGetBeneficiaryTotalAllocation2Beneficiaries() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo beneficiaryInfo1 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo1);

        BigDecimal expectedTotalAllocation = AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0.asBigDecimal().add(
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1.asBigDecimal());

        Assert.assertEquals("total allocation", expectedTotalAllocation, accountBeneficiaryInfo.getTotalAllocation());
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#getTotalAllocation} with 3 beneficiaries gt 100%
     */
    @Test
    public void testGetBeneficiaryTotalAllocation3BeneficiariesGt100pc() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo beneficiaryInfo1 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
        BeneficiaryInfo beneficiaryInfo2 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo1);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo2);

        BigDecimal expectedTotalAllocation =
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0.asBigDecimal().add(
                        AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1.asBigDecimal()).add(
                        AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0.asBigDecimal());

        Assert.assertEquals("total allocation", expectedTotalAllocation, accountBeneficiaryInfo.getTotalAllocation());
        Assert.assertTrue("total allocation gt 100%:" + expectedTotalAllocation,
                          expectedTotalAllocation.compareTo(BigDecimal.ONE) > 0);
    }

    /**
     * Test method for
     * {@link rewards.internal.account.AccountBeneficiaryInfo#isValidAllocationTotal()} with 1 beneficiary lt 100%
     */
    @Test
    public void testIsValidAllocationTotal1BeneficiaryLt100pc() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);

        BigDecimal totalAllocation = AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0.asBigDecimal();

        Assert.assertTrue("should be valid total allocation:" + totalAllocation,
                          accountBeneficiaryInfo.isValidAllocationTotal());
    }

    /**
     * Test method for
     * {@link rewards.internal.account.AccountBeneficiaryInfo#isValidAllocationTotal()} with 2 beneficiaries eq 100%
     */
    @Test
    public void testIsValidAllocationTotal2BeneficiariesEq100pc() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo beneficiaryInfo1 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo1);

        BigDecimal totalAllocation =
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0.asBigDecimal().add(
                        AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1.asBigDecimal());

        Assert.assertTrue("should be valid total allocation:" + totalAllocation,
                          accountBeneficiaryInfo.isValidAllocationTotal());
    }

    /**
     * Test method for
     * {@link rewards.internal.account.AccountBeneficiaryInfo#isValidAllocationTotal()} with 3 beneficiaries gt 100%
     */
    @Test
    public void testIsValidAllocationTotal3BeneficiariesGt100pc() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        BeneficiaryInfo beneficiaryInfo0 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
        BeneficiaryInfo beneficiaryInfo1 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
        BeneficiaryInfo beneficiaryInfo2 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo0);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo1);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo2);

        BigDecimal totalAllocation =
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0.asBigDecimal().add(
                        AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1.asBigDecimal()).add(
                        AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0.asBigDecimal());

        Assert.assertFalse("should be invalid total allocation:" + totalAllocation,
                           accountBeneficiaryInfo.isValidAllocationTotal());
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#updateAccountBeneficiaries(Account)}
     * .
     */
    @Test
    public void testValidateAllocationsLe100pc() {

        Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(account);

        accountBeneficiaryInfo.getBeneficiary(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0)
                              .setAllocationPercentage(
                                      AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1);

        BeneficiaryInfo beneficiaryInfo2 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo2);

        accountBeneficiaryInfo
                .removeBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1);

        accountBeneficiaryInfo.validateAllocations();

    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#updateAccountBeneficiaries(Account)}
     * .
     */
    @Test
    public void testValidateAllocationsGt100pc() {

        Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(account);

        BeneficiaryInfo beneficiaryInfo2 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);

        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo2);

        try {
            accountBeneficiaryInfo.validateAllocations();
            Assert.fail("Total allocations gt 100% so expecting InvalidBeneficiaryAllocationsException");
        } catch (InvalidBeneficiaryAllocationsException ex) {
            final String expectedMsgContent = "total allocations gt 100%";
            Assert.assertTrue("exception message should include diagnostic '" + expectedMsgContent + "'",
                              ex.getMessage().contains(expectedMsgContent));
        }
    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#updateAccountBeneficiaries(Account)}
     * .
     */
    @Test
    public void testUpdateAccountBeneficiaries() {

        Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(account);

        accountBeneficiaryInfo.getBeneficiary(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0)
                              .setAllocationPercentage(
                                      AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1);

        BeneficiaryInfo beneficiaryInfo2 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo2);

        accountBeneficiaryInfo
                .removeBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1);

        accountBeneficiaryInfo.updateAccountBeneficiaries(account);

        AccountTestData.verifyAccount0(account, 2);

        // Updated (but no change to savings)
        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        // Added (but zero savings)
        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

    }

    /**
     * Test method for
     * {@link AccountBeneficiaryInfo#updateAccountBeneficiaries(Account)}
     * .
     */
    @Test
    public void testUpdateAccountBeneficiariesAllocationGt100pc() {

        Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(account);

        BeneficiaryInfo beneficiaryInfo2 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);

        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo2);

        try {
            accountBeneficiaryInfo.updateAccountBeneficiaries(account);
            Assert.fail("Total allocations gt 100% so expecting InvalidBeneficiaryAllocationsException");
        } catch (InvalidBeneficiaryAllocationsException ex) {

            AccountTestData.verifyAccount0(account, 2);
            // No changes
            AccountTestData.verifyBeneficiary(account,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);
            AccountTestData.verifyBeneficiary(account,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);
            final String expectedMsgContent = "total allocations gt 100%";
            Assert.assertTrue("exception message should include diagnostic '" + expectedMsgContent + "'",
                              ex.getMessage().contains(expectedMsgContent));
        }
    }


    /**
     * Test equals for same object
     */
    @Test
    public void testEqualsForSame() {
        Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);
        Assert.assertTrue("same is equal", accountBeneficiaryInfo.equals(accountBeneficiaryInfo));
    }

    /**
     * Test equals (same values including ID)
     */
    @Test
    public void testEqualsWhenEqualIncludingId() {
        Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);
        AccountBeneficiaryInfo otherAccountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);
        Assert.assertTrue("equal values is equal", accountBeneficiaryInfo.equals(otherAccountBeneficiaryInfo));
    }

    /**
     * Test equals (same values excluding ID)
     */
    @Test
    public void testEqualsWhenEqualExcludingId() {
        Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);
        account.setEntityId(null);
        AccountBeneficiaryInfo otherAccountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);
        Assert.assertTrue("different entityId equal", accountBeneficiaryInfo.equals(otherAccountBeneficiaryInfo));
        Assert.assertTrue("different entityId equal (reversed)",
                          otherAccountBeneficiaryInfo.equals(accountBeneficiaryInfo));
    }

    /**
     * Test equals (same values including ID, no beneficiaries)
     */
    @Test
    public void testEqualsWhenEqualIncludingIdNoBenficiaries() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        AccountBeneficiaryInfo otherAccountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        Assert.assertTrue("equal values is equal", accountBeneficiaryInfo.equals(otherAccountBeneficiaryInfo));
    }

    /**
     * Test (not) equals (different name)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentName() {
        Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);
        account.setName(AccountTestData.TEST_NAME_1);
        AccountBeneficiaryInfo otherAccountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);
        Assert.assertFalse("different name should not be equal",
                           accountBeneficiaryInfo.equals(otherAccountBeneficiaryInfo));
    }

    /**
     * Test (not) equals (different number)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentNumber() {
        Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);
        account.setNumber(AccountTestData.TEST_NUMBER_1);
        AccountBeneficiaryInfo otherAccountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);
        Assert.assertFalse("different number should not be equal",
                           accountBeneficiaryInfo.equals(otherAccountBeneficiaryInfo));
    }

    /**
     * Test (not) equals (different beneficiaries, other has none)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentBeneficiariesOneNone() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        accountBeneficiaryInfo.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);
        AccountBeneficiaryInfo otherAccountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        Assert.assertFalse("different beneficiaries should not be equal",
                           accountBeneficiaryInfo.equals(otherAccountBeneficiaryInfo));
        Assert.assertFalse("different beneficiaries should not be equal (reversed)",
                           otherAccountBeneficiaryInfo.equals(accountBeneficiaryInfo));
    }

    /**
     * Test (not) equals (different beneficiaries, other has none)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentBeneficiariesOneOne() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        accountBeneficiaryInfo.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);
        AccountBeneficiaryInfo otherAccountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        otherAccountBeneficiaryInfo.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0);
        Assert.assertFalse("different beneficiaries should not be equal",
                           accountBeneficiaryInfo.equals(otherAccountBeneficiaryInfo));
    }

    /**
     * Test (not) equals (different beneficiaries, other has none)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentBeneficiariesOneTwoe() {
        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        accountBeneficiaryInfo.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);
        AccountBeneficiaryInfo otherAccountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();
        otherAccountBeneficiaryInfo.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);
        otherAccountBeneficiaryInfo.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0);
        Assert.assertFalse("different beneficiaries should not be equal",
                           accountBeneficiaryInfo.equals(otherAccountBeneficiaryInfo));
    }

}
