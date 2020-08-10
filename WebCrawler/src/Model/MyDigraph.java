package Model;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidEdgeException;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a Digraph that adheres to the {@link Digraph} interface.
 * <br>
 * Does not allow duplicates of stored elements through <b>equals</b> criteria.
 * <br>
 * @param <V> Type of element stored at a vertex
 * @param <E> Type of element stored at an edge
 * 
 * @author BRKsCosta
 */
public class MyDigraph<V, E> implements Digraph<V, E> {

    private Map<V, Vertex<V>> vertices;
    private Map<E, Edge<E, V>> edges;

    public MyDigraph() {
        this.vertices = new HashMap<>();
        this.edges = new HashMap<>();
    }

    /**
     * Return all edges stored in a {@param inbound} vertex.
     * @param inbound The inbound vertex
     * @return A collection of incident edges of this vertex
     * @throws InvalidVertexException 
     */
    @Override
    public synchronized Collection<Edge<E, V>> incidentEdges(Vertex<V> inbound) throws InvalidVertexException {
        checkVertex(inbound);

        List<Edge<E, V>> incidentEdges = new ArrayList<>();
        for (Edge<E, V> edge : edges.values()) {

            if (((MyEdge) edge).getInbound() == inbound) {
                incidentEdges.add(edge);
            }
        }
        return incidentEdges;
    }
    
    /**
     * Return all outbound edges stored on vertex
     * @param outbound The outbound edge
     * @return A collection of edges
     * @throws InvalidVertexException 
     */
    @Override
    public synchronized Collection<Edge<E, V>> outboundEdges(Vertex<V> outbound) throws InvalidVertexException {
        checkVertex(outbound);

        List<Edge<E, V>> outboundEdges = new ArrayList<>();
        for (Edge<E, V> edge : edges.values()) {

            if (((MyEdge) edge).getOutbound() == outbound) {
                outboundEdges.add(edge);
            }
        }
        return outboundEdges;
    }
    
