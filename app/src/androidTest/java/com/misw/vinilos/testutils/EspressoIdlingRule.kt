package com.misw.vinilos.testutils

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Registra/desregistra el CountingIdlingResource global.
 *
 * Al estar enganchado vía OkHttp interceptor, los tests no necesitan Thread.sleep().
 */
class EspressoIdlingRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                EspressoIdlingResource.resetForTests()

                val idlingResource = object : IdlingResource {
                    @Volatile
                    private var callback: IdlingResource.ResourceCallback? = null

                    override fun getName(): String = "VinilosNetwork"

                    override fun isIdleNow(): Boolean {
                        val idle = EspressoIdlingResource.isIdleNow()
                        if (idle) {
                            callback?.onTransitionToIdle()
                        }
                        return idle
                    }

                    override fun registerIdleTransitionCallback(cb: IdlingResource.ResourceCallback) {
                        callback = cb
                    }
                }

                IdlingRegistry.getInstance().register(idlingResource)
                try {
                    base.evaluate()
                } finally {
                    IdlingRegistry.getInstance().unregister(idlingResource)
                }
            }
        }
    }
}


