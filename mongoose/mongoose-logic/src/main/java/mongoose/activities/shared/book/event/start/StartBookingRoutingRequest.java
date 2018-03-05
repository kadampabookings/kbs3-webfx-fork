package mongoose.activities.shared.book.event.start;

import mongoose.activities.shared.generic.routing.MongooseRoutingUtil;
import naga.framework.ui.router.PushRoutingRequest;
import naga.platform.client.url.history.History;

/**
 * @author Bruno Salmon
 */
public class StartBookingRoutingRequest extends PushRoutingRequest {

    public StartBookingRoutingRequest(Object eventId, History history) {
        super(MongooseRoutingUtil.interpolateEventIdInPath(eventId, StartBookingRouting.PATH), history);
    }
}