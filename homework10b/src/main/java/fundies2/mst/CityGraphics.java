package fundies2.mst;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The View portion of the application.
 */
public class CityGraphics extends Application {
    private static final int MAX_LATITUDE = 50;
    private static final int MIN_LATITUDE = 25;
    private static final int MAX_LONGITUDE = -65;
    private static final int MIN_LONGITUDE = -125;
    private static final int PIXELS_PER_DEGREE = 10;
    private static final int CANVAS_HEIGHT = (MAX_LATITUDE - MIN_LATITUDE) * PIXELS_PER_DEGREE;
    private static final int CANVAS_WIDTH = (MAX_LONGITUDE - MIN_LONGITUDE) * PIXELS_PER_DEGREE;
    private static final int SCENE_WIDTH = CANVAS_WIDTH;
    private static final int SCENE_HEIGHT = CANVAS_HEIGHT;
    private static final Color NODE_COLOR = Color.BLUE;
    private static final Color EDGE_COLOR = Color.LIGHTGREEN;
    private static final Color NODE_HIGHLIGHT_COLOR = Color.PURPLE;
    private static final Color EDGE_HIGHLIGHT_COLOR = Color.PURPLE;
    private static final int NODE_RADIUS = 2;
    private static final int MIN_DISTANCE_FOR_LINE = 10;

    private static boolean isInitialized = false;
    private static CityGraphics cityGraphics;
    private final Group group = new Group();

    @Override
    public void init() {
        cityGraphics = this;
    }

    /**
     * Gets the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static CityGraphics getInstance() {
        // Don't return this until initialization is complete.
        while (!isInitialized) {
            Thread.yield();
        }
        return cityGraphics;
    }

    @Override
    public void start(Stage primaryStage) {
        final Pane pane = new Pane(group);
        pane.setPrefWidth(CANVAS_WIDTH);
        pane.setPrefHeight(CANVAS_HEIGHT);

        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);

        primaryStage.setTitle("Cities");
        primaryStage.setScene(scene);
        primaryStage.show();
        isInitialized = true;
    }

    private void drawNode(Graph.Node<City> node, int radius, Color color) {
        final int x = getX(node.getData());
        if (x < 0) {
            System.err.println("Could not display " + node.getData());
            return;
        }
        final int y = getY(node.getData());
        Circle circle = new Circle(x, y, radius);
        circle.setFill(color);
        addHover(circle, x, y, node.getData().getName());
        Platform.runLater(() -> {
            group.getChildren().add(circle);
        });
    }

    private void drawNode(Graph.Node<City> node) {
        drawNode(node, NODE_RADIUS, NODE_COLOR);
    }

    private void addHover(Node node, int x, int y, String s) {
        // TODO: Make prettier. https://stackoverflow.com/a/40446828/631051
        final Text text = new Text(x, y, s);
        node.hoverProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean show) -> {
                    if (show) {
                        group.getChildren().add(text);
                    } else {
                        group.getChildren().remove(text);
                    }
                });
    }

    private void drawEdge(Graph.Edge<City> edge, Color color, boolean enableHover) {
        City city1 = edge.getNode1().getData();
        City city2 = edge.getNode2().getData();
        final Line line = new Line(getX(city1), getY(city1), getX(city2), getY(city2));
        if (enableHover) {
            addHover(
                    line,
                    (getX(city1) + getX(city2)) / 2,
                    (getY(city1) + getY(city2)) / 2,
                    String.format("%s - %s (%d)", city1.getName(), city2.getName(), edge.getWeight()));
        }
        line.setStroke(color);
        Platform.runLater(() -> {
            group.getChildren().add(line);
        });
    }

    private void drawEdge(Graph.Edge<City> edge) {
        drawEdge(edge, EDGE_COLOR, false);
    }

    public void drawGraph(Graph<City> graph) {
        for (Graph.Edge<City> edge : graph.getEdges()) {
            if (edge.getWeight() >= MIN_DISTANCE_FOR_LINE) {
                drawEdge(edge);
            }
        }

        for (Graph.Node<City> node : graph.getNodes()) {
            drawNode(node);
        }
    }

    private void highlightNode(Graph.Node<City> node, Color color) {
        drawNode(node, NODE_RADIUS, color);
    }

    private void highlightNode(Graph.Node<City> node) {
        highlightNode(node, NODE_HIGHLIGHT_COLOR);
    }

    private void highlightEdge(Graph.Edge<City> edge, Color color) {
        drawEdge(edge, color, true);
    }

    /**
     * Highlights the specified edge and adds hover text.
     *
     * @param edge the edge to highlight
     */
    public void highlightEdge(Graph.Edge<City> edge) {
        highlightEdge(edge, EDGE_HIGHLIGHT_COLOR);
    }

    private int getX(City city) {
        return longToX(city.getLongitude());
    }

    private int getY(City city) {
        return latToY(city.getLatitude());
    }

    private int longToX(double longitude) {
        return (int) ((longitude - MIN_LONGITUDE) * PIXELS_PER_DEGREE);
    }

    private int latToY(double latitude) {
        return SCENE_HEIGHT - (int) ((latitude - MIN_LATITUDE) * PIXELS_PER_DEGREE);
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new Controller());
        thread.start();
        launch(args);
    }
}
