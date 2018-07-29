package me.urakalee.markdown.handler

import me.urakalee.markdown.Mark
import me.urakalee.markdown.MarkHandler

/**
 * @author Uraka.Lee
 */
object ListHandler : MarkHandler {

    override fun handleList(inputMark: Mark, source: String, sourceMark: Mark): String {
        return when (sourceMark) {
            Mark.LI -> {
                Mark.LO.defaultMark
            }
            Mark.LO -> {
                Mark.LA.defaultMark
            }
            Mark.LA -> {
                Mark.LI.defaultMark
            }
            else -> {
                super.handleList(inputMark, source, sourceMark)
            }
        }
    }
}