import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class ICmodel {

	private SocialNet_IC icNet = new SocialNet_IC();
	private HashSet<Integer> seeds = new HashSet<Integer>();
	
	
	public ICmodel()
	{
		
	}
	public ICmodel(String network, String prop, boolean isUnidirectional)
	{
		if(!isUnidirectional)
		{
			this.setNetwork(network); //bidirectional
			this.setWeight(prop);
		}
		else
			this.setUniDirectionalNetwork(network, prop); //unidirectional
	}
	public void setNetworkWeight(String pathNetwork, String pathProp) // Too slow
	{
		if(this.icNet.size()!=0)
			System.out.println("Network is already setting");
		int from, to;
		FileReader FileStream, FileStream2;
		try {
			FileStream = new FileReader(pathNetwork);
			BufferedReader BufferedStream = new BufferedReader(FileStream); //read network
			FileStream2 = new FileReader(pathProp);
			BufferedReader BufferedStream2 = new BufferedReader(FileStream2); //read probability in ic model
			
			try {
				do{
					
					String readline = BufferedStream.readLine();
					String[] readlines = readline.split("\t"); // format: ID\tID
					
					from = Integer.parseInt(readlines[0]);
					to = Integer.parseInt(readlines[1]);
					
					this.icNet.add(from);
					this.icNet.add(to);

					//********************
					
					
					double inW = Double.parseDouble(BufferedStream2.readLine());

					double outW = Double.parseDouble(BufferedStream2.readLine());
					
					this.icNet.connect(from, to, inW, outW);
					//this.icNet.connect(from, to);
					//this.icNet.connect1direct(from, to);
				}
				while(BufferedStream.ready());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Setting finish");
	}
	public void setNetwork(String pathNetwork)
	{
		if(this.icNet.size()!=0)
			System.out.println("Network is already setting");
		int from, to;
		FileReader FileStream;
		try {
			FileStream = new FileReader(pathNetwork);
			BufferedReader BufferedStream = new BufferedReader(FileStream); //read network
			
			try {
				do{
					
					String readline = BufferedStream.readLine();
					String[] readlines = readline.split("\t"); // format: ID\tID
					
					from = Integer.parseInt(readlines[0]);
					to = Integer.parseInt(readlines[1]);
					
					this.icNet.add(from);
					this.icNet.add(to);

					//********************
					
					this.icNet.connect(from, to);
				}
				while(BufferedStream.ready());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Setting Edge finish");
	}
	public void setWeight(String path)
	{
		this.icNet.setAllWeight(path);
		System.out.println("Setting Weight finish");
	}
	public void setUniDirectionalNetwork(String pathNetwork, String pathProp)
	{
		if(this.icNet.size()!=0)
			System.out.println("Network is already setting");
		int from, to;
		FileReader FileStream, FileStream2;
		try {
			FileStream = new FileReader(pathNetwork);
			BufferedReader BufferedStream = new BufferedReader(FileStream); //read network
			FileStream2 = new FileReader(pathProp);
			BufferedReader BufferedStream2 = new BufferedReader(FileStream2); //read probability in ic model
			try {
				do{
					
					String readline = BufferedStream.readLine();
					String[] readlines = readline.split("\t"); // format: ID\tID
					
					from = Integer.parseInt(readlines[0]);
					to = Integer.parseInt(readlines[1]);
					
					this.icNet.add(from);
					this.icNet.add(to);
					
					//********************
					String readline2 = BufferedStream2.readLine();
					double weight = Double.parseDouble(readline2);
					
					this.icNet.connect1direct(from, to, weight);
				}
				while(BufferedStream.ready());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void setSeeds(ArrayList<Integer> seeds)
	{
		this.seeds.clear();
		HashSet<Integer> set = new HashSet<Integer>(), tempSet = new HashSet<Integer>();
		set.addAll(seeds);
		for(SocialNode n : this.icNet.getVertices())
			tempSet.add(n.getID());
		
		set.retainAll(tempSet);
		this.seeds.addAll(set);
	}
	
	
	public int propagateProcess()
	{
		// set initial active node
		for(int seed : this.seeds)
			this.icNet.setActive(seed);
		
		int propagationTimes = 0;
		
		HashSet<Integer> previousActive = new HashSet<Integer>();
		previousActive.addAll(this.seeds);
		HashSet<Integer> currentActive = new HashSet<Integer>();
		
		//processing
		do{
			currentActive.clear();
			for(int preNode : previousActive)
			{
				for(SocialEdge e : this.icNet.getEdges(preNode)) //influence neighbors
					if(!this.icNet.isActive(e.b.getID()) && e.getweight() > Math.random()) // if not active and the random number greater than probability
					{
						//System.out.println("\nrand number:" + Math.random());
						currentActive.add(e.b.getID());
						this.icNet.setActive(e.b.getID());
					}
			}
			
			previousActive.clear();
			previousActive.addAll(currentActive);
			propagationTimes++;
			
		} while (currentActive.size() != 0);
		
		return propagationTimes;
	}
	
	public int getActivedNum()
	{
		return this.icNet.activeNodeSize();
	}
	public void clearAct()
	{
		this.icNet.clearAllStatus();
	}
	
	public int blockingProcess(ArrayList<Integer> nodes)
	{
		for(int id : nodes)
			this.icNet.setActive(id);
		this.propagateProcess();
		return this.getActivedNum() - nodes.size();
	}
	public void testnetwork()
	{
		System.out.println("vertice size: "+this.icNet.size());
		this.icNet.printSubE2E(40);
	}
	public ArrayList<SocialNode> getNode()
	{
		return this.icNet.getVertices();
	}
	
	
	public static void main(String[] args) {
		ICmodel ic = new ICmodel();
		
		ic.setNetwork("com-dblp.ungraph.txt");
		ic.setWeight("C:/Users/mosu/SocNetExperiment/prop_dblp_8020");
		
		//ic.testnetwork();
		System.out.println(ic.icNet.getEdges(87619));
		
		ArrayList<Integer> s = new ArrayList<Integer>();
		/*s.add(414);
		s.add(57655);
		s.add(66243);
		s.add(5828);
		s.add(356896);*/
		s.add(57655);
		s.add(1127);
		s.add(481);
		s.add(66243);
		s.add(102529);
		ic.setSeeds(s);
		
		
		System.out.println("\nPropagation times: "+ ic.propagateProcess() + "\nActive number " +ic.getActivedNum());
		
	}

}
