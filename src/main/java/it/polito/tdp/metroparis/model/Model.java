package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
//import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private List<Fermata> fermate;
	Map<Integer, Fermata> fermateIdMap ;
	
	private Graph<Fermata, DefaultEdge> grafo;
	/* METODO DIJKSTRA PER CAMMINI MINIMI
	private DijkstraShortestPath<Fermata,DefaultEdge> grafo2;
	private GraphPath<Fermata,DefaultEdge> grafo3;
*/
	public List<Fermata> getFermate() {
		if (this.fermate == null) {
			MetroDAO dao = new MetroDAO();
			this.fermate = dao.getAllFermate();
			
			this.fermateIdMap = new HashMap<Integer, Fermata>();
			for (Fermata f : this.fermate)
				this.fermateIdMap.put(f.getIdFermata(), f);

		}
		return this.fermate;
	}
	

	public void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		

//		Graphs.addAllVertices(this.grafo, this.fermate);
		Graphs.addAllVertices(this.grafo, getFermate());
		
		MetroDAO dao = new MetroDAO();

		List<CoppiaId> fermateDaCollegare = dao.getAllFermateConnesse();
		for (CoppiaId coppia : fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()), fermateIdMap.get(coppia.getIdArrivo()));
		}
		/* METODO DIJKSTRA
		this.grafo2 = new DijkstraShortestPath<>(this.grafo);
		*/
//		System.out.println(this.grafo);
//		System.out.println("Vertici = " + this.grafo.vertexSet().size());
//		System.out.println("Archi   = " + this.grafo.edgeSet().size());
	}

	public List<Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo) {
		creaGrafo() ;
		/* METODO DIJKSTRA
		this.grafo3 = this.grafo2.getPath(partenza, arrivo);
		List<Fermata> lista = this.grafo3.getVertexList();
		System.out.println(lista);
		System.out.println(lista.size()); */
		Map<Fermata,Fermata> alberoInverso = visitaGrafo(partenza);
		
		Fermata corrente = arrivo ;
		List<Fermata> percorso = new ArrayList<>() ;	//LinkedList per molti elementi in lista
		
		while(corrente != null) {
			percorso.add(0, corrente);	//aggiunge nell'ordine corretto
			corrente = alberoInverso.get(corrente) ;
		}
		
		return percorso ;
	}

	
	public Map<Fermata,Fermata> visitaGrafo(Fermata partenza) {
		GraphIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);
		
		Map<Fermata,Fermata> alberoInverso = new HashMap<>() ;
		alberoInverso.put(partenza, null) ;
		
		visita.addTraversalListener(new RegistraAlberoDiVisita(alberoInverso, this.grafo));
		while (visita.hasNext()) {
			Fermata f = visita.next();
//			System.out.println(f);
		}
		
		return alberoInverso ;
	}

}
