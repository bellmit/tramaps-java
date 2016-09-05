/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps;

import ch.geomo.tramaps.example.ExampleMetroMap;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.MetroMapDrawer;
import ch.geomo.tramaps.map.displacement.DisplaceHandler;
import ch.geomo.tramaps.map.displacement.MetroMapLineSpaceHandler;
import ch.geomo.tramaps.map.displacement.ScaleHandler;
import com.vividsolutions.jts.geom.Envelope;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

//        makeSpace(ScaleHandler::new);
        makeSpace(DisplaceHandler::new);

        draw();

    }

    private void draw() {

        Group group = new Group();
        Envelope bbox = map.getBoundingBox();

        Canvas canvas = new Canvas(bbox.getWidth() + 300, bbox.getHeight() + 300);
        GraphicsContext context = canvas.getGraphicsContext2D();

        MetroMapDrawer drawer = new MetroMapDrawer(map);
        drawer.draw(context, bbox);

        double scale = 600 / bbox.getHeight();
        canvas.setScaleX(scale);
        canvas.setScaleY(-scale);

        // hack -> to be removed
//        group.setRotate(270);

//        canvas.setTranslateX(50);
//        canvas.setTranslateY(-bbox.getMinX() + 50);
        canvas.setTranslateX(-250);
        canvas.setTranslateY(-250);

        group.getChildren().add(canvas);

        group.setAutoSizeChildren(true);
        stage.setScene(new Scene(group));
        stage.show();
        //System.exit(0);

    }

    private void initLayout() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getClassLoader().getResource("tramaps.fxml"));
    }

    public static void main(String... args) throws IOException {
        MainApp.launch(args);
    }

}
