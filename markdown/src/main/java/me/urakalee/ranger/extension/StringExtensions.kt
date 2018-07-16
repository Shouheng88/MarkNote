package me.urakalee.ranger.extension

/**
 * @author Uraka.Lee
 */
fun String.selectedLine(selectionStart: Int, selectionEnd: Int): Triple<String, Int, Int> {
    var lineStart = selectionStart
    var lineEnd = selectionStart
    while (lineStart > 0) {
        val prevChar = this[lineStart - 1]
        if (!prevChar.isLineBreak()) {
            lineStart -= 1
        } else {
            break
        }
    }
    if (lineStart < 0) {
        lineStart = 0
    }
    while (lineEnd < this.length) {
        val char = this[lineEnd]
        if (!char.isLineBreak() || lineEnd == this.length - 1) {
            lineEnd += 1
        } else {
            break
        }
    }
    if (lineEnd > this.length) {
        lineEnd = this.length
    }
    return Triple(this.substring(lineStart, lineEnd), lineStart, lineEnd)
}