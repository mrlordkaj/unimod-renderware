/*
 * Copyright (C) 2019 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.unicore.plugin.gta;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.plugin.gta.item.ItemPATHNode;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
class PathNode {
    
    PathSegment segment; // parent
    final ItemPATHNode data; // shared data layer
    final Vector3 position = new Vector3(); // absolute start
    final ArrayList<PathNode> links = new ArrayList();
    final ArrayList<PathLane> leftLanes = new ArrayList();
    final ArrayList<PathLane> rightLanes = new ArrayList();
    
    // for re-create next node index
    short /*index, */nextIndex = -1;
    ArrayList<PathNode> tmpLinks;
    
    PathNode(PathSegment segment, ItemPATHNode data) {
        this.segment = segment;
        this.data = data;
        nextIndex = data.nextId;
    }
    
    void computePosition(Matrix4 transform) {
        position.set(data.position).mul(transform);
    }
    
    boolean computeLanes() {
        return computeLanes(data.numLefts, data.numRights);
    }
    
    boolean computeLanes(int numLefts, int numRights) {
        if (data.type == ItemPATHNode.TYPE_PORT) { // external only
            PathNode next = links.get(0);
            Vector3 head = next.position.cpy().sub(position).nor();
            Vector3 hand = head.cpy().crs(Vector3.Y);
            
            // right lanes are placed in the right,
            // and have same direction with origin
            rightLanes.clear();
            for (int i = 0; i < numRights; i++) {
                float x = (i + 0.5f) * data.laneWidth;
                if (numLefts == 0) {
                    // one-way fix
                    x -= (numRights / 2) * data.laneWidth;
                }
                PathLane lane = new PathLane();
                lane.start = hand.cpy().scl(x).add(position);
                lane.end = head.cpy().scl(4).add(lane.start);
                rightLanes.add(lane);
            }
            
            // left lanes are placed in the left,
            // and have reversed direction with origin
            head = head.cpy().scl(-1);
            leftLanes.clear();
            for (int i = 0; i < numLefts; i++) {
                float x = (i + 0.5f) * data.laneWidth;
                if (numRights == 0) {
                    // one-way fix
                    x -= (numLefts / 2) * data.laneWidth;
                }
                PathLane lane = new PathLane();
                lane.start = hand.cpy().scl(-x).add(position);
                lane.end = head.cpy().scl(4).add(lane.start);
                leftLanes.add(lane);
            }
            return true;
        }
        return false;
    }
    
    boolean isPort() {
        return links.size() == 1;
    }
    
    boolean isTurn() {
        return links.size() == 2;
    }
    
    boolean isCross() {
        return links.size() > 2;
    }
    
    boolean tryMerge(PathNode b) {
        if (    !isPort() || !b.isPort() ||
                segment == b.segment ||
                segment.isCross() || b.segment.isCross() ||
                position.dst(b.position) > 0.8f) {
            return false;
        }
        
        PathNode a2 = segment.getSecondPort(this);
        PathNode b2 = b.segment.getSecondPort(b);
        if (    a2.leftLanes.size() != b2.rightLanes.size() ||
                a2.rightLanes.size() != b2.leftLanes.size()) {
            return false;
        }
        
        // begin merge
        leftLanes.clear();
        rightLanes.clear();
        // takes link of b
        PathNode link = b.links.get(0);
        links.add(link);
        link.links.remove(b);
        link.links.add(this);
        // add all nodes from b's segment
        PathSegment bSeg = b.segment;
        ArrayList<PathNode> moves = bSeg.nodes;
        moves.remove(b);
        while (!moves.isEmpty()) {
            PathNode move = moves.remove(0);
            move.segment = segment;
            segment.nodes.add(move);
        }
        b.segment.destruct();
        return true;
    }
}
