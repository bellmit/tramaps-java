package ch.geomo.tramaps;

import ch.geomo.tramaps.conflicts.Conflict;
import ch.geomo.tramaps.conflicts.ConflictFinder;
import ch.geomo.tramaps.geo.util.GeomUtil;
import ch.geomo.tramaps.graph.Edge;
import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.MetroMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class MainApp extends Application {

    private MetroMap map;

    private Stage stage;
    private BorderPane layout;

    private double edgeMargin = 5;
    private double routeMargin = 5;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        this.stage.setTitle("Tramaps GUI");
        this.initLayout();
        this.createTestMap();
        this.scale();
        this.draw();
    }

    public void initLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getClassLoader().getResource("tramaps.fxml"));
        this.layout = loader.load();
    }

    public void draw() {
        Group group = new Group();
        Envelope bbox = this.map.getBoundingBox();
        Canvas canvas = new Canvas(bbox.getWidth()*3, bbox.getHeight()*3);
        GraphicsContext context = canvas.getGraphicsContext2D();
        this.drawMetroMap(context);
        canvas.setTranslateX(bbox.getMinX());
        canvas.setTranslateY(-bbox.getMinY()*2);
        canvas.setRotate(270);
        group.getChildren().add(canvas);
        this.stage.setScene(new Scene(group));
        this.stage.show();
    }

    private void drawMetroMap(GraphicsContext context) {

        this.map.getEdges().forEach(edge -> {
            double width = edge.getEdgeWidth(this.edgeMargin);
            context.setLineWidth(width);
            context.setStroke(Color.rgb((int) width, (int) width, (int) width, 0.5d));
            context.strokeLine(edge.getNodeA().getX(), edge.getNodeA().getY(), edge.getNodeB().getX(), edge.getNodeB().getY());
        });
        this.map.getNodes().forEach(node -> {
            Envelope station = node.getSignature().getGeometry().getEnvelopeInternal();
            context.setFill(Color.rgb(139, 187, 206, 0.5d));
            context.fillRect(station.getMinX(), station.getMinY(), station.getWidth(), station.getHeight());
        });
        this.map.getEdges().forEach(edge -> {
            context.setLineWidth(1);
            context.setStroke(Color.BLACK);
            context.strokeLine(edge.getNodeA().getX(), edge.getNodeA().getY(), edge.getNodeB().getX(), edge.getNodeB().getY());
        });
        context.translate(-5, -5);
        this.map.getNodes().forEach(node -> {
            context.setFill(Color.rgb(0, 145, 255));
            context.fillOval(node.getX(), node.getY(), 10, 10);
        });

        context.translate(5, 5);
        Set<Conflict> conflicts = new ConflictFinder(routeMargin, edgeMargin).getConflicts(map.getEdges(), map.getNodes());
        conflicts.forEach(conflict -> {
            context.setFill(Color.rgb(240, 88, 88, 0.4));
            Envelope bbox = conflict.getConflictPolygon().getEnvelopeInternal();
            context.fillRect(bbox.getMinX(), bbox.getMinY(), bbox.getWidth(), bbox.getHeight());
        });

    }

    public void createTestMap() {

        this.map = new MetroMap();

        Node a = new Node(GeomUtil.createPoint(new Coordinate(150, 200)));
        Node b = new Node(GeomUtil.createPoint(new Coordinate(150, 100)));
        Node c = new Node(GeomUtil.createPoint(new Coordinate(200, 100)));
        Node d = new Node(GeomUtil.createPoint(new Coordinate(200, 150)));
        Node e = new Node(GeomUtil.createPoint(new Coordinate(200, 250)));
        Node f = new Node(GeomUtil.createPoint(new Coordinate(150, 300)));
        Node g = new Node(GeomUtil.createPoint(new Coordinate(100, 300)));

        Edge ab = new Edge(a, b);
        Edge bc = new Edge(b, c);
        Edge cd = new Edge(c, d);
        Edge de = new Edge(d, e);
        Edge ef = new Edge(e, f);
        Edge fg = new Edge(f, g);

        Route line1 = new Route(20, Color.BLUE);
        Route line2 = new Route(20, Color.RED);
        Route line3 = new Route(20, Color.GREEN);
        Route line4 = new Route(20, Color.YELLOW);
        Route line5 = new Route(20, Color.ORANGE);
        Route line6 = new Route(20, Color.MAGENTA);

        ab.setRoutes(Arrays.asList(line1, line2, line3, line4, line5));
        bc.setRoutes(Arrays.asList(line1, line2, line4, line5));
        cd.setRoutes(Arrays.asList(line1, line2, line4, line5));
        de.setRoutes(Arrays.asList(line1, line2, line4, line5));
        ef.setRoutes(Arrays.asList(line1, line2, line4, line5, line6));
        fg.setRoutes(Arrays.asList(line1, line6));

        this.map.getNodes().addAll(Arrays.asList(a, b, c, d, e, f, g));
        this.map.getEdges().addAll(Arrays.asList(ab, bc, cd, de, ef, fg));

    }

    public void scale() {

        DisplacementHandler handler = new DisplacementHandler();
        System.out.println("Before Scaling:");
        System.out.println(map);
        handler.makeSpaceByScaling(map, this.routeMargin, this.edgeMargin);
        System.out.println("Scaled Map:");
        System.out.println(map);
        System.out.println("Finish");

    }

    public static void main(String[] args) throws IOException {
        MainApp.launch(args);
    }

}
