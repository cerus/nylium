package dev.cerus.nylium.server.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.cerus.nylium.server.block.states.IdentifiableState;
import dev.cerus.nylium.server.block.states.State;
import dev.cerus.nylium.server.key.NamespacedKey;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BlockRegistry {

    private static final Logger LOGGER = Logger.getLogger(BlockRegistry.class.getName());

    private static final Map<NamespacedKey, Set<State>> POSSIBLE_STATES_MAP = new HashMap<>();
    private static final Map<IdentifiableState, Integer> STATE_PROTOCOL_MAP = new HashMap<>();
    private static final Map<Integer, IdentifiableState> PROTOCOL_STATE_MAP = new HashMap<>();
    public static int BITS_PER_BLOCK;
    public static IdentifiableState AIR;

    /**
     * Attempts to load the global palette from the provided input stream
     *
     * @param inputStream Stream to load the palette from
     */
    public static void load(final InputStream inputStream) throws JsonIOException, JsonSyntaxException {
        final JsonObject rootObj = JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();
        for (final String key : rootObj.keySet()) {
            final NamespacedKey type = NamespacedKey.of(key);
            final JsonObject childObj = rootObj.get(key).getAsJsonObject();

            // Map properties object into a map containing the name of the property and all possible values for that property
            final JsonObject propertiesValuesObj = childObj.has("properties")
                    ? childObj.get("properties").getAsJsonObject() : new JsonObject();
            final Map<String, Set<String>> propertiesValuesMap = propertiesValuesObj.keySet().stream()
                    .collect(Collectors.toMap(s -> s, s -> {
                        return StreamSupport.stream(propertiesValuesObj.get(s).getAsJsonArray().spliterator(), false)
                                .map(JsonElement::getAsString)
                                .collect(Collectors.toSet());
                    }));

            // Parse possible states
            final Set<State> possibleStates = new HashSet<>();
            final JsonArray statesArr = childObj.get("states").getAsJsonArray();
            for (final JsonElement stateElem : statesArr) {
                final JsonObject stateObj = stateElem.getAsJsonObject();
                final int id = stateObj.get("id").getAsInt();
                final boolean def = stateObj.has("default") && stateObj.get("default").getAsBoolean();

                // Parse properties object for this state
                final JsonObject propertiesObj = stateObj.has("properties")
                        ? stateObj.get("properties").getAsJsonObject() : new JsonObject();
                final Map<String, String> propertiesMap = propertiesObj.keySet().stream()
                        .collect(Collectors.toMap(s -> s, s -> propertiesObj.get(s).getAsString()));

                // Construct state and store for later use
                final State state = new State(
                        propertiesValuesMap,
                        propertiesMap,
                        def
                );
                possibleStates.add(state);

                final IdentifiableState identifiableState = IdentifiableState.of(type, state);
                STATE_PROTOCOL_MAP.put(identifiableState, id);
                PROTOCOL_STATE_MAP.put(id, identifiableState);
            }

            // Register this type and its possible states
            POSSIBLE_STATES_MAP.put(type, possibleStates);
        }

        AIR = getState(0);

        BITS_PER_BLOCK = STATE_PROTOCOL_MAP.values().stream()
                .mapToInt(value -> value)
                .max()
                .orElse(0);
        BITS_PER_BLOCK = (int) Math.ceil(Math.log(BITS_PER_BLOCK) / Math.log(2));

        LOGGER.info("Loaded " + POSSIBLE_STATES_MAP.size() + " blocks (bpb: " + BITS_PER_BLOCK + ")");
    }

    public static int getProtocolId(final IdentifiableState state) {
        return STATE_PROTOCOL_MAP.get(state);
    }

    public static State getDefaultState(final NamespacedKey type) {
        return POSSIBLE_STATES_MAP.get(type).stream()
                .filter(State::isDefault)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find a default state for " + type.toString()));
    }

    public static IdentifiableState getState(final int protocol) {
        return PROTOCOL_STATE_MAP.get(protocol);
    }

}
