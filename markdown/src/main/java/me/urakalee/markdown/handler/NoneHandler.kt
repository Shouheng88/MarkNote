package me.urakalee.markdown.handler

import me.urakalee.markdown.Mark
import me.urakalee.markdown.MarkHandler

/**
 * empty 在文字顶头时会出现, 需要多加一个空格
 *
 * @author Uraka.Lee
 */
object NoneHandler : MarkHandler {

    override fun handleHeader(source: String, sourceMark: Mark): String {
        return insertMark(Mark.H.defaultMark, source)
    }

    override fun handleList(source: String, sourceMark: Mark): String {
        return insertMark(Mark.LI.defaultMark, source)
    }

    private fun insertMark(inputMark: String, sourceMark: String): String {
        return if (sourceMark.isEmpty()) "$inputMark " else "$inputMark $sourceMark"
    }
}