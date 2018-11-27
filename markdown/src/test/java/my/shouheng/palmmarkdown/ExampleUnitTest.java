package my.shouheng.palmmarkdown;

import org.junit.Test;

import kotlin.Triple;
import me.urakalee.ranger.extension.StringExtensionsKt;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <markdowmWebView href="http://d.android.com/tools/testing">Testing documentation</markdowmWebView>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        Triple<String, Integer, Integer> triple =
                StringExtensionsKt.selectedLine("12321", 0, 1);

    }
}