import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

class SocialNode implements Comparable<SocialNode>{
	private int ID;
	private boolean status = false;
	private void setID(int id){
		this.ID = id;
	}
	public void setStatus(boolean boo){
		this.status = boo;
	}
	public SocialNode(int id){
		setID(id);
	}
	public boolean getStatus()
	{
		return this.status;
	}
	public int getID()
	{
		return this.ID;
	}
	@Override
	public String toString()
	{
		return String.format("%d", ID);
	}
	@Override
	public int compareTo(SocialNode other)
	{
		if(this.ID-other.ID>0)
			return 1;
		else if(this.ID-other.ID==0)
			return 0;
		else
			return -1;
	}
	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 47*hash + Objects.hashCode(this.ID);
		return hash;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) 
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SocialNode other = (SocialNode) obj;
		if (!Objects.equals(this.ID, other.ID))
			return false;
		
		return true;
	}
}
class SocialEdge implements Comparable<SocialEdge>{
	SocialNode a, b;
	double weight = -1.0;
	
	public SocialEdge(SocialNode from, SocialNode to)
	{
		this.a = from;
		this.b = to;
	}
	public SocialEdge(int fromID, int toID)
	{
		SocialNode from = new SocialNode(fromID);
		SocialNode to = new SocialNode(toID);
		this.a = from;
		this.b = to;
	}
	public SocialEdge(SocialNode from, SocialNode to, double wt)
	{
		this.a = from;
		this.b = to;
		this.weight = wt;
	}
	public SocialEdge(int fromID, int toID, double wt)
	{
		SocialNode from = new SocialNode(fromID);
		SocialNode to = new SocialNode(toID);
		this.a = from;
		this.b = to;
		this.weight = wt;
	}
	
	public boolean isWeighted()
	{
		if(this.weight == -1.0)
			return false;
		return true;
	}
	
	public double getweight()
	{
		if(this.isWeighted())
			return this.weight;
		else
		{
			System.out.println("Edge: ("+this.a+this.b+") is unweighted");
			return 0.0;
		}
	}
	public void setweight(double wt)
	{
		this.weight = wt;
	}
	public boolean isStartFrom(int vertexID)
	{
		if(this.a.getID() == vertexID)
			return true;
		else
			return false;
	}
	public boolean isEndTo(int vertexID)
	{
		if(this.b.getID() == vertexID)
			return true;
		else
			return false;
	}
	@Override
	public String toString()
	{
		return String.format("(%S, %S)", this.a, this.b);
	}
	@Override
	public int compareTo(SocialEdge other)
	{
		if(this.a.equals(other.a) && this.b.equals(other.b) && this.weight == other.weight)
			return 0;
		else
			return 1;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) 
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SocialEdge other = (SocialEdge) obj;
		if (!Objects.equals(this.a, other.a) || !Objects.equals(this.b, other.b))
			return false;
		
		return true;
	}
}

class SocialNet_IC{
	
