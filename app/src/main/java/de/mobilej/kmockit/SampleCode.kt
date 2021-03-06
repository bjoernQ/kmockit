package de.mobilej.kmockit

class Evil {

    companion object {
        fun companion(): String {
            return "companion"
        }
    }

    fun theAnswer(value: String): Int {
        return 42
    }

    fun another(value: Int, value2: Int): String {
        return "$value + $value2 = ${value + value2}"
    }

    fun ambiguous(value: Int): String {
        return value.toString()
    }

    fun ambiguous(value: String): String {
        return value
    }
}

class AnotherClass {

    var value: String

    constructor(v: String) {
        value = v
    }

    fun myMethod(): String {
        return "Value is $value"
    }
}

fun AnotherClass.extension(i: Int): String {
    return "$i ${this.myMethod()}"
}

fun topLevel(v: String): String {
    return ">>$v<<"
}

class UnderTest(val dependency: MyDependency) {

    fun myMethod(value: String): String {
        return dependency.doSomething(value) + dependency.anotherMethod(10) + Evil().theAnswer("?")
    }

    fun myMethod2(value: String): String {
        return dependency.doSomething(value) + dependency.doSomething(value)
    }

    fun myMethod3(value: String): String {
        return dependency.doSomethingAgain(5, value) + dependency.doSomethingAgain(5, value)
    }

    fun myMethod4(block: () -> String): String {
        return dependency.higherOrderFunction(block)
    }

    fun myMethod5(block: (Int, String) -> Long): String {
        return dependency.higherOrderFunction2(block)
    }

    fun myMethod6(block: (Int, String) -> Long): String {
        return dependency.higherOrderFunction3(21, 42, block)
    }

    fun myMethodInt(i: Int): Int {
        return dependency.doInt(i);
    }

    fun myMethodBoolean(v: Boolean): Boolean {
        return dependency.doBoolean(v);
    }

    fun myMethodLong(v: Long): Long {
        return dependency.doLong(v);
    }

    fun myMethodChar(v: Char): Char {
        return dependency.doChar(v);
    }

    fun myMethodFloat(v: Float): Float {
        return dependency.doFloat(v);
    }

    fun myMethodDouble(v: Double): Double {
        return dependency.doDouble(v);
    }

    fun myMethodShort(v: Short): Short {
        return dependency.doShort(v);
    }

    fun myMethodByte(v: Byte): Byte {
        return dependency.doByte(v);
    }
}

class MyDependency {
    fun doSomething(value: String): String {
        return value.reversed()
    }

    fun anotherMethod(value: Int): String {
        return value.toString()
    }

    fun doSomethingAgain(i: Int, v: String): String {
        return v
    }

    fun higherOrderFunction(block: () -> String): String {
        return block()
    }

    fun higherOrderFunction2(block: (Int, String) -> Long): String {
        return block(5, "Hello").toString()
    }

    fun higherOrderFunction3(p1: Int, p2: Int, block: (Int, String) -> Long): String {
        return block(5, "Hello").toString()
    }

    fun unused() {
        // nothing
    }

    fun doInt(i: Int): Int {
        return i;
    }

    fun doBoolean(b: Boolean): Boolean {
        return b;
    }

    fun doLong(v: Long): Long {
        return v;
    }

    fun doFloat(v: Float): Float {
        return v;
    }

    fun doDouble(v: Double): Double {
        return v;
    }

    fun doByte(v: Byte): Byte {
        return v;
    }

    fun doShort(v: Short): Short {
        return v;
    }

    fun doChar(v: Char): Char {
        return v;
    }

}