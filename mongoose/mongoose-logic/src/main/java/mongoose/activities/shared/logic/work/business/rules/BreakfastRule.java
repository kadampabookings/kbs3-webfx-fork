package mongoose.activities.shared.logic.work.business.rules;

import mongoose.activities.shared.logic.work.WorkingDocument;
import mongoose.activities.shared.logic.work.business.logic.OptionLogic;
import mongoose.entities.Option;
import mongoose.services.EventService;

/**
 * @author Bruno Salmon
 */
public class BreakfastRule extends BusinessRule {

    @Override
    public void apply(WorkingDocument wd) {
        if (!wd.hasAccommodation() || !wd.hasMeals())
            wd.removeBreakfast();
        else {
            Option breakfastOption = getBreakfastOption(wd.getEventService());
            // Breakfast added only if it is on same site as accommodation
            if (!wd.hasBreakfast() && breakfastOption != null && breakfastOption.getSite() == wd.getAccommodationLine().getSite())
                addNewDependentLine(wd, breakfastOption, wd.getAccommodationLine(), 1);
            else
                wd.removeBreakfast();
        }
    }

    private static Option getBreakfastOption(EventService eventService) {
        Option breakfastOption = eventService.getBreakfastOption();
        if (breakfastOption == null)
            eventService.setBreakfastOption(breakfastOption = eventService.findFirstConcreteOption(OptionLogic::isBreakfastOption));
        return breakfastOption;
    }
}
