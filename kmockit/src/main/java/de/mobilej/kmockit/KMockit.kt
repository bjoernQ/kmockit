package de.mobilej.kmockit

import mockit.*
import mockit.integration.junit4.JMockit
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.annotation.AnnotationDescription
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
import net.bytebuddy.implementation.MethodDelegation
import java.io.File
import java.lang.reflect.Type
import java.net.URL
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.javaType


inline fun <reified T> delegate(noinline block: (invocation: Invocation) -> T): Delegate<T> {

    val target = object {
        @Suppress("unused")
        fun answer(invocation: Invocation): T {
            return block.invoke(invocation)
        }
    }

    val generic = TypeDescription.Generic.Builder
            .parameterizedType(Delegate::class.java, T::class.java).build()

    val instance = ByteBuddy()
            .subclass(generic)
            .defineMethod("answer", T::class.java, Visibility.PUBLIC)
            .withParameter(TypeDescription.ForLoadedType(Invocation::class.java))
            .intercept(MethodDelegation.to(target))
            .make()
            .load(Delegate::class.java.classLoader)
            .getLoaded()
            .newInstance()

    @Suppress("unchecked_cast")
    return instance as Delegate<T>
}

// approach found in https://medium.com/@elye.project/befriending-kotlin-and-mockito-1c2e7b0ef791
fun <T> npeAway(what: Any?): T {
    return uninitialized(what)
}

@Suppress("unchecked_cast")
private fun <T> uninitialized(what: Any?): T = what as T

interface Accessor {
    fun <T> any(clazz: Class<T>): T

    fun anyString(): String
    fun anyInt(): Int
    fun anyBoolean(): Boolean
    fun anyChar(): Char
    fun anyDouble(): Double
    fun anyFloat(): Float
    fun anyLong(): Long
    fun anyByte(): Byte
    fun anyShort(): Short

    fun <T> argThat(condition: (value: T) -> Boolean): T
}

interface VerificationAccessor : Accessor {
    fun <T> capture(capturedValues: MutableList<T>): T
}

class Stubber {
    var expect: (Accessor.() -> Unit)? = null

    inline infix fun <reified T> answers(noinline block: (invocation: Invocation) -> T) {
        object : Expectations(), Accessor {
            init {
                expect?.invoke(this)
                result = delegate(block)
            }

            override fun <T> any(clazz: Class<T>): T {
                return withInstanceOf(clazz)
            }

            override fun anyString(): String {
                return npeAway(withInstanceOf(String::class.java))
            }

            override fun anyInt(): Int {
                return withInstanceLike(0)
            }

            override fun anyBoolean(): Boolean {
                return withInstanceLike(true)
            }

            override fun anyByte(): Byte {
                return withInstanceLike(0.toByte())
            }

            override fun anyChar(): Char {
                return withInstanceLike('a')
            }

            override fun anyDouble(): Double {
                return withInstanceLike(1.0)
            }

            override fun anyFloat(): Float {
                return withInstanceLike(1.0F)
            }

            override fun anyLong(): Long {
                return withInstanceLike(0L)
            }

            override fun anyShort(): Short {
                return withInstanceLike(0.toShort())
            }

            override fun <T> argThat(condition: (value: T) -> Boolean): T {
                return with(object : Delegate<T> {
                    @Suppress("unused")
                    fun match(value: T): Boolean {
                        return condition(value)
                    }
                })
            }
        }
    }


    infix fun returns(answer: () -> Any?) {
        object : Expectations(), Accessor {
            init {
                expect?.invoke(this)
                result = (answer.invoke())
            }

            override fun <T> any(clazz: Class<T>): T {
                return withInstanceOf(clazz)
            }

            override fun anyString(): String {
                return npeAway(withInstanceOf(String::class.java))
            }

            override fun anyInt(): Int {
                return withInstanceLike(0)
            }

            override fun anyBoolean(): Boolean {
                return withInstanceLike(true)
            }

            override fun anyByte(): Byte {
                return withInstanceLike(0.toByte())
            }

            override fun anyChar(): Char {
                return withInstanceLike('a')
            }

            override fun anyDouble(): Double {
                return withInstanceLike(1.0)
            }

            override fun anyFloat(): Float {
                return withInstanceLike(1.0F)
            }

            override fun anyLong(): Long {
                return withInstanceLike(0L)
            }

            override fun anyShort(): Short {
                return withInstanceLike(0.toShort())
            }

            override fun <T> argThat(condition: (value: T) -> Boolean): T {
                return with(object : Delegate<T> {
                    @Suppress("unused")
                    fun match(value: T): Boolean {
                        return condition(value)
                    }
                })
            }
        }
    }

    infix fun returnsAll(answers: List<() -> Any?>) {
        object : Expectations(), Accessor {
            init {
                expect?.invoke(this)
                answers.forEach {
                    result = (it.invoke())
                }
            }

            override fun <T> any(clazz: Class<T>): T {
                return withInstanceOf(clazz)
            }

            override fun anyString(): String {
                return npeAway(withInstanceOf(String::class.java))
            }

            override fun anyInt(): Int {
                return withInstanceLike(0)
            }

            override fun anyBoolean(): Boolean {
                return withInstanceLike(true)
            }

            override fun anyByte(): Byte {
                return withInstanceLike(0.toByte())
            }

            override fun anyChar(): Char {
                return withInstanceLike('a')
            }

            override fun anyDouble(): Double {
                return withInstanceLike(1.0)
            }

            override fun anyFloat(): Float {
                return withInstanceLike(1.0F)
            }

            override fun anyLong(): Long {
                return withInstanceLike(0L)
            }

            override fun anyShort(): Short {
                return withInstanceLike(0.toShort())
            }

            override fun <T> argThat(condition: (value: T) -> Boolean): T {
                return with(object : Delegate<T> {
                    @Suppress("unused")
                    fun match(value: T): Boolean {
                        return condition(value)
                    }
                })
            }
        }
    }

}

