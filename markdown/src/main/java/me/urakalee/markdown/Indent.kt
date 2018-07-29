package me.urakalee.markdown

/**
 * @author Uraka.Lee
 */
class Indent constructor(c: String?) {

    var indent: Boolean = !c.isNullOrEmpty()
        private set
    var content: String = c ?: ""
        private set
    val length: Int
        get() = content.length
    private val equivalentLength: Int
        get() {
            var l = 0
            for (c in content) {
                if (c == ' ') {
                    l += 1
                } else if (c == '\t') {
                    val rem = l.rem(TAB_SIZE)
                    l += TAB_SIZE - rem
                }

            }
            return l
        }
    var level: Int = 0

    fun indent(): Indent {
        var l = equivalentLength
        val rem = l.rem(TAB_SIZE)
        l += TAB_SIZE - rem
        content = " ".repeat(l)
        indent = true
        return this
    }

    fun dedent(): Indent {
        if (!indent) return this
        var l = equivalentLength
        val rem = l.rem(TAB_SIZE)
        l -= if (rem == 0) {
            TAB_SIZE
        } else {
            rem
        }
        content = " ".repeat(l)
        indent = content.isNotEmpty()
        return this
    }

    companion object {

        val TAB_SIZE = 4
    }
}
