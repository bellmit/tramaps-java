/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps;

import ch.geomo.tramaps.example.ExampleMetroMap;
import ch.geomo.tramaps.example.MetroMapZuerich;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.MetroMapDrawer;
import ch.geomo.tramaps.map.SimpleGraphDrawer;
import ch.geomo.tramaps.map.displacement.MetroMapLineSpaceHandler;
import ch.geomo.tramaps.map.displacement.alg.DisplaceHandler;
import ch.geomo.tramaps.map.displacement.radius.DisplaceRadiusHandler;
import ch.geomo.tramaps.map.displacement.scale.ScaleHandler;
import com.vividsolutions.jts.geom.Envelope;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Supplier;

public class MainApp extends Application {

    private MetroMap map;
    private Stage stage;

    private void makeSpace(@NotNull Supplier<MetroMapLineSpaceHandler> makeSpaceHandlerSupplier) {
        MetroMapLineSpaceHandler handler = makeSpaceHandlerSupplier.get();
        handler.makeSpace(map);
    }

    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {

        stage = primaryStage;
        stage.setTitle("Tramaps GUI");
        initLayout();

         map = new ExampleMetroMap(5, 25);
//        map = new MetroMapZuerich(5, 25);

//        makeSpace(DisplaceRadiusHandler::new);
        // makeSpace(ScaleHandler::new);
         makeSpace(DisplaceHandler::new);

        draw();

    }

    private void draw() {

        Envelope bbox = map.getBoundingBox();

        double maxHeight = 700;
        double margin = 150;

        double width = bbox.getWidth() + margin * 2;
        double height = bbox.getHeight() + margin * 2;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext context = canvas.getGraphicsContext2D();

        MetroMapDrawer drawer = new MetroMapDrawer(map, margin);
        // SimpleGraphDrawer drawer = new SimpleGraphDrawer(map, margin);
        drawer.draw(context, bbox);
        show(canvas, maxHeight, maxHeight / height * width);

        double scale = maxHeight / height;
        canvas.setScaleX(scale);
        canvas.setScaleY(scale);

        double correction = 1 / scale * 2.8;
        canvas.setTranslateX(0 - canvas.getWidth() / correction);
        canvas.setTranslateY(0 - canvas.getHeight() / correction);

//        canvas.setScaleY(-scale);
//        canvas.setScaleY(scale);

        // hack -> to be removed
//        group.setRotate(270);

//        canvas.setTranslateX(50);
//        canvas.setTranslateY(-bbox.getMinX() + 50);
//        canvas.setTranslateX(-250);
//        canvas.setTranslateY(-250);


    }

    private void show(Canvas canvas, double defaultHeight, double defaultWidth) {

        Region region = new Region();
        region.setPrefHeight(defaultHeight);
        region.setPrefWidth(defaultWidth);

        Group group = new Group(region);
        StackPane rootPane = new StackPane();
        rootPane.getChildren().add(group);
        Scene scene = new Scene(rootPane, defaultWidth, defaultHeight);
        group.scaleXProperty().bind(scene.widthProperty().divide(defaultWidth));
        group.scaleYProperty().bind(scene.heightProperty().divide(defaultHeight));

        group.getChildren().add(canvas);

        stage.setScene(scene);
        stage.show();

    }

    private void initLayout() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getClassLoader().getResource("tramaps.fxml"));
    }

    public static void main(String... args) throws IOException {
        MainApp.launch(args);
    }

}
