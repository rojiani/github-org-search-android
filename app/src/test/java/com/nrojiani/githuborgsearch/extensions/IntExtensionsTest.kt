package com.nrojiani.githuborgsearch.extensions

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.*

class IntExtensionsTest {

    companion object {
        private val DEFAULT_LOCALE = Locale.US
    }

    @Test
    fun formatted() {
        val n = 123456789

        Locale.setDefault(DEFAULT_LOCALE)
        assertThat(n.formatted(), `is`("123,456,789"))
        assertThat(n.formatted(DEFAULT_LOCALE), `is`("123,456,789"))

        Locale.setDefault(Locale.GERMANY)
        assertThat(n.formatted(), `is`("123.456.789"))
        assertThat(n.formatted(Locale.GERMANY), `is`("123.456.789"))

        Locale.setDefault(Locale.FRANCE)
        assertThat(n.formatted(), `is`("123 456 789"))
        assertThat(n.formatted(Locale.FRANCE), `is`("123 456 789"))
    }
}
