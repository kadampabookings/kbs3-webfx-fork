package mongoose.activities.backend.events;

import mongoose.activities.shared.generic.routing.MongooseRoutingUtil;
import naga.framework.activity.combinations.domainpresentation.impl.DomainPresentationActivityContextFinal;
import naga.framework.orm.entity.Entity;
import naga.framework.router.util.PathBuilder;
import naga.framework.ui.router.UiRoute;
import naga.platform.client.url.history.History;
import naga.util.async.AsyncFunction;
import naga.util.async.Future;

/**
 * @author Bruno Salmon
 */
public class EventsRouting {

    private final static String ANY_PATH = "/events(/organization/:organizationId)?";
    private final static String ALL_EVENTS_PATH = "/events";
    private final static String ORGANIZATION_PATH = "/events/organization/:organizationId";

    public static UiRoute<?> uiRoute() {
        return UiRoute.createRegex(
                PathBuilder.toRegexPath(ANY_PATH)
                , false
                , EventsPresentationActivity::new
                , DomainPresentationActivityContextFinal::new
        );
    }

    public static void route(History history) {
        history.push(ALL_EVENTS_PATH);
    }

    public static AsyncFunction<EventsRoutingRequest, Void> executor() {
        return request -> Future.runAsync(() -> route(request.getHistory()));
    }

    public static void routeUsingOrganization(Entity organization, History history) {
        MongooseRoutingUtil.routeUsingEntityId(organization, history, EventsRouting::routeUsingOrganizationId);
    }

    public static void routeUsingOrganizationId(Object organizationId, History history) {
        history.push(MongooseRoutingUtil.interpolateOrganizationIdInPath(organizationId, ORGANIZATION_PATH));
    }
}
