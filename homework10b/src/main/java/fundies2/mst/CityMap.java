package fundies2.mst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Graph representation of a set of cities
 */
public class CityMap {
    private static final String CITY_DATA_PATH = "resources/cities.csv";
    private static final int NUM_DATA_FIELDS = 4;
    private static final int CITY_NAME_FIELD = 0;
    private static final int POPULATION_FIELD = 1; // unused
    private static final int LATITUDE_FIELD = 2;
    private static final int LONGITUDE_FIELD = 3;
    private static final int NUM_CITIES = 50; // file has 59

    private static final int EARTH_RADIUS_IN_MILES = 3963;

    private final Graph<City> graph = new Graph<>();

    /**
     * Constructs a graph representation of a set of cities.
     *
     * @throws IOException if the city data cannot be read
     */
    public CityMap() throws IOException {
        makeGraph();
    }

    /**
     * Gets the graph representation of the cities, where each city is anode,
     * and there are edges between each pair of cities weighted with the
     * distance between them.
     *
     * @return a graph representation of cities and the distances between them
     */
    public final Graph<City> getGraph() {
        return graph;
    }

    private static double distanceInMiles(City city1, City city2) {
        // Distance, d = 3963.0 * arccos[(sin(lat1) * sin(lat2)) + cos(lat1) * cos(lat2) * cos(long2 – long1)]
        return EARTH_RADIUS_IN_MILES
                * Math.acos((Math.sin(Math.toRadians(city1.getLatitude()))
                * Math.sin(Math.toRadians(city2.getLatitude())))
                + Math.cos(Math.toRadians(city1.getLatitude()))
                * Math.cos(Math.toRadians(city2.getLatitude()))
                * Math.cos(Math.toRadians(city2.getLongitude() - city1.getLongitude())));
    }

    private void makeGraph() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(CITY_DATA_PATH));
        int numCities = 0;
        for (String line : lines) {
            if (numCities++ >= NUM_CITIES) {
                break;
            }
            String[] fields = line.split(",");
            if (fields.length == NUM_DATA_FIELDS) {
                City city = new City(fields[CITY_NAME_FIELD],
                        Double.parseDouble(fields[LATITUDE_FIELD]),
                        Double.parseDouble(fields[LONGITUDE_FIELD]));
                graph.addNode(city);
            } else {
                System.err.println("Unable to parse line: " + line);
            }
        }
        List<Graph.Node<City>> nodesCopy = graph.getNodes();
        for (Graph.Node<City> node : nodesCopy) {
            addEdgesHelper(node);
        }
    }

    private void addEdgesHelper(Graph.Node<City> currNode) {
        List<Graph.Node<City>> nodesCopy = graph.getNodes();
        for (Graph.Node<City> node : nodesCopy) {
            if (!node.equals(currNode)) {
                graph.addEdge(node, currNode, (int) distanceInMiles(node.getData(), currNode.getData()));
            }
        }
    }
}
