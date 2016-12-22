package naga.toolkit.fxdata.cell.renderer;

import naga.commons.util.Strings;
import naga.toolkit.fxdata.control.HtmlText;
import naga.toolkit.fx.scene.Node;

/**
 * @author Bruno Salmon
 */
public class HtmlTextRenderer implements ValueRenderer {

    public static HtmlTextRenderer SINGLETON = new HtmlTextRenderer();

    private HtmlTextRenderer() {}

    @Override
    public Node renderCellValue(Object value) {
        return new HtmlText(Strings.toString(value));
    }
}