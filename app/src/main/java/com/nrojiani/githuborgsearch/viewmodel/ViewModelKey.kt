package com.nrojiani.githuborgsearch.viewmodel

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Annotation used to associate ViewModel classes to `Provider<ViewModel>`
 * as entries in a map. This map can be injected into a ViewModel Factory.
 *
 * See [Kotlin Academy: Understanding Dagger 2 Multibindings ViewModel](https://blog.kotlin-academy.com/understanding-dagger-2-multibindings-viewmodel-8418eb372848)
 * for detailed explanation.
 *
 * @see [ViewModelFactory], [ViewModelModule]
 */
@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)
