package net.sourceforge.cilib.type.types.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Node<E> {

	private E elem;
	private List<Node<E>> edges;
	
	// Node Constructor
	public Node(E elem){
		this.elem = elem;
		this.edges = new ArrayList<Node<E>>(); 
	}
	
	// graph constructor
	public Node(){
		this.elem = null;
		this.edges = new ArrayList<Node<E>>(); 
	}
	
	// graph constructor
	public Node(Iterable<E> elems){
		this.elem = null;
		this.edges = new ArrayList<Node<E>>();
		this.addGraphNodes(elems);
	}
	
	//--------------------------------------+
	// node methods
	public E getElem() {
		return elem;
	}

	public boolean isGraph(){
		
		return (elem == null);
	}
	
	public List<Node<E>> getEdges(){
		
		return new ArrayList<Node<E>>(edges);
	}
	
	public Node<E> addEdge(E elem){
		
		Node<E> ret = new Node<E>(elem);
		edges.add(ret);
		return ret;
	}
	
	public void addEdge(Node<E> node){
		
		this.edges.add(node);
	}
	
	public void addBidirectionalEdge(Node<E> other){
		
		this.addEdge(other);
		other.addEdge(this);
	}
	
	public void getReachable(Set<Node<E>> reach){
		
		// base case: this already in reach
		if(!reach.contains(this)){
			reach.add(this);
			for (Node<E> node : edges) {
				node.getReachable(reach);
			}
		}
	}
	
	//--------------------------------------+
	// graph methods
	public List<Node<E>> getGraph(){
		
		return new ArrayList<Node<E>>(edges);
	}
	
	public Node<E> addGraphNode(E elem){
		
		Node<E> ret = new Node<E>(elem);
		edges.add(ret);
		return ret;
	}
	
	public void addGraphNodes(Iterable<E> elems){
		
		for (E e : elems) {
			Node<E> ret = new Node<E>(e);
			edges.add(ret);
		}
	}
	
	//--------------------------------------+
}
