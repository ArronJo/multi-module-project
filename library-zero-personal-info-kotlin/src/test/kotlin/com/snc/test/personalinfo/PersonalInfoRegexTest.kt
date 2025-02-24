package com.snc.test.personalinfo

import com.snc.zero.personalinfo.PersonalInfoRegex
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

class PersonalInfoRegexTest : BaseJUnit5Test() {

    @Test
    fun testResidentRegistrationNumber() {
        assertTrue(PersonalInfoRegex.residentRegistrationNumber.matches("900101-1234567"))
        assertTrue(PersonalInfoRegex.residentRegistrationNumber.matches("000101-4234567"))
        assertFalse(PersonalInfoRegex.residentRegistrationNumber.matches("900101-5234567"))
        assertFalse(PersonalInfoRegex.residentRegistrationNumber.matches("9001015234567"))
    }

    @Test
    fun testForeignerRegistrationNumber() {
        assertTrue(PersonalInfoRegex.foreignerRegistrationNumber.matches("900101-5234567"))
        assertTrue(PersonalInfoRegex.foreignerRegistrationNumber.matches("000101-8234567"))
        assertFalse(PersonalInfoRegex.foreignerRegistrationNumber.matches("900101-1234567"))
        assertFalse(PersonalInfoRegex.foreignerRegistrationNumber.matches("9001015234567"))
    }

    @Test
    fun testDriverLicenseNumber() {
        assertTrue(PersonalInfoRegex.driverLicenseNumber.matches("12-34-567890-12"))
        assertFalse(PersonalInfoRegex.driverLicenseNumber.matches("12-34-56789-12"))
        assertFalse(PersonalInfoRegex.driverLicenseNumber.matches("123-34-567890-12"))
    }

    @Test
    fun testPassportNumber() {
        assertTrue(PersonalInfoRegex.passportNumber.matches("M12345678"))
        assertTrue(PersonalInfoRegex.passportNumber.matches("M123Z5678"))
        assertFalse(PersonalInfoRegex.passportNumber.matches("M1234567"))
        assertFalse(PersonalInfoRegex.passportNumber.matches("12345678"))
    }

    @Test
    fun testMobilePhoneNumber() {
        assertTrue(PersonalInfoRegex.mobilePhoneNumber.matches("010-1234-5678"))
        assertTrue(PersonalInfoRegex.mobilePhoneNumber.matches("01012345678"))
        assertTrue(PersonalInfoRegex.mobilePhoneNumber.matches("016-123-4567"))
        assertFalse(PersonalInfoRegex.mobilePhoneNumber.matches("02-1234-5678"))
    }

    @Test
    fun testEmailAddress() {
        assertTrue(PersonalInfoRegex.emailAddress.matches("test@example.com"))
        assertTrue(PersonalInfoRegex.emailAddress.matches("test.name+tag@example.co.kr"))
        assertFalse(PersonalInfoRegex.emailAddress.matches("test@example"))
        assertFalse(PersonalInfoRegex.emailAddress.matches("test@.com"))
    }

    @Test
    fun testCreditCardNumber() {
        assertTrue(PersonalInfoRegex.creditCardNumber.matches("4111111111111111"))
        assertTrue(PersonalInfoRegex.creditCardNumber.matches("5500000000000004"))
        assertFalse(PersonalInfoRegex.creditCardNumber.matches("411111111111111"))
        assertFalse(PersonalInfoRegex.creditCardNumber.matches("41111111111111111"))
    }

    @Test
    fun testBankAccountNumber() {
        assertTrue(PersonalInfoRegex.bankAccountNumber.matches("11012345678"))
        assertTrue(PersonalInfoRegex.bankAccountNumber.matches("11012345678901"))
        assertFalse(PersonalInfoRegex.bankAccountNumber.matches("1101234567"))
        assertFalse(PersonalInfoRegex.bankAccountNumber.matches("1101234567890123"))
    }

    @Test
    fun testIpAddress() {
        assertTrue(PersonalInfoRegex.ipAddress.matches("192.168.0.1"))
        assertTrue(PersonalInfoRegex.ipAddress.matches("255.255.255.255"))
        assertFalse(PersonalInfoRegex.ipAddress.matches("256.0.0.1"))
        assertFalse(PersonalInfoRegex.ipAddress.matches("192.168.0"))
    }
}
