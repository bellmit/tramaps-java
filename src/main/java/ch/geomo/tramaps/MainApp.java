/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps;

import ch.geomo.tramaps.example.MetroMapExampleGraph;
import ch.geomo.tramaps.example.MetroMapZuerich;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.MetroMapDrawer;
import ch.geomo.tramaps.map.displacement.LineSpaceHandler;
import ch.geomo.tramaps.map.displacement.alg.DisplaceLineSpaceHandler;
import com.vividsolutions.jts.geom.Envelope;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Supplier;

public class MainApp extends Application {

    private static final double MAX_HEIGHT = 600;
    private static final double MAX_WIDTH = 1200;

    private MetroMap map;
    private Stage stage;

    private void makeSpace(@NotNull Supplier<LineSpaceHandler> makeSpaceHandlerSupplier) {
        LineSpaceHandler handler = makeSpaceHandlerSupplier.get();
        handler.makeSpace();
    }

    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {

        stage = primaryStage;
        stage.setTitle("Tramaps GUI");

        map = new MetroMapExampleGraph();
        //map = new MetroMapZuerich();

        //makeSpace(() -> new ScaleHandler(map));
        makeSpace(() -> new DisplaceLineSpaceHandler(map));

        drawMetroMap();

    }

    private void drawMetroMap() {

        Envelope bbox = map.getBoundingBox();

        double margin = 100;

        double scaleFactor = MAX_HEIGHT / bbox.getHeight();

        double scaledHeight = bbox.getHeight() * scaleFactor + margin * 2;
        double scaledWidth = bbox.getWidth() * scaleFactor + margin * 2;

        Canvas canvas = new Canvas(scaledWidth, scaledHeight);
        GraphicsContext context = canvas.getGraphicsContext2D();

        MetroMapDrawer drawer = new MetroMapDrawer(map, margin, scaleFactor);
        drawer.draw(context, bbox);

        // workaround: scaling is done when drawing otherwise an exception like:
        // -> Requested texture dimension exceeds maximum texture size
        // canvas.setScaleX(scaleFactor);
        // canvas.setScaleY(scaleFactor);

        showMetroMap(canvas, scaledHeight, scaledWidth);

    }

    private void showMetroMap(@NotNull Canvas canvas, double height, double width) {

        Group group = new Group();
        group.getChildren().add(canvas);

        ScrollPane scrollPane = new ScrollPane(group);

        Scene scene = new Scene(scrollPane, width + 5, height + 5);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");

        stage.setScene(scene);
        stage.setWidth(Math.min(MAX_WIDTH, width) + 5);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String... args) throws IOException {
        MainApp.launch(args);
    }

}
