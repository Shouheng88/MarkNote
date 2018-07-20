package me.urakalee.markdown.handler

import me.urakalee.markdown.Mark
import me.urakalee.markdown.MarkHandler

/**
 * empty 在文字顶头时会出现, 需要多加一个空格
 *
 * @author Uraka.Lee
 */
object NoneHandler : MarkHandler {

    override fun handleHeader(sourceMark: String): String {
        return insertMark(Mark.H.defaultMark, sourceMark)
    }

    override fun handleList(sourceMark: String): String {
        return insertMark(Mark.LI.defaultMark, sourceMark)
    }

    private fun insertMark(inputMark: String, sourceMark: String): String {
        return if (sourceMark.isEmpty()) "$inputMark " else "$inputMark $sourceMark"
    }
}