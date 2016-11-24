package naga.providers.toolkit.swing.drawing.view;

import naga.commons.util.Numbers;
import naga.commons.util.Objects;
import naga.providers.toolkit.swing.util.SwingFonts;
import naga.toolkit.drawing.text.Font;
import naga.toolkit.drawing.text.TextAlignment;
import naga.toolkit.drawing.text.TextShape;
import naga.toolkit.drawing.geometry.VPos;
import naga.toolkit.drawing.spi.view.base.TextShapeViewBase;
import naga.toolkit.drawing.spi.view.base.TextShapeViewMixin;
import naga.toolkit.drawing.spi.view.base.TextShapeViewMixin2;

import java.awt.*;

/**
 * @author Bruno Salmon
 */
public class SwingTextShapeView
        extends SwingShapeView<TextShape, TextShapeViewBase, TextShapeViewMixin>
        implements TextShapeViewMixin2 {

    private java.awt.Font swingFont;

    public SwingTextShapeView() {
        super(new TextShapeViewBase());
    }

    @Override
    public void updateText(String text) {
        updateSwingShape();
    }

    @Override
    public void updateFont(Font font) {
        swingFont = null;
        updateSwingShape();
    }

    private java.awt.Font getShapeSwingFont() {
        if (swingFont == null)
            swingFont = SwingFonts.toSwingFont(getNode().getFont());
        return swingFont;
    }

    @Override
    protected Shape createSwingShape(Graphics2D g) {
        return getShapeSwingFont().createGlyphVector(g.getFontRenderContext(), Objects.coalesce(getNode().getText(), "")).getOutline();
    }

    @Override
    public void prepareCanvasContext(Graphics2D g) {
        super.prepareCanvasContext(g);
        TextShape ts = getNode();
        double x = Numbers.doubleValue(ts.getX());
        double wrappingWidth = Numbers.doubleValue(ts.getWrappingWidth());
        // Partial implementation that doesn't support multi-line text wrapping. TODO: Add multi-line wrapping support
        if (wrappingWidth > 0) {
            int textWidth = g.getFontMetrics(getShapeSwingFont()).stringWidth(Objects.coalesce(ts.getText(), ""));
            TextAlignment textAlignment = ts.getTextAlignment();
            if (textAlignment == TextAlignment.CENTER)
                x += (wrappingWidth - textWidth) / 2;
            else if (textAlignment == TextAlignment.RIGHT)
                x += (wrappingWidth - textWidth);
        }
        g.translate(x, ts.getY() + vPosToBaselineOffset(ts.getTextOrigin(), g));
    }

    private double vPosToBaselineOffset(VPos vpos, Graphics2D g) {
        if (vpos != null && vpos != VPos.BASELINE) {
            FontMetrics fontMetrics = g.getFontMetrics(getShapeSwingFont());
            switch (vpos) {
                case TOP:
                    return fontMetrics.getAscent();
                case CENTER:
                    return fontMetrics.getAscent() - fontMetrics.getHeight() / 2;
                case BOTTOM:
                    return -fontMetrics.getDescent();
            }
        }
        return 0;
    }
}
