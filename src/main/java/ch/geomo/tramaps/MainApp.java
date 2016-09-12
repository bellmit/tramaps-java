/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps;

import ch.geomo.tramaps.example.MetroMapExampleGraph;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.MetroMapDrawer;
import ch.geomo.tramaps.map.displacement.MetroMapLineSpaceHandler;
import ch.geomo.tramaps.map.displacement.alg.DisplaceHandler;
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

    private static final double MAX_HEIGHT = 750;
    private static final double MAX_WIDTH = 1200;

    private MetroMap map;
    private Stage stage;

    private void makeSpace(@NotNull Supplier<MetroMapLineSpaceHandler> makeSpaceHandlerSupplier) {
        MetroMapLineSpaceHandler handler = makeSpaceHandlerSupplier.get();
        handler.makeSpace();
    }

    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {

        stage = primaryStage;
        stage.setTitle("Tramaps GUI");

        map = new MetroMapExampleGraph(5, 25);
        //map = new MetroMapZuerich(5, 25);

        //makeSpace(() -> new ScaleHandler(map));
        makeSpace(() -> new DisplaceHandler(map));
        //makeSpace(() -> new DisplaceRadiusHandler(map));

        drawMetroMap();

    }

    private void drawMetroMap() {

        Envelope bbox = map.getBoundingBox();

        double margin = 150;

        double width = bbox.getWidth() + margin * 2;
        double height = bbox.getHeight() + margin * 2;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext context = canvas.getGraphicsContext2D();

        double factor = MAX_HEIGHT / height;

        MetroMapDrawer drawer = new MetroMapDrawer(map, margin);
        //SimpleGraphDrawer drawer = new SimpleGraphDrawer(map, margin, factor);
        drawer.draw(context, bbox);

        canvas.setScaleX(factor);
        canvas.setScaleY(factor);

        showMetroMap(canvas, MAX_HEIGHT, MAX_HEIGHT / height * width);

    }

    private void showMetroMap(@NotNull Canvas canvas, double height, double width) {

        Group group = new Group();
        group.getChildren().add(canvas);

        ScrollPane scrollPane = new ScrollPane(group);
        scrollPane.setPrefSize(300, 300);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");

        Scene scene = new Scene(scrollPane, width + 5, height + 5);
        stage.setScene(scene);
        stage.setWidth(Math.min(MAX_WIDTH, width) + 5);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String... args) throws IOException {
        MainApp.launch(args);
    }

}
