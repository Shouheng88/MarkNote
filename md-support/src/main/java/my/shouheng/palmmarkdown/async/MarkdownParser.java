package my.shouheng.palmmarkdown.async;

import android.os.AsyncTask;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.superscript.SuperscriptExtension;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.Arrays;
import java.util.List;

import my.shouheng.palmmarkdown.ext.mark.MarkExtension;
import my.shouheng.palmmarkdown.ext.mathjax.MathJaxExtension;
import my.shouheng.palmmarkdown.tools.CustomAttributeProvider;
import my.shouheng.palmmarkdown.tools.NodeRendererFactoryImpl;

/**
 * Created by shouh on 2018/3/25.*/
public class MarkdownParser extends AsyncTask<String, String, String> {

    private final List<Extension> EXTENSIONS = Arrays.asList(
            TablesExtension.create(),
            TaskListExtension.create(),
            AbbreviationExtension.create(),
            AutolinkExtension.create(),
            MarkExtension.create(),
            StrikethroughSubscriptExtension.create(),
            SuperscriptExtension.create(),
            MathJaxExtension.create(),
            FootnoteExtension.create(),
            AttributesExtension.create());

    private final DataHolder OPTIONS = new MutableDataSet()
            .set(FootnoteExtension.FOOTNOTE_REF_PREFIX, "[")
            .set(FootnoteExtension.FOOTNOTE_REF_SUFFIX, "]")
            .set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "")
            .set(HtmlRenderer.FENCED_CODE_NO_LANGUAGE_CLASS, "nohighlight");

    private OnGetResultListener onGetResultListener;

    public MarkdownParser(OnGetResultListener onGetResultListener) {
        this.onGetResultListener = onGetResultListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        Parser parser = Parser.builder(OPTIONS)
                .extensions(EXTENSIONS)
                .build();

        HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS)
                .escapeHtml(true)
                .attributeProviderFactory(new IndependentAttributeProviderFactory() {
                    @Override
                    public AttributeProvider create(NodeRendererContext context) {
                        return new CustomAttributeProvider();
                    }
                })
                .nodeRendererFactory(new NodeRendererFactoryImpl())
                .extensions(EXTENSIONS)
                .build();

        return renderer.render(parser.parse(strings[0]));
    }

    @Override
    protected void onPostExecute(String html) {
        if (onGetResultListener != null) {
            onGetResultListener.onGetResult(html);
        }
    }

    public interface OnGetResultListener {
        void onGetResult(String html);
    }
}
