package mongoose.activities.shared.logic.calendar.graphic;

import mongoose.activities.shared.logic.calendar.Calendar;
import mongoose.activities.shared.logic.calendar.graphic.impl.CalendarGraphicImpl;
import naga.framework.ui.i18n.I18n;
import naga.toolkit.drawing.spi.DrawingNode;

/**
 * @author Bruno Salmon
 */
public interface CalendarGraphic {

    Calendar getCalendar();

    DrawingNode getDrawingNode();

    void setCalendar(Calendar calendar);

    static CalendarGraphic create(Calendar calendar, I18n i18n) {
        return new CalendarGraphicImpl(calendar, i18n);
    }
}
