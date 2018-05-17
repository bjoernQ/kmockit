package de.mobilej.kmockit

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import mockit.Injectable
import mockit.Tested
import mockit.integration.junit4.JMockit
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JMockit::class)
class ExampleUnitTest {

    @Injectable
    private lateinit var myDependency: MyDependency

    @Tested
    private lateinit var sut: UnderTest

    @Test
    fun simpleTest() {
        every { myDependency.doSomething(anyString()) } returns { "test" }
        every { myDependency.anotherMethod(anyInt()) } returns { 5 }

        val result = sut.myMethod("myVal")
        assertEquals("test542", result)

        verifications {
            once { myDependency.doSomething(anyString()) }
            once { myDependency.anotherMethod(10) }
            never { myDependency.unused() }
        }
    }

    @Test
    fun simpleTest2() {
        every { myDependency.anotherMethod(anyInt()) } returns { 5 }
        every { myDependency.doSomething(anyString()) } answers { "test" }

        val result = sut.myMethod("myVal")
        assertEquals("test542", result)

        val holder = mutableListOf<String>()
        verifications {
            once { myDependency.doSomething(capture(holder)) }
            once { myDependency.anotherMethod(10) }
            never { myDependency.unused() }
        }

        assertEquals("myVal", holder[0])
    }

    @Test
    fun simpleTest3() {

        mockup<Evil.Companion> {
            on(Evil.Companion::companion) { "mockup" }
        }

        mockup<Evil> {
            on(Evil::theAnswer) {
                99
            }
            on(Evil::another) {
                "NOTHING" + it[0]
            }
        }

        assertEquals("mockup", Evil.Companion.companion())
        assertEquals(99, Evil().theAnswer("?"))
        assertEquals("NOTHING1", Evil().another(1, 2))
    }

    @Test
    fun simpleTest4() {
        every { myDependency.doSomething(anyString()) } returnsAll listOf(
                { "test" },
                { "TEST" }
        )

        val result = sut.myMethod2("myVal")
        assertEquals("testTEST", result)

        verifications {
            twice { myDependency.doSomething(anyString()) }
            never { myDependency.unused() }
        }
    }

    @Test(expected = RuntimeException::class)
    fun simpleTest5() {
        every { myDependency.doSomething(anyString()) } answers {
            throw RuntimeException()
            @Suppress("unreachable_code")
            ""
        }

        sut.myMethod2("myVal")
    }

    @Test
    fun simpleTest6() {
        every { myDependency.doSomething(argThat { it == "myVal" }) } returns { "test" }

        val result = sut.myMethod2("myVal")
        assertEquals("testtest", result)

        verifications {
            twice { myDependency.doSomething(anyString()) }
        }
    }

    @Test
    fun simpleTest7() {
        every { myDependency.doInt(anyInt()) } returns { 22 }

        val result = sut.myMethodInt(55)
        assertEquals(22, result)

        verifications {
            once { myDependency.doInt(anyInt()) }
        }
    }

    @Test
    fun simpleTest8() {
        every { myDependency.doLong(anyLong()) } returns { 22L }

        val result = sut.myMethodLong(55L)
        assertEquals(22L, result)

        verifications {
            once { myDependency.doLong(anyLong()) }
        }
    }

    @Test
    fun simpleTest9() {
        every { myDependency.doBoolean(anyBoolean()) } returns { false }

        val result = sut.myMethodBoolean(true)
        assertEquals(false, result)

        verifications {
            once { myDependency.doBoolean(anyBoolean()) }
        }
    }

    @Test
    fun simpleTest10() {
        every { myDependency.doChar(anyChar()) } returns { 'x' }

        val result = sut.myMethodChar('a')
        assertEquals('x', result)

        verifications {
            once { myDependency.doChar(anyChar()) }
        }
    }

    @Test
    fun simpleTest11() {
        every { myDependency.doFloat(anyFloat()) } returns { 22.0F }

        val result = sut.myMethodFloat(15F)
        assertEquals(22.0F, result, 1F)

        verifications {
            once { myDependency.doFloat(anyFloat()) }
        }
    }

