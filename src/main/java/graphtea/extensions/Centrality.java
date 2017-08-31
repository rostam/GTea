package graphtea.extensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.JOptionPane;


import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;


/**
 * Written by Farshad Toosi (farshad.toosi@lero.ie)
 */
public class Centrality {

	private int [][] edge;
	private int v;
	int link;
	
	
	public Centrality (int edge[][]) {
		this.edge = edge;
		this.link = edge[0].length;
	}
	
	
	
	public int getLink() {
		return this.link;
	}
	
	public double [] Betweenness_Centrality(int v)
	{
		Graph g = new UndirectedSparseGraph();
		int lk=0;
		for(int i=0;i<v;i++)
		{	
			g.addVertex(i);
		}
		
		for(int i=0;i<link;i++)
		{
			g.addEdge(i+"",edge[0][i],edge[1][i]);
		}
		
				
		DijkstraShortestPath alg = new DijkstraShortestPath(g);
		
		ArrayList<Integer> [][] shortes  =  (ArrayList<Integer>[][]) new  ArrayList [v][v] ;
		
		for(int i=0;i<v;i++)
		{
			for(int j=0;j<v;j++)
			{
				List k  = alg.getPath(i, j);
				shortes[i][j] = new ArrayList<Integer>();
				if(i!=j)
				{
					for(int q=0;q<k.size();q++)
					{	
						int a1= Integer.parseInt((String) k.get(q));
						shortes [i][j].add(edge[0][a1]);
						shortes [i][j].add(edge[1][a1]);
					}
				}
			}
		}
		
		
		
		for(int i=0;i<v;i++)
			for(int j=0;j<v;j++)
				for(int q=0;q<shortes [i][j].size();q++)
					for(int p=q+1;p<shortes [i][j].size();p++)
						if(shortes [i][j].get(q) == shortes [i][j].get(p))
							shortes [i][j].remove(p);
		
		double centr [] = new double [v];
		double largest_btw_cnt=0;
		double smallest_btw_cnt=0;
		
		for(int i=0;i<v;i++)
			for(int j=i+1;j<v;j++)
				for(int q=0;q<shortes[i][j].size();q++)
					centr[shortes[i][j].get(q)]++;
		
		for(int i=0;i<v;i++)
			if(largest_btw_cnt<centr[i])
				largest_btw_cnt=centr[i];
		
		smallest_btw_cnt = largest_btw_cnt;
		return centr;
		
	}
	
	

	
	
}
