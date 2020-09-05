package com.nrojiani.githuborgsearch.network.responsehandler

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class HttpStatusTest {

    @Test
    fun whenHttpStatusCreated_categoryIsSet() {
        val ok = HttpStatus(200, "OK")
        ok.category.shouldBe(HttpStatus.Series.SUCCESSFUL)
        ok.category.codeRange.shouldBe("2xx")

        val notFound = HttpStatus(404, "NOT_FOUND")
        notFound.category.shouldBe(HttpStatus.Series.CLIENT_ERROR)
        notFound.category.codeRange.shouldBe("4xx")
    }
}
