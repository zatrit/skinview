package zatrit.skins.lib.resolver.capes;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import lombok.val;
import zatrit.skins.lib.api.Resolver;

/**
 * Implementation of {@link Resolver} for <a href="https://www.wurstclient.net/">Meteor Client</a>
 * based on {@link CapesListResolver}.
 */
public final class MeteorResolver extends CapesListResolver {
    private static final String BASE_URL = "https://meteorclient.com/api";
    private static final String OWNERS_URL = BASE_URL + "/capeowners";
    private static final String CAPES_URL = BASE_URL + "/capes";
    private Map<String, String> capes;

    private static @NotNull Map<String, String> parseTable(Reader reader) {
        val scanner = new Scanner(reader);
        val map = new HashMap<String, String>();

        while (scanner.hasNextLine()) {
            val pair = scanner.nextLine().split(" ");
            map.put(pair[0], pair[1]);
        }

        return map;
    }

    @Override
    protected @NotNull Map<String, String> fetchList() throws IOException {
        capes = parseTable(new InputStreamReader(new URL(CAPES_URL).openStream()));

        val owners = parseTable(new InputStreamReader(new URL(OWNERS_URL).openStream()));
        val uuidOwners = new HashMap<String, String>();

        for (val pair : owners.entrySet()) {
            uuidOwners.put(pair.getKey().replace("-", ""), pair.getValue());
        }

        return uuidOwners;
    }

    @Override
    protected String getUrl(String capeName) {
        return capes.get(capeName);
    }
}
