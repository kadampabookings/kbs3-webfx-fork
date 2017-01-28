package naga.fx.spi.javafx.peer;

import javafx.scene.web.WebView;
import naga.commons.util.Strings;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import naga.fxdata.control.HtmlText;
import naga.fxdata.spi.peer.base.HtmlTextPeerBase;
import naga.fxdata.spi.peer.base.HtmlTextPeerMixin;

/**
 * @author Bruno Salmon
 */
public class FxHtmlTextPeer
        <FxN extends WebView, N extends HtmlText, NB extends HtmlTextPeerBase<N, NB, NM>, NM extends HtmlTextPeerMixin<N, NB, NM>>
        extends FxNodePeer<FxN, N, NB, NM>
        implements HtmlTextPeerMixin<N, NB, NM>, FxLayoutMeasurable {

    private final WebView webView = new WebView();

    public FxHtmlTextPeer() {
        this((NB) new HtmlTextPeerBase());
    }

    FxHtmlTextPeer(NB base) {
        super(base);
        updateText(null);
    }

    @Override
    protected FxN createFxNode() {
        return (FxN) webView;
    }

    private Object executeScript(String script) {
        try {
            return webView.getEngine().executeScript(script);
        } catch (Exception e) { // probably the jsFunctions were not injected
            webView.getEngine().executeScript(jsFunctions);
            return webView.getEngine().executeScript(script);
        }
    }

    @Override
    public void updateText(String text) {
        webView.getEngine().loadContent("<div style='margin: 0; padding: 0;'>" + Strings.toSafeString(text) + "</div>");
    }

    @Override
    public void updateWidth(Double width) {
        executeScript("setDocumentWidth(" + documentWidth(width) + ");");
        updateResize();
    }

    @Override
    public void updateHeight(Double height) {
        updateResize();
    }

    private void updateResize() {
        N node = getNode();
        webView.resize(node.getWidth(), node.getHeight());
    }

    @Override
    public void updateBackground(Background background) {
    }

    @Override
    public void updateBorder(Border border) {
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double minHeight(double width) {
        return 0;
    }

    @Override
    public double prefHeight(double width) {
        String heightText = executeScript("documentPrefHeight(" + documentWidth(width) + ")").toString();
        return webViewHeight(Double.valueOf(heightText));
    }

    private double documentWidth(double webViewWidth) {
        return webViewWidth - 12;
    }

    private double webViewHeight(double documentHeight) {
        return documentHeight + 25;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    private final static String jsFunctions = "" +
            "function documentPrefHeight(width) {" +
            "var e = document.body.firstChild;" +
            "if (!e) return 0;" +
            "var s = e.style;" +
            "var w = s.width;" +
            "s.width = width > 0 ? width + 'px' : w;" +
            "var h = e.offsetHeight;" +
            "s.width = w;" +
            "return h;" +
            "};" +
            "function setDocumentWidth(width) {" +
            "var e = document.body.firstChild;" +
            "if (!e) return;" +
            "var s = e.style;" +
            "var w = s.width;" +
            "s.width = width > 0 ? width + 'px' : w;" +
            "};" +
            "document.body.style.overflow = 'hidden';";
}