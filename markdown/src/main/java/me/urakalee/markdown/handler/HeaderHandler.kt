package me.urakalee.markdown.handler

import me.urakalee.markdown.Mark
import me.urakalee.markdown.MarkHandler

/**
 * @author Uraka.Lee
 */
object HeaderHandler : MarkHandler {

    override fun handleHeader(source: String, sourceMark: Mark): String {
        return if (source.length < 6) "$sourceMark${Mark.H.defaultMark}" else Mark.H.defaultMark
    }
}