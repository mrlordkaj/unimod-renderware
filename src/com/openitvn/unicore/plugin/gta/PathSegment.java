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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.plugin.gta.item.PATHSegment;
import com.openitvn.unicore.plugin.gta.item.PATHNode;
import com.openitvn.unicore.world.IGeometry;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Thinh Pham
 */
class PathSegment extends IGeometry {
    
    private static final float[] NODE_COLORS = {
        Color.BLACK.toFloatBits(),
        Color.RED.toFloatBits(),
        Color.YELLOW.toFloatBits(),
        Color.GREEN.toFloatBits()
    };
    
    ArrayList<PathNode> nodes = new ArrayList<>();
    private PathNode crossNode; // cached for crossing segment
    private ModelInstance modInst;
    
    PathSegment(PATHSegment path) {
        super("SEG_"+path.modName);
        // build node instances
        for (PATHNode node : path.nodes) {
            nodes.add(new PathNode(this, node));
        }
        // link nodes
        for (PathNode node : nodes) {
            if (node.data.nextId >= 0) {
                PathNode next = nodes.get(node.data.nextId);
                if (!node.links.contains(next))
                    node.links.add(next);
                if (!next.links.contains(node))
                    next.links.add(node);
            }
        }
        // find cross node
        for (PathNode node : nodes) {
            if (node.isCross()) {
                crossNode = node;
                break;
            }
        }
    }
    
    boolean isCross() {
        return crossNode != null;
    }
    
    void compileData() {
        for (PathNode node : nodes) {
            node.computePosition(globalTransform);
        }
        for (PathNode node : nodes) {
            node.computeLanes();
        }
    }
    
    /**
     * Sorts nodes by optimized order. Ready for rebuild or export data.
     */
    void optimizeData() {
        ArrayList<PathNode> sortedNodes = new ArrayList();
        if (crossNode == null) {
            for (PathNode node : nodes) {
                node.tmpLinks = new ArrayList(node.links);
            }
            PathNode curNode = getPorts().get(0);
            short i = 1;
            while (!nodes.isEmpty()) {
                nodes.remove(curNode);
                sortedNodes.add(curNode);
                curNode.nextIndex = i++;
                PathNode nextNode = curNode.tmpLinks.remove(0);
                if (nextNode.isPort()) {
                    sortedNodes.add(nextNode);
                    nextNode.nextIndex = -1;
                    break;
                }
                nextNode.tmpLinks.remove(curNode);
                curNode = nextNode;
            }
        } else {
            // add cross node first
            crossNode.nextIndex = -1;
            sortedNodes.add(crossNode);
            nodes.remove(crossNode);
            // all other ports connect to cross node
            for (PathNode node : nodes) {
                node.nextIndex = 0;
                sortedNodes.add(node);
            }
        }
        nodes = sortedNodes;
    }
    
    /**
     *  Exports data as ASCII format.
     */
    void exportData(PrintStream ps) {
        for (PathNode node : nodes) {
            String strPos = String.format("%d, %f, %f, %f",
                    node.nextIndex,
                    node.position.x * 100,
                    node.position.z * 100,
                    node.position.y * 100);
            if (node.isCross()) {
                ps.printf("3, %s\n", strPos);
            } else if (node.isTurn()) {
                ps.printf("2, %s\n", strPos);
            } else if (node.isPort()) {
                ps.printf("1, %s, %f, %d, %d\n",
                    strPos,
                    node.data.laneWidth * 100,
                    node.rightLanes.size(),
                    node.leftLanes.size());
            }
        }
        ps.println("break");
    }
    
    /**
     * Exports data as binary format.
     */
    void exportData(FileOutputStream os) {
        
    }
    
