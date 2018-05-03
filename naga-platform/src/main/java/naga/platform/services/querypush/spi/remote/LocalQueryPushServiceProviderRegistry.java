package naga.platform.services.querypush.spi.remote;

import naga.platform.services.querypush.spi.QueryPushServiceProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
public class LocalQueryPushServiceProviderRegistry {

    private static final Map</* dataSourceId */ Object, QueryPushServiceProvider> localConnectedProviders = new HashMap<>();

    public static void registerLocalConnectedProvider(Object dataSourceId, QueryPushServiceProvider localConnectedProvider) {
        localConnectedProviders.put(dataSourceId, localConnectedProvider);
    }

    public static QueryPushServiceProvider getLocalConnectedProvider(Object dataSourceId) {
        return localConnectedProviders.get(dataSourceId);
    }
}