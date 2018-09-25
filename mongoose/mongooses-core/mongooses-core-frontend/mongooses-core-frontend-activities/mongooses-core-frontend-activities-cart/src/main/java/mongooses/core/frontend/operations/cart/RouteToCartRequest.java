package mongooses.core.frontend.operations.cart;

import mongooses.core.frontend.activities.cart.CartRouting;
import webfx.framework.operations.route.RoutePushRequest;
import webfx.platforms.core.services.json.Json;
import webfx.platforms.core.services.windowhistory.spi.BrowsingHistory;

import java.time.Instant;

/**
 * @author Bruno Salmon
 */
public final class RouteToCartRequest extends RoutePushRequest {

    public RouteToCartRequest(Object cartUuidOrDocument, BrowsingHistory history) {
        super(CartRouting.getCartPath(cartUuidOrDocument), history, Json.createObject().set("refresh", Instant.now()));
    }

}