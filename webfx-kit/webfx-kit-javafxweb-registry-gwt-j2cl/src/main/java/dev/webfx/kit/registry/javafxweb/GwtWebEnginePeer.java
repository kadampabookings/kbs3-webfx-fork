package dev.webfx.kit.registry.javafxweb;

import dev.webfx.kit.mapper.peers.javafxweb.engine.WebEnginePeerBase;
import dev.webfx.kit.mapper.peers.javafxweb.spi.gwt.HtmlWebViewPeer;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.scheduler.Scheduler;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLIFrameElement;
import elemental2.dom.Window;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author Bruno Salmon
 */
final class GwtWebEnginePeer extends WebEnginePeerBase {

    private final WebEngine webEngine;

    public GwtWebEnginePeer(WebEngine webEngine) {
        this.webEngine = webEngine;
        WebView webView = webEngine.getWebView();
        FXProperties.runNowAndOnPropertiesChange(e -> updateState(), webView == null ? null : webView.sceneProperty());
    }

    private Window getScriptWindow() {
        Window iFrameWindow = null;
        WebView webView = webEngine.getWebView();
        HtmlWebViewPeer peer = webView == null ? null : (HtmlWebViewPeer) webView.getNodePeer();
        if (peer != null) {
            HTMLIFrameElement iFrame = peer.getIFrame();
            if (iFrame != null)
                iFrameWindow = iFrame.contentWindow;
        }
        return iFrameWindow != null ? iFrameWindow : DomGlobal.window;
    }

    private void updateState() {
        Window scriptWindow = getScriptWindow();
        if (scriptWindow != null)
            worker.setState(Worker.State.READY);
        else {
            worker.setState(Worker.State.SCHEDULED);
            if (webEngine.getWebView().getScene() != null)
                Scheduler.scheduleDelay(100, this::updateState);
        }
    }

    @Override
    public Object executeScript(String script) {
        return GwtJSObject.eval(getScriptWindow(), script);
    }

}
