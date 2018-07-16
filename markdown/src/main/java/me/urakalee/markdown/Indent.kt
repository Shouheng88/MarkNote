package me.urakalee.markdown

/**
 * @author Uraka.Lee
 */
class Indent constructor(content: String?) {

    val indent: Boolean = !content.isNullOrEmpty()
    val content: String = content ?: ""
    var level: Int = 0
}
