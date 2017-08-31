package graphtea.extensions;

import java.util.ArrayList;


/**
 * Written by Farshad Toosi (farshad.toosi@lero.ie)
 */
public class edgeCrossing {

	double [][] poss;
	int [][] edge;
	int v;
	ArrayList <int []> crossings;
	boolean done;
	public edgeCrossing(double [][] pos, int [][] edge ) {
		this.poss = pos;
		this.edge = edge;
		this.v = pos[0].length;
		this.done = false;
	}
	
		
	public ArrayList <int []> getCrossedEdges() {
		
		if(!this.done)
			number_of_edge_crossing();
		
		return crossings;
	}
	
	
	public int number_of_edge_crossing()
	{
		crossings = new ArrayList<int []> ();
		int ans=0;
		for(int a=0;a<edge[0].length;a++)
		{
			int i = edge[0][a];
			int j = edge[1][a];
			
					for(int b=a+1;b<edge[0].length;b++)
					{
						int p = edge[0][b];
						int q = edge[1][b];
							if(p!=i && p!=j && q!=i && q!=j)
							{
								double x1=poss[0][i];
								double x2=poss[0][j];
								double x3=poss[0][p];
								double x4=poss[0][q];
								
								double y1=poss[1][i];
								double y2=poss[1][j];
								double y3=poss[1][p];
								double y4=poss[1][q];
								
								double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
								if (d != 0)
								{
									
									double xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
									double yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;
									boolean check1=false;
									boolean check2=false;
									
									if(x1==x2)
									{
										if(y1>y2)
										{
											if(yi>=y2 && y1>=yi)
												check1=true;
										}
										else
											if(y1<y2)
												if(yi<=y2 && y1<=yi)
													check1=true;
									}
									else
										if(x1>x2)
										{
											if(xi>=x2 && x1>=xi)
												check1=true;
										}
										else
											if(x1<x2)
												if(xi<=x2 && x1<=xi)
													check1=true;
									
										
									if(x3==x4)
									{
										if(y3>y4)
										{
											if(yi>=y4 && y3>=yi)
												check2=true;
										}
										else
											if(y3<y4)
												if(yi<=y4 && y3<=yi)
													check2=true;
									}
									else
										if(x3>x4)
										{
											if(xi>=x4 && x3>=xi)
												check2=true;
										}
										else
											if(x3<x4)
												if(xi<=x4 && x3<=xi)
													check2=true;
									
									
									if(check1 && check2)
									{
										ans++;
										int [] edge = new int [4];
										edge[0] = i;
										edge[1] = j;
										edge[2] = p;
										edge[3] = q;
										
										crossings.add(edge);
									}
									
								}
								
							
											
							}
					}
		}
			
		this.done = true;
		return (ans);
	}

	
	
	
	
}
