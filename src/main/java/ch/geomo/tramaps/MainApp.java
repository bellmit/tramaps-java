package ch.geomo.tramaps;

import ch.geomo.tramaps.conflicts.Conflict;
import ch.geomo.tramaps.conflicts.ConflictFinder;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.test.ExampleMetroMap;
import com.vividsolutions.jts.geom.Envelope;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;

public class MainApp extends Application {

    private MetroMap map;
    private Stage stage;

    private double edgeMargin = 25;
    private double routeMargin = 5;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        this.stage.setTitle("Tramaps GUI");
        this.initLayout();
        this.map = new ExampleMetroMap();
//        this.scale();
        this.displace();
        this.draw();
    }

    public void initLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getClassLoader().getResource("tramaps.fxml"));
    }

    public void draw() {
        Group group = new Group();
        Envelope bbox = this.map.getBoundingBox();

        Canvas canvas = new Canvas(bbox.getWidth() + 100, bbox.getHeight() + 100);
        GraphicsContext context = canvas.getGraphicsContext2D();

        this.drawMetroMap(context, bbox);

        double scale = 600 / bbox.getHeight();
        canvas.setScaleX(scale);
        canvas.setScaleY(scale);

        // hack -> to be removed
        group.setRotate(270);

        canvas.setTranslateX(50);
        canvas.setTranslateY(-bbox.getMinX() + 50);

        group.getChildren().add(canvas);

        group.setAutoSizeChildren(true);
        this.stage.setScene(new Scene(group));
//        this.stage.setHeight(600);
//        this.stage.setWidth(600/bbox.getWidth()*bbox.getHeight());
        this.stage.show();

    }

    private void drawMetroMap(GraphicsContext context, Envelope bbox) {

        // start drawing at the top left
        context.translate(-bbox.getMinX() + 50, -bbox.getMinY() + 50);

        int max = (int) Math.ceil(Math.max(bbox.getMaxX(), bbox.getMaxY())) + 1000;
        for (int i = -max, j = 0; i < max; i = i + 25, j = j + 25) {
            context.setStroke(j % 100 == 0 ? Color.GRAY : Color.LIGHTGRAY);
            context.strokeLine(i, 0, i, max * 2);
            context.strokeLine(0, i, max * 2, i);
        }

        this.map.getEdges().forEach(edge -> {
            double width = edge.getEdgeWidth(this.routeMargin);
            context.setLineWidth(width);
            context.setStroke(Color.rgb(139, 187, 206, 0.5d));
            context.setLineCap(StrokeLineCap.BUTT);
            if (edge.getVertices().isEmpty()) {
                context.strokeLine(edge.getNodeA().getX(), edge.getNodeA().getY(), edge.getNodeB().getX(), edge.getNodeB().getY());
            }
            else {
                context.beginPath();
                context.moveTo(edge.getNodeA().getX(), edge.getNodeA().getY());
                context.lineTo(edge.getVertices().get(0).getX(), edge.getVertices().get(0).getY());
                context.lineTo(edge.getNodeB().getX(), edge.getNodeB().getY());
                context.stroke();
            }
        });
        this.map.getNodes().forEach(node -> {
            Envelope station = node.getSignature().getGeometry().getEnvelopeInternal();
            context.setFill(Color.BLACK);
            context.fillRoundRect(station.getMinX() - 5, station.getMinY() - 5, station.getWidth() + 10, station.getHeight() + 10, 25, 25);
            context.setFill(Color.WHITE);
            context.fillRoundRect(station.getMinX(), station.getMinY(), station.getWidth(), station.getHeight(), 25, 25);
        });
        this.map.getEdges().forEach(edge -> {
            context.setLineWidth(1);
            context.setStroke(Color.BLACK);
            if (edge.getVertices().isEmpty()) {
                context.strokeLine(edge.getNodeA().getX(), edge.getNodeA().getY(), edge.getNodeB().getX(), edge.getNodeB().getY());
            }
            else {
                context.beginPath();
                context.moveTo(edge.getNodeA().getX(), edge.getNodeA().getY());
                context.lineTo(edge.getVertices().get(0).getX(), edge.getVertices().get(0).getY());
                context.lineTo(edge.getNodeB().getX(), edge.getNodeB().getY());
                context.stroke();
            }
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
            Envelope bbox2 = conflict.getConflictPolygon().getEnvelopeInternal();
            context.fillRect(bbox2.getMinX(), bbox2.getMinY(), bbox2.getWidth(), bbox2.getHeight());
        });

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

    public void scaleSubGraphs() {
        DisplacementHandler handler = new DisplacementHandler();
        System.out.println("Before Sub Graph Scaling:");
        System.out.println(map);
        handler.makeSpaceByScalingSubGraphs(map, this.routeMargin, this.edgeMargin);
        System.out.println("Scaled Map:");
        System.out.println(map);
        System.out.println("Finish");
    }

    public void displace() {
        DisplacementHandler handler = new DisplacementHandler();
        System.out.println("Before Displacement:");
        System.out.println(map);
        handler.makeSpaceByDisplacement(map, this.routeMargin, this.edgeMargin);
        System.out.println("Modified Map:");
        System.out.println(map);
        System.out.println("Finish");
    }

    public static void main(String[] args) throws IOException {
        MainApp.launch(args);
    }

}