fun every(block: Accessor.() -> Unit): Stubber {
    return Stubber().apply { expect = block }
}

class VerificationsBlock {
    fun times(count: Int, block: VerificationAccessor.() -> Unit) {
        object : Verifications(), VerificationAccessor {
            init {
                block.invoke(this)
                times = count
            }

            override fun <T> any(clazz: Class<T>): T {
                return withInstanceOf(clazz)
            }

            override fun anyString(): String {
                return npeAway(withInstanceOf(String::class.java))
            }

            override fun anyInt(): Int {
                return withInstanceLike(0)
            }

            override fun anyBoolean(): Boolean {
                return withInstanceLike(true)
            }

            override fun anyByte(): Byte {
                return withInstanceLike(0.toByte())
            }

            override fun anyChar(): Char {
                return withInstanceLike('a')
            }

            override fun anyDouble(): Double {
                return withInstanceLike(1.0)
            }

            override fun anyFloat(): Float {
                return withInstanceLike(1.0F)
            }

            override fun anyLong(): Long {
                return withInstanceLike(0L)
            }

            override fun anyShort(): Short {
                return withInstanceLike(0.toShort())
            }

            override fun <T> argThat(condition: (value: T) -> Boolean): T {
                return with(object : Delegate<T> {
                    @Suppress("unused")
                    fun match(value: T): Boolean {
                        return condition(value)
                    }
                })
            }

            override fun <T> capture(capturedValues: MutableList<T>): T {
                return npeAway<T>(withCapture(capturedValues))
            }

        }
    }

    fun once(block: VerificationAccessor.() -> Unit) {
        times(1, block)
    }

    fun twice(block: VerificationAccessor.() -> Unit) {
        times(2, block)
    }

    fun never(block: VerificationAccessor.() -> Unit) {
        times(0, block)
    }
}

fun verifications(block: VerificationsBlock.() -> Unit) {
    block.invoke(VerificationsBlock())
}

class MockItUp(val kClass: KClass<*>) {

    val ops = mutableListOf<Pair<KFunction<*>, (Array<Any?>) -> Any?>>()

    fun <R> on(kFunction1: KFunction<*>, function: (Array<Any?>) -> R) {
        ops.add(Pair(kFunction1, function))
    }

    fun <R> ctor(vararg args: KClass<*>, function: (Array<Any?>) -> R) {
        val wantedParams = args.map { it.createType() }
        val ctor = kClass.constructors.find {
            val ptypes = it.parameters.map { it.type }
            ptypes == wantedParams
        }
        if (ctor != null) {
            ops.add(Pair(ctor, function))
        } else {
            throw IllegalArgumentException("No such constructor")
        }
    }
}

inline fun <reified T> mockup(block: MockItUp.() -> Unit) {
    val mockItUp = MockItUp(T::class)
    block(mockItUp)

    // collect all methods to be mocked and create a JMockit MockUp dynamically
    val generic = TypeDescription.Generic.Builder
            .parameterizedType(MockUp::class.java, T::class.java).build()

    val name = "generatedkmockit.MockUp${System.nanoTime()}"
    var bb = ByteBuddy().subclass(generic, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
            .name(name)

    mockItUp.ops.forEach { pair ->

        DelegateMethod.add(name, pair.first.javaMethod ?: pair.first.javaConstructor, pair.second)

        val annotation = AnnotationDescription.Builder.ofType(Mock::class.java)
                .build()

        var retType: Type = pair.first.returnType.javaType
        var methodName = pair.first.name
        val paramTypes = pair.first.parameters.filter { !it.type.isSubtypeOf(T::class.createType()) }.map { it.type.javaType }

        if (methodName == "<init>") {
            methodName = "\$init"
            retType = java.lang.Void.TYPE
        }

        bb = bb.defineMethod(methodName, retType, Visibility.PUBLIC)
                .withParameters(paramTypes)
                .intercept(MethodDelegation.to(DelegateMethod::class.java))
                .annotateMethod(annotation)
    }


    // need to save it into the classpath - just loading doesn't work for some reason
    // this is a bit hacky
    val cl = JMockit::class.java.classLoader as URLClassLoader
    val intermediatesClasses =
            (cl.urLs.find { it.toString().contains("intermediates") })
                    ?: (cl.urLs.find { File(it.file).isDirectory })
    val classesDir = (intermediatesClasses as URL).file

    cleanUpIfFirstRun(classesDir)

    bb.make()
            .load(MockUp::class.java.classLoader, ClassLoadingStrategy.Default.WRAPPER_PERSISTENT)
            .saveIn(File(classesDir))

    val clz = Class.forName(name)
    clz.newInstance()
}

fun cleanUpIfFirstRun(directory: String) {
    if (firstRun) {
        firstRun = false

        File(directory, "generatedkmockit").deleteRecursively()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> forceFnType(fn: T) = fn as KFunction<*>

inline fun <reified T> function(name: String, vararg args: KClass<*>): KFunction<*> {
    val wantedParams = args.map { it.createType() }
    val fn = T::class.members.filter {
        if (it.name == name) {
            val ptypes = it.parameters.map { it.type }.takeLastWhile { it != T::class.createType() }
            ptypes == wantedParams
        } else {
            false
        }
    }

    if (fn.size != 1 && fn.first() is KFunction<*>) {
        throw IllegalArgumentException("No such function found")
    } else if (fn.first() !is KFunction<*>) {
        throw IllegalArgumentException("No such function found")
    }

    return fn.first() as KFunction<*>
}

private var firstRun = true