    @Test
    fun simpleTest12() {
        every { myDependency.doDouble(anyDouble()) } returns { 22.0 }

        val result = sut.myMethodDouble(15.0)
        assertEquals(22.0, result, 1.0)

        verifications {
            once { myDependency.doDouble(anyDouble()) }
        }
    }

    @Test
    fun simpleTest13() {
        every { myDependency.doShort(anyShort()) } returns { 22.toShort() }

        val result = sut.myMethodShort(15.toShort())
        assertEquals(22.toShort(), result)

        verifications {
            once { myDependency.doShort(anyShort()) }
        }
    }

    @Test
    fun simpleTest14() {
        every { myDependency.doByte(anyByte()) } returns { 22.toByte() }

        val result = sut.myMethodByte(15.toByte())
        assertEquals(22.toByte(), result)

        verifications {
            once { myDependency.doByte(anyByte()) }
        }
    }

    @Test
    fun simpleTest15() {

        mockup<Evil> {
            on(forceFnType<(Evil.(Int) -> String)>(Evil::ambiguous)) {
                "1"
            }
            on(forceFnType<(Evil.(String) -> String)>(Evil::ambiguous)) {
                "2"
            }
        }

        assertEquals("1", Evil().ambiguous(5))
        assertEquals("2", Evil().ambiguous("X"))
    }

    @Test
    fun simpleTest16() {

        mockup<Evil> {
            on(function<Evil>("ambiguous", Int::class)) {
                "1"
            }

            on(function<Evil>("ambiguous", String::class)) {
                "2"
            }
        }

        assertEquals("1", Evil().ambiguous(5))
        assertEquals("2", Evil().ambiguous("X"))
    }

    @Test
    fun simpleTest17() {

        var ctorCalled = false

        mockup<AnotherClass> {
            ctor(String::class) {
                ctorCalled = true
            }
        }

        assertEquals(null, AnotherClass("2").value)
        assertTrue(ctorCalled)
    }

    @Test
    fun simpleTest18() {

        mockupSpecial("de.mobilej.kmockit.SampleCode") {
            on(AnotherClass::extension) {
                "mocked"
            }
        }

        assertEquals("mocked", AnotherClass("2").extension(5))
    }

    @Test
    fun simpleTest19() {

        mockupSpecial("de.mobilej.kmockit.SampleCode") {
            on(::topLevel) {
                "mocked"
            }
        }

        assertEquals("mocked", topLevel("Hello"))
    }

    @Test
    fun simpleTest20() {
        every {
            myDependency.doSomethingAgain(argThat { it == 5 }, argThat { it == "myVal" })
        } returns { "test" }

        val result = sut.myMethod3("myVal")
        assertEquals("testtest", result)

        val intHolder = mutableListOf<Int>()
        val stringHolder = mutableListOf<String>()
        verifications {
            twice { myDependency.doSomethingAgain(capture(intHolder), capture(stringHolder)) }
        }

        assertEquals(2, intHolder.size)
        assertEquals(2, stringHolder.size)
    }


    @Test
    fun simpleTest21() {
        val lambdaHolder = mutableListOf<() -> String>()
        sut.myMethod4 { "Hello" }
        verifications {
            once { myDependency.higherOrderFunction(capture(lambdaHolder)) }
        }

        assertEquals("Hello", lambdaHolder[0]())
    }

    @Test
    fun simpleTest22() {
        val lambdaHolder = mutableListOf<(Int, String) -> Long>()
        sut.myMethod5 { _, _ -> 21L }
        verifications {
            once { myDependency.higherOrderFunction2(capture(lambdaHolder)) }
        }

        assertEquals(21L, lambdaHolder[0](1, "A"))
    }

    @Test
    fun simpleTest23() {
        val lambdaHolder = mutableListOf<(Int, String) -> Long>()
        sut.myMethod6 { _, _ -> 21L }
        verifications {
            once { myDependency.higherOrderFunction3(argThat { it == 21 }, argThat { it == 42 }, capture(lambdaHolder)) }
        }

        assertEquals(21L, lambdaHolder[0](1, "A"))
    }
}