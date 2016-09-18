/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.tramaps.example;

import ch.geomo.tramaps.graph.Node;
import ch.geomo.tramaps.graph.Route;
import ch.geomo.tramaps.map.MetroMap;
import ch.geomo.tramaps.map.MetroMapEdgeBuilder;
import ch.geomo.tramaps.map.signature.RectangleStationSignature;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;

public class MetroMapZuerich extends MetroMap {

    public MetroMapZuerich(double routeMargin, double edgeMargin) {

        super(routeMargin, edgeMargin);

        Route s2 = new Route("S2", 20, Color.BLACK);
        Route s4 = new Route("S4", 20, Color.BLACK);
        Route s5 = new Route("S5", 20, Color.BLACK);
        Route s6 = new Route("S6", 20, Color.BLACK);
        Route s7 = new Route("S7", 20, Color.BLACK);
        Route s8 = new Route("S8", 20, Color.BLACK);
        Route s9 = new Route("S9", 20, Color.BLACK);
        Route s10 = new Route("S10", 20, Color.BLACK);
        Route s12 = new Route("S12", 20, Color.BLACK);
        Route s14 = new Route("S14", 20, Color.BLACK);
        Route s24 = new Route("S24", 20, Color.BLACK);

        Node hb = createNode(120, 105, "Zürich HB");

        Node uetlibergTrainCrossing = createCrossingNode(115, 90); // C1
        Node selnau = createNode(110, 85, "Selnau");
        Node selnauToBinzGiesshuebel = createJunctionNode(105, 80); // J1
        Node binz = createNode(95, 80, "Binz");
        Node friesenberg = createNode(90, 80, "Friesenberg");
        Node giesshuebel = createNode(110, 75, "Giesshübel");
        Node saalsporthalle = createNode(110, 70, "Saalsporthalle");

        new MetroMapEdgeBuilder(this)
                .routes(s4, s10)
                .station(hb)
                .bend(120, 95) // B1
                .crossing(uetlibergTrainCrossing)
                .station(selnau)
                .junction(selnauToBinzGiesshuebel)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s4)
                .junction(selnauToBinzGiesshuebel)
                .station(giesshuebel)
                .station(saalsporthalle)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s10)
                .junction(selnauToBinzGiesshuebel)
                .station(binz)
                .station(friesenberg)
                .create();

        Node hbToHardbrueckeWipkingenWiedikon = createJunctionNode(110, 105); // J2

        Node wiedikon = createNode(110, 95, "Wiedikon");
        Node enge = createNode(120, 85, "Enge");
        Node wollishofen = createNode(145, 60, "Wollishofen");

        new MetroMapEdgeBuilder(this)
                .routes(s2, s5, s6, s7, s8, s9, s12, s24)
                .station(hb)
                .junction(hbToHardbrueckeWipkingenWiedikon)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s2, s8)
                .junction(hbToHardbrueckeWipkingenWiedikon)
                .bend(105, 100) // B2
                .station(wiedikon)
                .crossing(uetlibergTrainCrossing)
                .station(enge)
                .station(wollishofen)
                .create();

        Node hardbruecke = createNode(100, 105, "Hardbrücke");
        Node altstetten = createNode(75, 105, "Altstetten");
        Node hardbrueckeToOerlikonAltstetten = createJunctionNode(95, 105); // J3
        Node oerlikonToWipkingenHardbruecke = createJunctionNode(105, 135); // J4
        Node oerlikonToHbWipkingenHardbruecke = createJunctionNode(105, 140); // J5
        Node oerlikon = createNode(105, 145, "Oerlikon");
        Node wipkingen = createNode(105, 110, "Wipkingen");

        new MetroMapEdgeBuilder(this)
                .routes(s5, s6, s7, s9, s12)
                .junction(hbToHardbrueckeWipkingenWiedikon)
                .station(hardbruecke)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s5, s6, s7, s9, s12)
                .station(hardbruecke)
                .junction(hardbrueckeToOerlikonAltstetten)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s6, s7, s9)
                .junction(hardbrueckeToOerlikonAltstetten)
                .bend(90, 110) // B3
                .bend(90, 120) // B4
                .junction(oerlikonToWipkingenHardbruecke)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s6, s7, s9, s24)
                .junction(oerlikonToWipkingenHardbruecke)
                .junction(oerlikonToHbWipkingenHardbruecke)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s2, s6, s7, s8, s9, s24)
                .junction(oerlikonToHbWipkingenHardbruecke)
                .station(oerlikon)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s5, s12)
                .junction(hardbrueckeToOerlikonAltstetten)
                .station(altstetten)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s24)
                .junction(hbToHardbrueckeWipkingenWiedikon)
                .station(wipkingen)
                .junction(oerlikonToWipkingenHardbruecke)
                .create();

        Node stadelhofenToHb = createJunctionNode(125, 105); // J6
        Node stadelhofen = createNode(135, 100, "Stadelhofen");
        Node stadelhofenToTiefenbrunnenStettbach = createJunctionNode(140, 95); // J7
        Node tiefenbrunnen = createNode(160, 75, "Tiefenbrunnen");

        new MetroMapEdgeBuilder(this)
                .routes(s2, s5, s7, s8, s9, s12)
                .station(hb)
                .junction(stadelhofenToHb)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s2, s8)
                .junction(stadelhofenToHb)
                .bend(130, 110) // B5
                .bend(130, 125) // B6
                .bend(120, 135) // B7
                .bend(110, 135) // B8
                .junction(oerlikonToHbWipkingenHardbruecke)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s5, s7, s9, s12)
                .junction(stadelhofenToHb)
                .bend(130, 105) // B9
                .station(stadelhofen)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s7)
                .station(stadelhofen)
                .junction(stadelhofenToTiefenbrunnenStettbach)
                .station(tiefenbrunnen)
                .create();

        Node oerlikonToWallisellen = createJunctionNode(105,150); // J8

        new MetroMapEdgeBuilder(this)
                .routes(s2, s8, s7, s9, s14)
                .station(oerlikon)
                .junction(oerlikonToWallisellen)
                .create();

        Node wallisellen = createNode(140, 165, "Wallisellen");
        Node crossJunction = createJunctionNode(160,165); // J9

        new MetroMapEdgeBuilder(this)
                .routes(s8, s14)
                .junction(oerlikonToWallisellen)
                .bend(110, 155) // B10
                .bend(130, 155) // B11
                .station(wallisellen)
                .junction(crossJunction)
                .create();

        Node stettbach = createNode(160, 150, "Stettbach");

        new MetroMapEdgeBuilder(this)
                .routes(s12)
                .junction(crossJunction)
                .station(stettbach)
                .bend(160, 115) // B12
                .junction(stadelhofenToTiefenbrunnenStettbach)
                .create();

        Node dietlikon = createNode(170, 190, "Dietlikon");

        new MetroMapEdgeBuilder(this)
                .routes(s8, s12)
                .junction(crossJunction)
                .bend(160, 180) // B13
                .station(dietlikon)
                .create();

        Node duebendorf = createNode(205, 160, "Dübendorf");

        new MetroMapEdgeBuilder(this)
                .routes(s14)
                .junction(crossJunction)
                .bend(200, 165) // B14
                .station(duebendorf)
                .create();

//        getNodes().forEach(node -> node.updateY(node.getY() * -1));

    }

    public Node createNode(double x, double y, @Nullable String name) {
        return createNode(x, y, name, RectangleStationSignature::new);
    }

}
