package me.urakalee.markdown.handler

import me.urakalee.markdown.MarkHandler

/**
 * @author Uraka.Lee
 */
object HeaderHandler : MarkHandler {

    override fun handleHeader(sourceMark: String): String {
        return if (sourceMark.length < 6) "$sourceMark#" else "#"
    }
}