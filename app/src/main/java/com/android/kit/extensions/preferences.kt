package editor.video.motion.fast.slow.core.extensions

import android.content.SharedPreferences


fun SharedPreferences.incrementLong(key: String, valueIncrement: Long = 1L, start: Long = 0L) {
    val editor = edit()
    val current = getLong(key, start)
    editor.putLong(key, current + valueIncrement)
    editor.apply()
}

fun SharedPreferences.incrementInt(key: String, valueIncrement: Int = 1, start: Int = 0) {
    val editor = edit()
    val current = getInt(key, start)
    editor.putInt(key, current + valueIncrement)
    editor.apply()
}

fun SharedPreferences.boolean(key: String, def: Boolean = false) = getBoolean(key, def)

fun SharedPreferences.long(key: String, def: Long = 0) = getLong(key, def)

fun SharedPreferences.int(key: String, def: Int = 0) = getInt(key, def)