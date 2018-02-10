# KMockit

Kotlin friendly wrapper for JMockit

## About

As it turned out JMockit works fine for Kotlin on the JVM.
It works very well but the API is designed to look and feel good in Java.
This is a thin wrapper for JMockit to make it feel more natural in Kotlin.

Here is an example of how it looks like
```kotlin
        every { myDependency.doSomething(anyString()) } returns { "test" }
        every { myDependency.anotherMethod(anyInt()) } returns { 5 }

        val result = sut.myMethod("myVal")
        assertEquals("test542", result)

        verifications {
            once { myDependency.doSomething(anyString()) }
            once { myDependency.anotherMethod(10) }
            never { myDependency.unused() }
        }
```

It also offers a different way for MockUp
```kotlin
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
```

For more examples have a look at the sample app's ExampleUnitTest.kt

## Versioning

As long as it's below 1.0.0 the API isn't guaranteed to be stable and there is no strong versioning scheme applied.

Starting with 1.0.0 the API will be stable and semantic version is applied.

## Get it

For now it's available via JitPack only.