package ch.geomo.tramaps;

import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.MetroMapDrawer;
import ch.geomo.tramaps.map.displacement.DisplaceHandler;
import ch.geomo.tramaps.map.displacement.MakeSpaceHandler;
import ch.geomo.tramaps.example.ExampleMetroMap;
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

    private double edgeMargin = 25;
    private double routeMargin = 5;

    private void makeSpace(@NotNull Supplier<MakeSpaceHandler> makeSpaceHandlerSupplier) {
        MakeSpaceHandler handler = makeSpaceHandlerSupplier.get();
        handler.makeSpace(map, this.routeMargin, this.edgeMargin);
    }

    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {

        this.stage = primaryStage;
        this.stage.setTitle("Tramaps GUI");
        this.initLayout();

        this.map = new ExampleMetroMap();

        this.makeSpace(ScaleHandler::new);
//        this.makeSpace(DisplaceHandler::new);

        this.draw();

    }

    private void draw() {

        Group group = new Group();
        Envelope bbox = this.map.getBoundingBox();

        Canvas canvas = new Canvas(bbox.getWidth() + 100, bbox.getHeight() + 100);
        GraphicsContext context = canvas.getGraphicsContext2D();

        MetroMapDrawer drawer = new MetroMapDrawer(routeMargin, edgeMargin);
        drawer.setMetroMap(map);
        drawer.draw(context, bbox);

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
        this.stage.show();

    }

    private void initLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getClassLoader().getResource("tramaps.fxml"));
    }

    public static void main(String[] args) throws IOException {
        MainApp.launch(args);
    }

}
