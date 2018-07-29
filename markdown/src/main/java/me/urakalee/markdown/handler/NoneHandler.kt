package me.urakalee.markdown.handler

import me.urakalee.markdown.Mark
import me.urakalee.markdown.MarkHandler

/**
 * empty 在文字顶头时会出现, 需要多加一个空格
 *
 * @author Uraka.Lee
 */
object NoneHandler : MarkHandler {

    override fun handleMark(inputMark: Mark, source: String, sourceMark: Mark): String {
        return when (inputMark) {
            Mark.H,
            Mark.LI,
            Mark.TD,
            Mark.QT -> {
                addPrecedingMark(inputMark.defaultMark, source)
            }
            else -> {
                super.handleMark(inputMark, source, sourceMark)
            }
        }
    }

    private fun addPrecedingMark(inputMark: String, source: String): String {
        return if (source.isEmpty()) "$inputMark " else "$inputMark $source"
    }
}