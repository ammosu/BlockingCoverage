import java.util.ArrayList;


public class Greedy {
	public ArrayList<Integer> topK(int k, int MC, String pathNetwork, String pathPropagation)
	{
		ICmodel ic = new ICmodel();
		ic.setNetwork(pathNetwork);
		ic.setWeight(pathPropagation);
		
		
		ArrayList<Integer> topSeeds = new ArrayList<Integer>();
		int maxID = -1;
		double maxValue = 0.0;
		while(topSeeds.size() < k)
		{
			ArrayList<Integer> tempSeeds = new ArrayList<Integer>();

			for(SocialNode sNode : ic.getNode())
			{
				tempSeeds.clear();
				tempSeeds.addAll(topSeeds);
				tempSeeds.add(sNode.getID());
				
				ic.setSeeds(tempSeeds);
				double value = this.MonteCarloSim(MC, ic);
				
				if(value > maxValue)
				{
					maxValue = value;
					maxID = sNode.getID();
				}
			}
			topSeeds.add(maxID);
			System.out.print(".");
		}
		System.out.println("\nMax value:" + maxValue);
		
		return topSeeds;
	}
	
	public double MonteCarloSim(int MC , ICmodel ic)
	{
		double expectedInfluence = 0.0;
		
		for(int i = 0; i < MC; i++)
		{
			ic.propagateProcess();
			expectedInfluence += (double)ic.getActivedNum();
			ic.clearAct();
		}
		
		expectedInfluence /= (double)MC;
		return expectedInfluence;
	}
	public void testMCsim()
	{
		ICmodel ic = new ICmodel();
		ic.setNetwork("C:/Users/mosu/SocNetExperiment/com-dblp.ungraph - small.txt");
		ic.setWeight("C:/Users/mosu/SocNetExperiment/prop_dblp_8020");
		ArrayList<Integer> s = new ArrayList<Integer>();
		s.add(444);
		s.add(399039);
		ic.setSeeds(s);
		System.out.println("\nPropagation times: "+ ic.propagateProcess() + "\nActive number: " +this.MonteCarloSim(10000, ic));
		
	}
	public double MonteCarloSimulateBlocking(ArrayList<Integer> blockingNode, int MC , ICmodel ic)
	{
		double expectedInfluence = 0.0;
		
		for(int i = 0; i < MC; i++)
		{
			expectedInfluence += (double)ic.blockingProcess(blockingNode);
			ic.clearAct();
		}
		
		expectedInfluence /= (double)MC;
		return expectedInfluence;
	}
	public void testMCblockingSim()
	{
		ICmodel ic = new ICmodel();
		ic.setNetwork("C:/Users/mosu/SocNetExperiment/com-dblp.ungraph - small.txt");
		ic.setWeight("C:/Users/mosu/SocNetExperiment/prop_dblp_8020");
		ArrayList<Integer> s = new ArrayList<Integer>(), blocking = new ArrayList<Integer>();
		s.add(87619);
		//s.add(399039);
		ic.setSeeds(s);
		blocking.add(728);
		System.out.println("\nActive number: " +this.MonteCarloSimulateBlocking(blocking, 10000, ic));
		
	}
	
	public ArrayList<Integer> blockingTopk(int k, int MC, String pathNetwork, String pathPropagation, ArrayList<Integer> seed)
	{
		ArrayList<Integer> blockingNode = new ArrayList<Integer>();
		
		ICmodel ic = new ICmodel();
		ic.setNetwork(pathNetwork);
		ic.setWeight(pathPropagation);
		ic.setSeeds(seed);
		
		
		int minID = -1;
		double minValue = Double.MAX_VALUE;
		while(blockingNode.size() < k)
		{
			ArrayList<Integer> blocking = new ArrayList<Integer>();
			for(SocialNode sNode : ic.getNode())
			{
				if(!seed.contains(sNode.getID()))
				{
					blocking.clear();
					blocking.addAll(blockingNode);
					blocking.add(sNode.getID());
					
					double value = this.MonteCarloSimulateBlocking(blocking, MC, ic);
					if( value < minValue)
					{
						minID = sNode.getID();
						minValue = value;
					}
				}
			}
			blockingNode.add(minID);
			System.out.print(".");
		}
		
		System.out.println("\nValue after blocking: " +this.MonteCarloSimulateBlocking(blockingNode, MC, ic));
		
		return blockingNode;
	}
	public ArrayList<Integer> BlockingCoverage(double coverage, int MC, String pathNetwork, String pathPropagation, ArrayList<Integer> seed)
	{
		ArrayList<Integer> blockingNode = new ArrayList<Integer>();
		
		ICmodel ic = new ICmodel();
		ic.setNetwork(pathNetwork);
		ic.setWeight(pathPropagation);
		ic.setSeeds(seed);
		
		
		int minID = -1;
		double minValue = Double.MAX_VALUE;
		while(minValue > coverage)
		{
			ArrayList<Integer> blocking = new ArrayList<Integer>();
			for(SocialNode sNode : ic.getNode())
			{
				if(!seed.contains(sNode.getID()))
				{
					blocking.clear();
					blocking.addAll(blockingNode);
					blocking.add(sNode.getID());
					
					double value = this.MonteCarloSimulateBlocking(blocking, MC, ic);
					if( value < minValue)
					{
						minID = sNode.getID();
						minValue = value;
					}
				}
			}
			blockingNode.add(minID);
			System.out.print(".");
		}
		
		System.out.println("\nValue after blocking: " +this.MonteCarloSimulateBlocking(blockingNode, MC, ic));
		
		return blockingNode;
	}
	
	public static void main(String[] args) {
		Greedy greedy = new Greedy();
		//greedy.testMCblockingSim();
		double startTime, endTime;
		
		ArrayList<Integer> seeds = new ArrayList<Integer>(), blockingNode = new ArrayList<Integer>();
		
		startTime = System.currentTimeMillis();
		//seeds = greedy.topK(1, 1, "com-dblp.ungraph.txt", "C:/Users/mosu/SocNetExperiment/prop_dblp_8020");
		seeds.add(57655);
		seeds.add(1127);
		seeds.add(481);
		seeds.add(66243);
		seeds.add(102529);/**/
		//blockingNode = greedy.blockingTopk(5, 1000, "C:/Users/mosu/SocNetExperiment/com-dblp.ungraph - small.txt", "C:/Users/mosu/SocNetExperiment/prop_dblp_8020", seeds);
		blockingNode = greedy.BlockingCoverage(38000.0, 1, "C:/Users/mosu/SocNetExperiment/com-dblp.ungraph - small.txt", "C:/Users/mosu/SocNetExperiment/prop_dblp_8020", seeds);
		endTime = System.currentTimeMillis();
		
		System.out.println("Seed: " + seeds);
		System.out.println("Blocking Nodes: " + blockingNode);
		System.out.println("Total time :" + (endTime - startTime)/1000);
	}

}
