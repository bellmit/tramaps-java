/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.converter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class StottFile {

    Map<String, StottNode> nodes;
    List<StottEdge> edges;

    private File file;

    public StottFile(@NotNull File file) throws FileNotFoundException {
        this.file = file;
        load();
    }

    public StottFile(@NotNull String file) throws FileNotFoundException {
        this(new File(file));
    }

    private StottNode toNode(String[] data) {
        StottNode node = new StottNode();
        node.setLabel(data[1].trim());
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        try {
            node.setX(format.parse(data[2]).doubleValue());
            node.setY(format.parse(data[3]).doubleValue());
        }
        catch (ParseException e) {
            System.out.println(e);
        }
        return node;
    }

    private StottEdge toEdge(String[] data) {
        StottEdge edge = new StottEdge();
        // edge.setLabel(data[1].trim());
        edge.setLabel(data[3].replace(",", " ->"));
        edge.setColor(data[2]);
        List<StottNode> edgeNodes = Arrays.stream(data[3].split(", "))
                .map(String::trim)
                .map(nodes::get)
                .collect(Collectors.toList());
        edge.setStations(edgeNodes);
        return edge;
    }

    private void load() throws FileNotFoundException {

        nodes = new HashMap<>();
        edges = new ArrayList<>();

        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String[] line = scanner.nextLine().replaceAll("([:](\\w+)[=])", ":").split(":");
            if ("NODE".equals(line[0])) {
                StottNode node = toNode(line);
                nodes.put(node.getLabel(), node);
            }
            else {
                StottEdge edge = toEdge(line);
                edges.add(edge);
            }
        }

    }

    public Set<StottNode> getNodes() {
        return new HashSet<>(nodes.values());
    }

    public Set<StottEdge> getEdges() {
        return new HashSet<>(edges);
    }
}
