package my.shouheng.palmmarkdown.tools;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.util.TextCollectingVisitor;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.html.Escaping;
import com.vladsch.flexmark.util.options.DataHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by shouh on 2018/3/25.*/
public class NodeRendererFactoryImpl implements NodeRendererFactory {

    @Override
    public NodeRenderer create(DataHolder options) {
        return new NodeRenderer() {
            @Override
            public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
                HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
                set.add(new NodeRenderingHandler<>(Image.class, new CustomNodeRenderer<Image>() {
                    @Override
                    public void render(Image node, NodeRendererContext context, HtmlWriter html) {
                        if (!context.isDoNotRenderLinks()) {
                            String altText = new TextCollectingVisitor().collectAndGetText(node);

                            ResolvedLink resolvedLink = context.resolveLink(LinkType.IMAGE, node.getUrl().unescape(), null);
                            String url = resolvedLink.getUrl();

                            if (!node.getUrlContent().isEmpty()) {
                                // reverse URL encoding of =, &
                                String content = Escaping.percentEncodeUrl(node.getUrlContent()).replace("+", "%2B").replace("%3D", "=").replace("%26", "&amp;");
                                url += content;
                            }

                            final int index = url.indexOf('@');

                            if (index >= 0) {
                                String[] dimensions = url.substring(index + 1, url.length()).split("\\|");
                                url = url.substring(0, index);

                                if (dimensions.length == 2) {
                                    String width = dimensions[0] == null || dimensions[0].equals("") ? "auto" : dimensions[0];
                                    String height = dimensions[1] == null || dimensions[1].equals("") ? "auto" : dimensions[1];
                                    html.attr("style", "width: " + width + "; height: " + height);
                                }
                            }

                            html.attr("src", url);
                            html.attr("alt", altText);

                            if (node.getTitle().isNotNull()) {
                                html.attr("title", node.getTitle().unescape());
                            }

                            html.srcPos(node.getChars()).withAttr(resolvedLink).tagVoid("img");
                        }
                    }
                }));
                return set;
            }
        };
    }
}
