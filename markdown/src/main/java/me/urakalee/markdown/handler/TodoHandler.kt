package me.urakalee.markdown.handler

import me.urakalee.markdown.Mark
import me.urakalee.markdown.MarkHandler

/**
 * @author Uraka.Lee
 */
object TodoHandler : MarkHandler {

    val CHECKED = "- [X]"
    val UNCHECKED = "- [ ]"

    override fun handleTodo(inputMark: Mark, source: String, sourceMark: Mark): String {
        return handleTodo(source,
                {
                    CHECKED
                },
                {
                    UNCHECKED
                },
                {
                    super.handleTodo(inputMark, source, sourceMark)
                })
    }

    fun <T> handleTodo(source: String,
                       handleUnchecked: () -> T,
                       handleChecked: () -> T,
                       handleDefault: () -> T): T {
        return when (source.toUpperCase()) {
            UNCHECKED -> {
                handleUnchecked.invoke()
            }
            CHECKED -> {
                handleChecked.invoke()
            }
            else -> handleDefault.invoke()
        }
    }
}