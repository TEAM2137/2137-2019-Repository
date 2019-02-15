package org.torc.robot2019.vision;

import java.awt.Dimension;
import java.awt.Point;

public class HatchTarget {
    public Point center;
    public Dimension size;
    
    public HatchTarget() {
        center = new Point(0, 0);
        size = new Dimension(0, 0);
    }
    public HatchTarget(Point _center, Dimension _size) {
        center = _center;
        size = _size;
    }
}