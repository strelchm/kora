package ru.tinkoff.kora.kora.app.ksp.app

import ru.tinkoff.kora.common.KoraApp
import ru.tinkoff.kora.annotation.processor.common.MockLifecycle
import ru.tinkoff.kora.common.KoraSubmodule
import org.mockito.Mockito
import java.util.*

@KoraApp
interface AppWithComponentCollisionAndDirect {
    fun c1(): Class1 {
        return Class1()
    }

    fun c2(): Class1 {
        return Class1()
    }

    fun c3(): Class1 {
        return Class1()
    }

    fun class2(class1: Class1): Class2 {
        return Class2(class1)
    }

    class Class1 : MockLifecycle
    data class Class2(val class1: Class1) : MockLifecycle
}
