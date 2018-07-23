package me.urakalee.ranger.extension

/**
 * @author Uraka.Lee
 */
fun Char.isLineBreak(): Boolean {
    return this == '\r' || this == '\n'
}

fun Char.isIndent(): Boolean {
    return this == ' ' || this == '\t'
}