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
import org.jetbrains.annotations.NotNull;

public class MetroMapZuerich extends MetroMap {

    public MetroMapZuerich() {

        super(2, 25, 25);

        Route s2 = new Route("S2", 5, Color.BLACK);
        Route s3 = new Route("S3", 5, Color.BLACK);
        Route s4 = new Route("S4", 5, Color.BLACK);
        Route s5 = new Route("S5", 5, Color.BLACK);
        Route s6 = new Route("S6", 5, Color.BLACK);
        Route s7 = new Route("S7", 5, Color.BLACK);
        Route s8 = new Route("S8", 5, Color.BLACK);
        Route s9 = new Route("S9", 5, Color.BLACK);
        Route s10 = new Route("S10", 5, Color.BLACK);
        Route s11 = new Route("S10", 5, Color.BLACK);
        Route s12 = new Route("S12", 5, Color.BLACK);
        Route s14 = new Route("S14", 5, Color.BLACK);
        Route s15 = new Route("S15", 5, Color.BLACK);
        Route s16 = new Route("S16", 5, Color.BLACK);
        Route s18 = new Route("S18", 5, Color.BLACK);
        Route s19 = new Route("S19", 5, Color.BLACK);
        Route s21 = new Route("S21", 5, Color.BLACK);
        Route s24 = new Route("S24", 5, Color.BLACK);
        Route s25 = new Route("S25", 5, Color.BLACK);
        Route s42 = new Route("S24", 5, Color.BLACK);
        Route sbb = new Route("SBB", 5, Color.BLACK);

        Node hb = createNode("Zürich HB", 105, 120);

        Node uetlibergTrainCrossing = createCrossingNode(115, 90); // C1
        Node selnau = createNode("Selnau", 85, 110);
        Node selnauToBinzGiesshuebel = createJunctionNode(105, 80); // J1
        Node binz = createNode("Binz", 80, 95);
        Node friesenberg = createNode("Friesenberg", 80, 90);
        Node giesshuebel = createNode("Giesshübel", 75, 110);
        Node saalsporthalle = createNode("Saalsporthalle", 70, 110);

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

        Node wiedikon = createNode("Wiedikon", 95, 110);
        Node enge = createNode("Enge", 85, 120);
        Node wollishofen = createNode("Wollishofen", 60, 145);

        new MetroMapEdgeBuilder(this)
                .routes(s2, s3, s5, s6, s7, s8, s9, s11, s12, s15, s16, s21, s24, s42, sbb)
                .station(hb)
                .junction(hbToHardbrueckeWipkingenWiedikon)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s2, s8, s24, s25, sbb)
                .junction(hbToHardbrueckeWipkingenWiedikon)
                .bend(105, 100) // B2
                .station(wiedikon)
                .crossing(uetlibergTrainCrossing)
                .station(enge)
                .station(wollishofen)
                .create();

        Node hardbruecke = createNode("Hardbrücke", 105, 100);
        Node altstetten = createNode("Altstetten", 105, 75);
        Node hardbrueckeToOerlikonAltstetten = createJunctionNode(95, 105); // J3
        Node oerlikonToWipkingenHardbruecke = createJunctionNode(105, 135); // J4
        Node oerlikonToHbWipkingenHardbruecke = createJunctionNode(105, 140); // J5
        Node oerlikon = createNode("Oerlikon", 145, 105);
        Node wipkingen = createNode("Wipkingen", 110, 105);

