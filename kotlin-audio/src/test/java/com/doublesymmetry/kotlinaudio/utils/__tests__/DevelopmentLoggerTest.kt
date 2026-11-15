package com.doublesymmetry.kotlinaudio.utils

import com.doublesymmetry.kotlinaudio.BuildConfig
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class DevelopmentLoggerTest {
    private val tree = RecordingTree()

    @Before
    fun plantRecordingTree() {
        Timber.plant(tree)
    }

    @After
    fun removeRecordingTree() {
        Timber.uproot(tree)
    }

    @Test
    fun logsFormattedDebugMessageOnlyInDebugBuilds() {
        DevelopmentLogger.d("Playing %s", "episode")

        assertEquals(
            if (BuildConfig.DEBUG) listOf(Entry(3, null, "Playing episode", null)) else emptyList<Entry>(),
            tree.entries,
        )
    }

    @Test
    fun preservesTagOnFormattedDebugMessageOnlyInDebugBuilds() {
        DevelopmentLogger.tag("player").d("Playing %s", "episode")

        assertEquals(
            if (BuildConfig.DEBUG) listOf(Entry(3, "player", "Playing episode", null)) else emptyList<Entry>(),
            tree.entries,
        )
    }

    @Test
    fun logsThrowableWithFormattedWarningOnlyInDebugBuilds() {
        val failure = IllegalStateException("decoder failure")

        DevelopmentLogger.w(failure, "Playback %s", "failed")

        expectWarning(throwable = failure, tag = null, message = "Playback failed")
    }

    @Test
    fun preservesTagOnFormattedWarningOnlyInDebugBuilds() {
        DevelopmentLogger.tag("artwork").w("Artwork %s", "missing")

        assertEquals(
            if (BuildConfig.DEBUG) listOf(Entry(5, "artwork", "Artwork missing", null)) else emptyList<Entry>(),
            tree.entries,
        )
    }

    @Test
    fun preservesTagAndThrowableOnWarningOnlyInDebugBuilds() {
        val failure = IllegalStateException("decoder failure")

        DevelopmentLogger.tag("player").w(failure, "Playback %s", "failed")

        expectWarning(throwable = failure, tag = "player", message = "Playback failed")
    }

    private fun expectWarning(throwable: Throwable, tag: String?, message: String) {
        if (!BuildConfig.DEBUG) {
            assertTrue(tree.entries.isEmpty())
            return
        }

        assertEquals(1, tree.entries.size)
        val entry = tree.entries.single()
        assertEquals(5, entry.priority)
        assertEquals(tag, entry.tag)
        assertTrue(entry.message.startsWith("$message\njava.lang.IllegalStateException: decoder failure"))
        assertSame(throwable, entry.throwable)
    }

    private data class Entry(
        val priority: Int,
        val tag: String?,
        val message: String,
        val throwable: Throwable?,
    )

    private class RecordingTree : Timber.Tree() {
        val entries = mutableListOf<Entry>()

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            entries.add(Entry(priority, tag, message, t))
        }
    }
}
