/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.tramaps.graph;

import java.util.Objects;

public class NodeLabel {

    private String name;

    public NodeLabel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof NodeLabel)) {
            return false;
        }

        NodeLabel label = (NodeLabel)obj;
        return Objects.equals(getName(), label.getName());

    }

}