        new MetroMapEdgeBuilder(this)
                .routes(s2, s3, s5, s6, s7, s8, s9, s11, s12, s15, s16, s21, s42, sbb)
                .junction(hbToHardbrueckeWipkingenWiedikon)
                .station(hardbruecke)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s2, s3, s5, s6, s7, s8, s9, s11, s12, s15, s16, s21, s42, sbb)
                .station(hardbruecke)
                .junction(hardbrueckeToOerlikonAltstetten)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s6, s7, s9, s15, s16, s21)
                .junction(hardbrueckeToOerlikonAltstetten)
                .bend(95, 125)
                .junction(oerlikonToWipkingenHardbruecke)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s6, s7, s9, s15, s16, s21, s24, sbb)
                .junction(oerlikonToWipkingenHardbruecke)
                .junction(oerlikonToHbWipkingenHardbruecke)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s2, s6, s7, s8, s9, s14, s15, s16, s19, s21, s24, sbb)
                .junction(oerlikonToHbWipkingenHardbruecke)
                .station(oerlikon)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s3, s42, s12, sbb, s19, s5, s14)
                .junction(hardbrueckeToOerlikonAltstetten)
                .station(altstetten)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s24, sbb)
                .junction(hbToHardbrueckeWipkingenWiedikon)
                .station(wipkingen)
                .junction(oerlikonToWipkingenHardbruecke)
                .create();

        Node stadelhofenToHb = createJunctionNode(130, 105); // J6
        Node stadelhofen = createNode("Stadelhofen", 100, 135);
        Node stadelhofenToTiefenbrunnenStettbach = createJunctionNode(140, 95); // J7
        Node tiefenbrunnen = createNode("Tiefenbrunnen", 75, 160);

        new MetroMapEdgeBuilder(this)
                .routes(s2, s8, s14, s19, s9, s5, s15, s24, s11, s3, s7, s6, s16, s12)
                .station(hb)
                .junction(stadelhofenToHb)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s2, s8, s14, s19)
                .junction(stadelhofenToHb)
                .bend(130, 115)
                .junction(oerlikonToHbWipkingenHardbruecke)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s9, s5, s15, s24, s11, s3, s7, s6, s16, s12)
                .junction(stadelhofenToHb)
                .station(stadelhofen)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s18, s16, s6, s7, s11, s12, s3, s9, s15, s5)
                .station(stadelhofen)
                .junction(stadelhofenToTiefenbrunnenStettbach)
                .create();

        new MetroMapEdgeBuilder(this)
                .routes(s18, s16, s6, s7)
                .junction(stadelhofenToTiefenbrunnenStettbach)
                .station(tiefenbrunnen)
                .create();

        Node oerlikonToWallisellen = createJunctionNode(105, 150); // J8

        new MetroMapEdgeBuilder(this)
                .routes(s2, s6, s7, s8, s9, s14, s15, s16, s19, s21, s24, sbb)
                .station(oerlikon)
                .junction(oerlikonToWallisellen)
                .create();

        Node wallisellen = createNode("Wallisellen", 165, 140);
        Node crossJunction = createJunctionNode(160, 165); // J9

        new MetroMapEdgeBuilder(this)
                .routes(s8, s14, s19)
                .junction(oerlikonToWallisellen)
                .bend(110, 155) // B10
                .bend(130, 155) // B11
                .station(wallisellen)
                .junction(crossJunction)
                .create();

        Node stettbach = createNode("Stettbach", 150, 160);

        new MetroMapEdgeBuilder(this)
                .routes(s11, s12, s3, s9, s15, s5)
                .junction(crossJunction)
                .station(stettbach)
                .bend(160, 115) // B12
                .junction(stadelhofenToTiefenbrunnenStettbach)
                .create();

        Node dietlikon = createNode("Dietlikon", 190, 170);

        new MetroMapEdgeBuilder(this)
                .routes(s8, s19, s3, s11, s12)
                .junction(crossJunction)
                .bend(160, 180) // B13
                .station(dietlikon)
                .create();

        Node duebendorf = createNode("Dübendorf", 160, 205);

        new MetroMapEdgeBuilder(this)
                .routes(s14, s9, s15, s5)
                .junction(crossJunction)
                .bend(200, 165) // B14
                .station(duebendorf)
                .create();

//        Node schlieren = createNode(40, 105, "Schlieren");
//        Node urdorf = createNode(40, 75, "Urdorf");
//
//        Node altstettenToSchlierenUrdorf = createJunctionNode(70, 105);
//
//        new MetroMapEdgeBuilder(this)
//                .routes(s3, s42, s12, sbb, s19, s5, s14)
//                .station(altstetten)
//                .junction(altstettenToSchlierenUrdorf)
//                .create();
//
//        new MetroMapEdgeBuilder(this)
//                .routes(s5, s14)
//                .junction(altstettenToSchlierenUrdorf)
//                .station(urdorf)
//                .create();
//
//        new MetroMapEdgeBuilder(this)
//                .routes(s3, s42, s12, sbb, s19)
//                .junction(altstettenToSchlierenUrdorf)
//                .station(schlieren)
//                .create();

    }

    /**
     * @return a node for given position and name using a {@link RectangleStationSignature}
     */
    private Node createNode(@NotNull String name, double y, double x) {
        return createNode(x, y, name, RectangleStationSignature::new);
    }

}
