package pcd.ass01.simtrafficexamples;

import pcd.ass01.simtrafficbase.ThreadManager;

/**
 * 
 * Main class to create and run a simulation
 * 
 */
public class RunTrafficSimulation {

	public static void main(String[] args) {

//	 	var simulation = new TrafficSimulationSingleRoadTwoCars(2);
		var simulation = new TrafficSimulationSingleRoadSeveralCars(30);
//		var simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars(nThreads);
//		var simulation = new TrafficSimulationWithCrossRoads(nThreads);
		simulation.setup();

		RoadSimStatistics stat = new RoadSimStatistics();
		RoadSimView view = new RoadSimView(simulation);
		view.display();
		
		simulation.addSimulationListener(stat);
		simulation.addSimulationListener(view);
	}
}
