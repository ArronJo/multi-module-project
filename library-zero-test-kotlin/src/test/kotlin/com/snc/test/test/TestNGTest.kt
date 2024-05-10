package com.snc.test.test

import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

@Test
class TestNGTest {

    @BeforeClass
    fun setUp() {
        println("setUp")
    }

    @Test
    fun testMethod() {
        println("testMethod")
    }

    @Test(groups = ["functest", "checkintest"])
    fun testMethod1() {
        println("testMethod1")
    }

    @Test(groups = ["functest", "checkintest"])
    fun testMethod2() {
        println("testMethod2")
    }

    @Test(groups = ["functest"])
    fun testMethod3() {
        println("testMethod3")
    }

    @AfterClass
    fun tearDown() {
        println("tearDown")
    }
}