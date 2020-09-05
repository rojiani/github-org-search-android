package com.nrojiani.githuborgsearch.extensions

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.*

internal class IntExtensionsTest {

    @Test
    fun formatted() {
        val n = 123456789

        Locale.setDefault(DEFAULT_LOCALE)
        n.formatted().shouldBe("123,456,789")
        n.formatted(DEFAULT_LOCALE).shouldBe("123,456,789")

        Locale.setDefault(Locale.GERMANY)
        n.formatted().shouldBe("123.456.789")
        n.formatted(Locale.GERMANY).shouldBe("123.456.789")

        Locale.setDefault(Locale.FRANCE)
        n.formatted().shouldBe("123 456 789")
        n.formatted(Locale.FRANCE).shouldBe("123 456 789")
    }

    companion object {
        private val DEFAULT_LOCALE = Locale.US
    }
}
