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