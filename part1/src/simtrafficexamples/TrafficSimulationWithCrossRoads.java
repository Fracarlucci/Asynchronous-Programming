package pcd.ass01.simtrafficexamples;

import java.util.List;
import java.util.Random;

import pcd.ass01.simengineseq.AbstractSimulation;
import pcd.ass01.simtrafficbase.*;
import pcd.ass01.simtrafficbase.TrafficLight.TrafficLightState;

public class TrafficSimulationWithCrossRoads extends AbstractSimulation {

	private final ThreadManager threadManager;
	private final RoadsEnv env;

	public TrafficSimulationWithCrossRoads(int nThreads) {
		super();
		this.env = new RoadsEnv();
		this.threadManager = new ThreadManager(nThreads, 2, this, env);
	}

	public void setup() {

		final int nCyclesPerSec = 25;

		Random  gen = new Random(1);

		this.setupTimings(0, 1);

		this.setupEnvironment(env);

		TrafficLight tl1 = env.createTrafficLight(new P2d(740,300), TrafficLight.TrafficLightState.GREEN, 75, 25, 100);

		Road r1 = env.createRoad(new P2d(0,300), new P2d(1500,300));
		r1.addTrafficLight(tl1, 740);


		CarAgent car1 = new CarAgentExtended("car-1", env, r1, 0, 0.1, 0.3, 6);
		this.addAgent(car1);
		CarAgent car2 = new CarAgentExtended("car-2", env, r1, 100, 0.1, 0.3, 5);
		this.addAgent(car2);

		TrafficLight tl2 = env.createTrafficLight(new P2d(750,290),  TrafficLight.TrafficLightState.RED, 75, 25, 100);

		Road r2 = env.createRoad(new P2d(750,0), new P2d(750,600));
		r2.addTrafficLight(tl2, 290);

		threadManager.generateTrafficLight(List.of(tl1,tl2), 1);

		CarAgent car3 = new CarAgentExtended("car-3", env, r2, 0, 0.1, 0.2, 5);
		this.addAgent(car3);
		CarAgent car4 = new CarAgentExtended("car-4", env, r2, 100, 0.1, 0.1, 4);
		this.addAgent(car4);

		threadManager.generateCars(List.of(car1, car2, car3, car4), 1);
		threadManager.setnCyclesPerSec(nCyclesPerSec);
		this.syncWithTime(25);
	}

	@Override
	public void run(int nSteps) {
		this.threadManager.setSteps(nSteps);
		this.threadManager.startThreads(1);
	}

	public ThreadManager getThreadManager() {
		return this.threadManager;
	}
}
