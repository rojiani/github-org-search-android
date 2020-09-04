package com.nrojiani.githuborgsearch.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nrojiani.githuborgsearch.data.repository.ReposRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class OrgDetailsViewModelTest {

    private lateinit var mockReposRepository: ReposRepository
    private lateinit var orgDetailsViewModel: OrgDetailsViewModel
    /* InstantTaskExecutorRule is a JUnit Test Rule that swaps the background executor used by
       the Architecture Components with a different one which executes each task synchronously. */
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockReposRepository = Mockito.mock(ReposRepository::class.java)
        orgDetailsViewModel = OrgDetailsViewModel(mockReposRepository)
    }

    @Test
    fun todo() {
        // org.junit.runners.model.InvalidTestClassError if no tests
    }
}
