package me.urakalee.markdown.handler

import me.urakalee.markdown.Mark
import me.urakalee.markdown.MarkHandler

/**
 * @author Uraka.Lee
 */
object ListHandler : MarkHandler {

    override fun handleHeader(sourceMark: String): String {
        return Mark.H.defaultMark
    }

    override fun handleList(sourceMark: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}