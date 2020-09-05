package com.nrojiani.githuborgsearch.network.responsehandler

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HttpStatusTest {

    @Test
    fun whenHttpStatusCreated_categoryIsSet() {
        val ok = HttpStatus(200, "OK")
        assertEquals(HttpStatus.Series.SUCCESSFUL, ok.category)
        assertEquals("2xx", ok.category.codeRange)

        val notFound = HttpStatus(404, "NOT_FOUND")
        assertEquals(HttpStatus.Series.CLIENT_ERROR, notFound.category)
        assertEquals("4xx", notFound.category.codeRange)
    }
}
