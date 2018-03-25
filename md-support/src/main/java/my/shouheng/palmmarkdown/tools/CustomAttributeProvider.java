package my.shouheng.palmmarkdown.tools;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.util.html.Attributes;

/**
 * Created by shouh on 2018/3/25. */
public class CustomAttributeProvider implements AttributeProvider {

    @Override
    public void setAttributes(final Node node, final AttributablePart part, final Attributes attributes) {}
}