    /**
     * Given tow vertex this method return if are adjacent. If it share the same edge
     * @param outbound The outbound vertex
     * @param inbound The inbound vertex
     * @return True or false
     * @throws InvalidVertexException 
     */
    @Override
    public boolean areAdjacent(Vertex<V> outbound, Vertex<V> inbound) throws InvalidVertexException {
        //we allow loops, so we do not check if outbound == inbound
        checkVertex(outbound);
        checkVertex(inbound);

        /* find and edge that goes outbound ---> inbound */
        for (Edge<E, V> edge : edges.values()) {
            if (((MyEdge) edge).getOutbound() == outbound && ((MyEdge) edge).getInbound() == inbound) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @param outbound The outbound vertex
     * @param inbound The inbound vertex
     * @param edgeElement The edge to insert
     * @return The new edge
     * @throws InvalidVertexException
     * @throws InvalidEdgeException 
     */
    @Override
    public synchronized Edge<E, V> insertEdge(Vertex<V> outbound, Vertex<V> inbound, E edgeElement) throws InvalidVertexException, InvalidEdgeException {
        /*if (existsEdgeWith(edgeElement)) {
            throw new InvalidEdgeException("There's already an edge with this element.");
        }*/

        MyVertex outVertex = checkVertex(outbound);
        MyVertex inVertex = checkVertex(inbound);

        MyEdge newEdge = new MyEdge(edgeElement, outVertex, inVertex);

        edges.put(edgeElement, newEdge);

        return newEdge;
    }
    
    /**
     * Insert a new edge on graph
     * @param outboundElement The outbound vertex
     * @param inboundElement The inbound vertex
     * @param edgeElement The edge
     * @return Return the new edge inserted
     * @throws InvalidVertexException
     * @throws InvalidEdgeException 
     */
    @Override
    public synchronized Edge<E, V> insertEdge(V outboundElement, V inboundElement, E edgeElement) throws InvalidVertexException, InvalidEdgeException {
        /*if (existsEdgeWith(edgeElement)) {
            throw new InvalidEdgeException("There's already an edge with this element.");
        }*/

        if (!existsVertexWith(outboundElement)) {
            throw new InvalidVertexException("No vertex contains " + outboundElement);
        }
        if (!existsVertexWith(inboundElement)) {
            throw new InvalidVertexException("No vertex contains " + inboundElement);
        }

        MyVertex outVertex = vertexOf(outboundElement);
        MyVertex inVertex = vertexOf(inboundElement);

        MyEdge newEdge = new MyEdge(edgeElement, outVertex, inVertex);

        edges.put(edgeElement, newEdge);

        return newEdge;
    }
    
    /**
     * Count all vertex stored on graph
     * @return A integer number
     */
    @Override
    public int numVertices() {
        return vertices.size();
    }
    
    /**
     * Count all edges stored on graph
     * @return A integer number
     */
    @Override
    public int numEdges() {
        return edges.size();
    }
    
    /**
     * Return all vertex
     * @return A collection of vertex
     */
    @Override
    public synchronized Collection<Vertex<V>> vertices() {
        List<Vertex<V>> list = new ArrayList<>();
        vertices.values().forEach((v) -> {
            list.add(v);
        });
        return list;
    }
    
    /**
     * Return all edges
     * @return A collection of edges
     */
    @Override
    public synchronized Collection<Edge<E, V>> edges() {
        List<Edge<E, V>> list = new ArrayList<>();
        edges.values().forEach((e) -> {
            list.add(e);
        });
        return list;
    }
    /**
     * Return the opposite vertex
     * @param v The actual vertex
     * @param e The edge stored on a vertex
     * @return Return the opposite vertex
     * @throws InvalidVertexException
     * @throws InvalidEdgeException 
     */
    @Override
    public synchronized Vertex<V> opposite(Vertex<V> v, Edge<E, V> e) throws InvalidVertexException, InvalidEdgeException {
        checkVertex(v);
        MyEdge edge = checkEdge(e);

        if (!edge.contains(v)) {
            return null; /* this edge does not connect vertex v */
        }

        if (edge.vertices()[0] == v) {
            return edge.vertices()[1];
        } else {
            return edge.vertices()[0];
        }

    }
    /**
     * Inserts a new vertex on graph
     * @param vElement The element to insert
     * @return Return the new vertex to be removed
     * @throws InvalidVertexException 
     */
    @Override
    public synchronized Vertex<V> insertVertex(V vElement) throws InvalidVertexException {
        if (existsVertexWith(vElement)) {
            throw new InvalidVertexException("There's already a vertex with this element.");
        }

        MyVertex newVertex = new MyVertex(vElement);

        vertices.put(vElement, newVertex);

        return newVertex;
    }
    
    /**
     * Return an vertex
     * @param v The vertex to be removed
     * @return Return the vertex to be removed
     * @throws InvalidVertexException 
     */
    @Override
    public synchronized V removeVertex(Vertex<V> v) throws InvalidVertexException {
        checkVertex(v);

        V element = v.element();

        //remove incident edges
        Collection<Edge<E, V>> inOutEdges = incidentEdges(v);
        inOutEdges.addAll(outboundEdges(v));
        
        for (Edge<E, V> edge : inOutEdges) {
            edges.remove(edge.element());
        }

        vertices.remove(v.element());

        return element;
    }
    
    /**
     * Remove an edge
     * @param e The edge to be removed
     * @return Return the element removed
     * @throws InvalidEdgeException 
     */
    @Override
    public synchronized E removeEdge(Edge<E, V> e) throws InvalidEdgeException {
        checkEdge(e);

        E element = e.element();
        edges.remove(e.element());

        return element;
    }
    
     /**
     * Replace the vertex for another
     * @param v The current vertex to be replaced
     * @param newElement The new vertex
     * @return Return the old vertex
     * @throws InvalidEdgeException 
     */
    @Override
    public V replace(Vertex<V> v, V newElement) throws InvalidVertexException {
        if (existsVertexWith(newElement)) {
            throw new InvalidVertexException("There's already a vertex with this element.");
        }

        MyVertex vertex = checkVertex(v);

        V oldElement = vertex.element;
        vertex.element = newElement;

        return oldElement;
    }
    
    /**
     * 
     * @param e The current edge
     * @param newElement The new edge
     * @return Return the old edge
     * @throws InvalidEdgeException 
     */
    @Override
    public E replace(Edge<E, V> e, E newElement) throws InvalidEdgeException {
        if (existsEdgeWith(newElement)) {
            throw new InvalidEdgeException("There's already an edge with this element.");
        }

        MyEdge edge = checkEdge(e);

        E oldElement = edge.element;
        edge.element = newElement;

        return oldElement;
    }
    
    /**
     * Get the value stored on vertex
     * @param vElement The element to be checked
     * @return Value stored on element
     */
    private MyVertex vertexOf(V vElement) {
        for (Vertex<V> v : vertices.values()) {
            if (v.element().equals(vElement)) {
                return (MyVertex) v;
            }
        }
        return null;
    }
    
    /**
     * Check is exist the vertex on graph
     * @param vElement The vertex to be checked
     * @return True or false
     */
    private boolean existsVertexWith(V vElement) {
        return vertices.containsKey(vElement);
    }
    
    /**
     * Check is exist the edge on graph
     * @param edgeElement The edge to be checked
     * @return True or false
     */
    private boolean existsEdgeWith(E edgeElement) {
        return edges.containsKey(edgeElement);
    }
    
    /**
     * Show all vertices and edges elements
     * @return Vertex and edges
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(
                String.format("Graph with %d vertices and %d edges:\n", numVertices(), numEdges())
        );

        sb.append("--- Vertices: \n");
        for (Vertex<V> v : vertices.values()) {
            sb.append("\t").append(v.toString()).append("\n");
        }
        sb.append("\n--- Edges: \n");
        for (Edge<E, V> e : edges.values()) {
            sb.append("\t").append(e.toString()).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * This is a inner class that represents the vertex. It depends of a generic data
     * type V.
     */
    private class MyVertex implements Vertex<V> {
        
        V element;
        
        public MyVertex(V element) {
            this.element = element;
        }
        /**
         * Return the concrete element object
         * @return Return the real representation of the type.
         */
        @Override
        public V element() {
            return this.element;
        }
        
        /**
         * Print the vertex.
         * @return Printed vertex
         */
        @Override
        public String toString() {
            return "Vertex{" + element + '}';
        }
    }
    
    /**
     * This is a inner class that defines the Edge it depends of two generic 
     * data type E represent the edge and V represent the concrete vertex.
     */
    private class MyEdge implements Edge<E, V> {

        E element;
        Vertex<V> vertexOutbound;
        Vertex<V> vertexInbound;

        public MyEdge(E element, Vertex<V> vertexOutbound, Vertex<V> vertexInbound) {
            this.element = element;
            this.vertexOutbound = vertexOutbound;
            this.vertexInbound = vertexInbound;
        }
        
        /**
         * Return the concrete element independent of the type.
         * @return Return the concrete element.
         */
        @Override
        public E element() {
            return this.element;
        }
        
        /**
         * Check if already contains the same object on inbound or outbound vertex
         * @param v The vertex in concrete
         * @return True or false
         */
        public boolean contains(Vertex<V> v) {
            return (vertexOutbound == v || vertexInbound == v);
        }
        
        /**
         * Return an array of vertex stored in the edge
         * @return a Array with two positions <code>[0] Inbound vertex</code>
         * <code>[1] Outbound vertex</code>.
         */
        @Override
        public Vertex<V>[] vertices() {
            Vertex[] vertices = new Vertex[2];
            vertices[0] = vertexOutbound;
            vertices[1] = vertexInbound;

            return vertices;
        }
        
        /**
         * Print the edge
         * @return A formatted string
         */
        @Override
        public String toString() {
            return "Edge{{" + element + "}, vertexOutbound=" + vertexOutbound.toString()
                    + ", vertexInbound=" + vertexInbound.toString() + '}';
        }
        
        /**
         * Get the outbound vertex
         * @return Return the outbound vertex
         */
        public Vertex<V> getOutbound() {
            return vertexOutbound;
        }
        
        /**
         * Get the inbound vertex
         * @return Return the inbound vertex
         */
        public Vertex<V> getInbound() {
            return vertexInbound;
        }
    }

    /**
     * Checks whether a given vertex is valid and belongs to this graph
     *
     * @param v The vertex to be checked
     * @return The vertex passed by parameter
     * @throws InvalidVertexException
     */
    private MyVertex checkVertex(Vertex<V> v) throws InvalidVertexException {
        if(v == null) throw new InvalidVertexException("Null vertex.");
        
        MyVertex vertex;
        try {
            vertex = (MyVertex) v;
        } catch (ClassCastException e) {
            throw new InvalidVertexException("Not a vertex.");
        }

        if (!vertices.containsKey(vertex.element)) {
            throw new InvalidVertexException("Vertex does not belong to this graph.");
        }

        return vertex;
    }
    
    /**
     * Checks whether a given edge is valid and belongs to this graph
     * @param e The edge to be checked
     * @return The edge passed by parameter
     * @throws InvalidEdgeException 
     */
    private MyEdge checkEdge(Edge<E, V> e) throws InvalidEdgeException {
        if(e == null) throw new InvalidEdgeException("Null edge.");
        
        MyEdge edge;
        try {
            edge = (MyEdge) e;
        } catch (ClassCastException ex) {
            throw new InvalidVertexException("Not an adge.");
        }

        if (!edges.containsKey(edge.element)) {
            throw new InvalidEdgeException("Edge does not belong to this graph.");
        }

        return edge;
    }
    
}