    void rebuildModel() {
        // cleanup
        if (modInst != null) {
            modInst.model.dispose();
            modInst = null;
        }
        
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        
        // updade nodes
        short i = 0, j = 0, k = 0;
        int numVertices = nodes.size();
        float[] vertexData = new float[numVertices * 4];
        short[] nodeIndexData = new short[numVertices];
        short[] linkIndexData = new short[numVertices * 2]; // max, but not size
        for (PathNode node : nodes) {
            // vertex data
            vertexData[j++] = node.position.x;
            vertexData[j++] = node.position.y + 0.2f; // move up 20cm for easy debug
            vertexData[j++] = node.position.z;
            int type = node.links.size();
            if (type > 3) type = 3;
            vertexData[j++] = NODE_COLORS[type];
            // node index data
            nodeIndexData[i] = i;
            // link index data
            if (node.nextIndex >= 0) {
                linkIndexData[k++] = i;
                linkIndexData[k++] = node.nextIndex;
            }
            i++;
        }
        // node part
        Mesh nodeMesh = new Mesh(true, numVertices, numVertices, VertexAttribute.Position(), VertexAttribute.ColorPacked());
        nodeMesh.setVertices(vertexData);
        nodeMesh.setIndices(nodeIndexData);
        mb.part(null, nodeMesh, GL20.GL_POINTS, new Material());
        // link part
        linkIndexData = Arrays.copyOf(linkIndexData, k);
        Mesh linkMesh = new Mesh(true, numVertices, k, VertexAttribute.Position(), VertexAttribute.ColorPacked());
        linkMesh.setVertices(vertexData);
        linkMesh.setIndices(linkIndexData);
        mb.part(null, linkMesh, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.BLACK)));
        
        // update lanes
        float leftColor = NODE_COLORS[1];
        float rightColor = NODE_COLORS[3];
        vertexData = new float[numVertices * 8 * 4]; // max 8 points, 4 channels, but not real size
        nodeIndexData = new short[numVertices * 8 * 2]; // max, but not real size
        j = 0; k = 0;
        for (PathNode node : nodes) {
            // left direction
            for (PathLane lane : node.leftLanes) {
                vertexData[j++] = lane.start.x;
                vertexData[j++] = lane.start.y + 0.2f;
                vertexData[j++] = lane.start.z;
                vertexData[j++] = leftColor;
                vertexData[j++] = lane.end.x;
                vertexData[j++] = lane.end.y + 0.2f;
                vertexData[j++] = lane.end.z;
                vertexData[j++] = leftColor;
                k++; nodeIndexData[k] = k;
                k++; nodeIndexData[k] = k;
            }
            // right direction
            for (PathLane lane : node.rightLanes) {
                vertexData[j++] = lane.start.x;
                vertexData[j++] = lane.start.y + 0.4f; // up 40cm for easy debug
                vertexData[j++] = lane.start.z;
                vertexData[j++] = rightColor;
                vertexData[j++] = lane.end.x;
                vertexData[j++] = lane.end.y + 0.4f; // up 40cm for easy debug
                vertexData[j++] = lane.end.z;
                vertexData[j++] = rightColor;
                k++; nodeIndexData[k] = k;
                k++; nodeIndexData[k] = k;
            }
        }
        vertexData = Arrays.copyOf(vertexData, j);
        nodeIndexData = Arrays.copyOf(nodeIndexData, k);
        Mesh laneMesh = new Mesh(true, j, k, VertexAttribute.Position(), VertexAttribute.ColorPacked());
        laneMesh.setVertices(vertexData);
        laneMesh.setIndices(nodeIndexData);
        mb.part(null, laneMesh, GL20.GL_LINES, new Material());
        
        // apply to model instance
        modInst = new ModelInstance(mb.end());
    }
    
    @Override
    public void draw(ModelBatch mb, Environment env) {
        if (modInst != null) {
            GL11.glPointSize(8);
            GL11.glLineWidth(1);
            mb.render(modInst);
        }
    }
    
    ArrayList<PathNode> getPorts() {
        ArrayList<PathNode> rs = new ArrayList<>();
        for (PathNode node : nodes) {
            if (node.links.size() == 1)
                rs.add(node);
        }
        return rs;
    }
    
    PathNode getOffensivePort(PathNode a) {
        ArrayList<PathNode> ports = getPorts();
        if (crossNode == null) {
            for (PathNode b : ports) {
                if (b != a)
                    return b;
            }
        } else {
            for (PathNode b : ports) {
                if (b != a) {
                    Vector3 aVec = a.position.cpy().sub(crossNode.position).nor();
                    Vector3 bVec = b.position.cpy().sub(crossNode.position).nor();
                    if (Math.abs(aVec.dot(bVec)) > 0.866f)
                        return b;
                }
            }
            
        }
        return null;
    }
    
    /**
     * Gets the port which not is provided port.
     * Not valid for crossing segments.
     */
    PathNode getSecondPort(PathNode firstPort) {
        for (PathNode node : nodes) {
            if (node != firstPort && node.links.size() == 1)
                return node;
        }
        return null;
    }
    
//    @Override
//    public void draw(ICamera camera) {
//        // points
//        GL11.glPointSize(4);
//        imr.begin(camera.combined, GL20.GL_POINTS);
//        for (PathNode node : nodes) {
//            Vector3 start = node.position;
////            if (node.data.type != PATHNode.TYPE_PORT)
////                continue;
//            switch (node.data.type) {
//                case PATHNode.TYPE_PORT:
//                    imr.color(Color.RED);
//                    break;
//                case PATHNode.TYPE_TURN:
//                    imr.color(Color.YELLOW);
//                    break;
//                default:
//                    imr.color(Color.BLACK);
//                    break;
//            }
//            imr.vertex(start.x, start.y, start.z);
//        }
//        imr.end();
//        // lines
//        GL11.glLineWidth(1);
//        imr.begin(camera.combined, GL20.GL_LINES);
//        for (PathNode node : nodes) {
//            // link between nodes
//            if (node.nextIndex >= 0) {
//                PathNode next = nodes.get(node.nextIndex);
//                WorldHelper.line(node.position, next.position, Color.BLUE, Color.GREEN, imr);
//            }
//            // lane direction
//            for (PathLane lane : node.leftLanes) {
//                WorldHelper.line(lane.start, lane.end, Color.RED, imr);
//            }
//            for (PathLane lane : node.rightLanes) {
//                WorldHelper.line(lane.start, lane.end, Color.GREEN, imr);
//            }
//        }
//        imr.end();
//    }
}