	private HashSet<SocialNode> vertices = new HashSet<SocialNode>();
	private Hashtable<Integer, ArrayList<SocialEdge>> edges = new Hashtable<Integer, ArrayList<SocialEdge>>();
	private HashSet<Integer> activedNodes = new HashSet<Integer>();
	
	
	public void connect(int fromID, int toID)
	{
		SocialNode from = new SocialNode(fromID), to = new SocialNode(toID);
		this.vertices.add(from);
		this.vertices.add(to);
		
		SocialEdge newEdge = new SocialEdge(from, to);
		SocialEdge invEdge = new SocialEdge(to, from);
		if(!this.edges.containsKey(fromID)) // if no edge fromID -> something
		{
			ArrayList<SocialEdge> edgeList = new ArrayList<SocialEdge>();
			edgeList.add(newEdge);
			this.edges.put(fromID ,edgeList);
		}
		else if(!this.edges.get(fromID).contains(newEdge))
		{
			this.edges.get(fromID).add(newEdge);
		}
		
		if(!this.edges.containsKey(toID))
		{
			ArrayList<SocialEdge> edgeList = new ArrayList<SocialEdge>();
			edgeList.add(invEdge);
			this.edges.put(toID ,edgeList);
		}
		else if(!this.edges.get(toID).contains(invEdge))
		{
			this.edges.get(toID).add(invEdge);
		}
	}
	public void connect(int fromID, int toID, double inWT, double outWT)
	{
		SocialNode from = new SocialNode(fromID), to = new SocialNode(toID);
		this.vertices.add(from);
		this.vertices.add(to);
		SocialEdge newEdge = new SocialEdge(from, to, inWT);
		SocialEdge invEdge = new SocialEdge(to, from, outWT);
		if(!this.edges.containsKey(fromID)) // if no edge fromID -> something
		{
			ArrayList<SocialEdge> edgeList = new ArrayList<SocialEdge>();
			edgeList.add(newEdge);
			this.edges.put(fromID ,edgeList);
		}
		else if(!this.edges.get(fromID).contains(newEdge))
		{
			this.edges.get(fromID).add(newEdge);
		}
		
		if(!this.edges.containsKey(toID))
		{
			ArrayList<SocialEdge> edgeList = new ArrayList<SocialEdge>();
			edgeList.add(invEdge);
			this.edges.put(toID ,edgeList);
		}
		else if(!this.edges.get(toID).contains(invEdge))
		{
			this.edges.get(toID).add(invEdge);
		}
	}
	public void connect1direct(int fromID, int toID)
	{
		SocialNode from = new SocialNode(fromID), to = new SocialNode(toID);
		this.vertices.add(from);
		this.vertices.add(to);
		SocialEdge newEdge = new SocialEdge(from, to);
		
		if(!this.edges.containsKey(fromID)) // if no edge fromID -> something
		{
			ArrayList<SocialEdge> edgeList = new ArrayList<SocialEdge>();
			edgeList.add(newEdge);
			this.edges.put(fromID ,edgeList);
		}
		else if(!this.edges.get(fromID).contains(newEdge))
		{
			this.edges.get(fromID).add(newEdge);
		}
		
	}
	public void connect1direct(int fromID, int toID, double WT)
	{
		SocialNode from = new SocialNode(fromID), to = new SocialNode(toID);
		this.vertices.add(from);
		this.vertices.add(to);
		SocialEdge newEdge = new SocialEdge(from, to, WT);
		
		if(!this.edges.containsKey(fromID)) // if no edge fromID -> something
		{
			ArrayList<SocialEdge> edgeList = new ArrayList<SocialEdge>();
			edgeList.add(newEdge);
			this.edges.put(fromID ,edgeList);
		}
		else if(!this.edges.get(fromID).contains(newEdge))
		{
			this.edges.get(fromID).add(newEdge);
		}
	}
	public void setAllWeight(String weightPath)
	{
		FileReader FileStream; 
		try {
			FileStream = new FileReader(weightPath);
			BufferedReader BufferedStream = new BufferedReader(FileStream); //read probability in ic model
			try {
				for(ArrayList<SocialEdge> se : this.edges.values())
					for(int i = 0; i < se.size(); i++)
					{
						if(!se.get(i).isWeighted() && BufferedStream.ready() )
							se.get(i).setweight(Double.parseDouble(BufferedStream.readLine()));
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean add(int vertexID)
	{
		SocialNode v = new SocialNode(vertexID);
		if(this.vertices.contains(v))
			return false;
		else
		{
			this.vertices.add(v);
			return true;
		}
	}
	public boolean contains(int vertexID)
	{
		SocialNode v = new SocialNode(vertexID);
		if(this.vertices.contains(v))
			return true;
		return false;
	}
	public boolean containsEdge(int from, int to)
	{
		if(this.edges.containsKey(from))
			if(this.edges.get(from).contains(new SocialEdge(new SocialNode(from), new SocialNode(to))))
				return true;
		return false;
	}
	public boolean remove(int vertexID) // not finish
	{
		SocialNode v = new SocialNode(vertexID);
		if(this.vertices.contains(v))
		{	
			this.vertices.remove(vertexID);
			return true;
		}
		return false ;
	}
	public void clear()
	{
		this.edges.clear();
		this.vertices.clear();
	}
	public List<SocialNode> getInwardEdges(int vertexID)
	{
		List<SocialNode> list = new ArrayList<SocialNode>() ;
		for(SocialEdge e : this.edges.get(vertexID))
			list.add(e.b);
		return list;
	}
	public ArrayList<SocialEdge> getEdges(int vertexID)
	{
		return this.edges.get(vertexID);
	}
	public void printVertices()
	{
		for(SocialNode v : this.vertices)
			System.out.print(v+", ");
	}
	public void printEdges()
	{
		for(Entry<Integer, ArrayList<SocialEdge>> e : this.edges.entrySet())
			for(SocialEdge edge : e.getValue())
				System.out.print(edge+", ");
	}
	public void printE2E()
	{
		for(Entry<Integer, ArrayList<SocialEdge>> e : this.edges.entrySet())
			for(SocialEdge edge : e.getValue())
				System.out.print(edge+":"+edge.weight+", ");
	}
	public void printSubE2E(int lines)
	{
		int count = 0;
		for(Entry<Integer, ArrayList<SocialEdge>> e : this.edges.entrySet())
		{
			for(SocialEdge edge : e.getValue())
			{
				count++;
				System.out.print(edge+":"+edge.weight+", ");
			}
			if(count >= lines)
				break;
		}
	}
	public int activeNodeSize() // return # active nodes
	{
		return this.activedNodes.size();
	}
	public int size()
	{
		return this.vertices.size();
	}
	public int edge_size()
	{
		return this.edges.size();
	}
	public void clearAllStatus() // clear all status
	{
		this.activedNodes.clear();
	}
	public void setActive(int id)
	{
		this.activedNodes.add(id);
	}
	public boolean isActive(int id)
	{
		if(this.activedNodes.contains(id))
			return true;
		return false;
	}
	public ArrayList<SocialNode> getVertices()
	{
		ArrayList<SocialNode> v = new ArrayList<SocialNode>();
		v.addAll(this.vertices);
		return v;
	}
}
class SocialNet_LT{
	
}
public class Social_Network {

	public static void main(String[] args) {
		
		SocialNet_IC ic = new SocialNet_IC();
		
		ic.add(1);
		ic.add(3);
		ic.add(4);
		ic.add(12);
		ic.add(9);
		ic.connect1direct(5, 12); //single direction
		ic.connect(5, 10); // bi-direction
		ic.connect(5, 15);
		ic.connect(10, 15);
		
		System.out.print("Node: ");
		ic.printVertices();
		System.out.print("\nEdge: ");
		ic.printEdges();
		System.out.println("\n"+ic.getInwardEdges(15));
		System.out.println("Active Size: "+ic.activeNodeSize());
		int from = 12, to = 5;
		System.out.println("Contain edge "+from+"->"+to+" ? "+ ic.containsEdge(from, to));
	}

}
