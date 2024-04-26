package pcd.ass01.simtrafficbase;

import java.util.ArrayList;
import java.util.List;

import pcd.ass01.simengineconcur.Barrier;
import pcd.ass01.simengineseq.AbstractEnvironment;
import pcd.ass01.simengineseq.AbstractSimulation;

/**
 * 
 * Thread managing the agents assigned to it
 * 
 */
public class AgentsThread extends Thread {

  private final Barrier actBarrier;   // Barrier before doing an action.
  private final Barrier stepBarrier;  // Barrier before doing next step.
  private final List<CarAgent> carAgents;
  private final AbstractSimulation simulation;
  private final int dt;

  public AgentsThread(Barrier barrier, Barrier eventBarrier, int dt, AbstractSimulation simulation){
    super();
    this.actBarrier = barrier;
    this.stepBarrier = eventBarrier;
    this.carAgents = new ArrayList<>();
    this.simulation = simulation;
    this.dt = dt;
  }

  public void addCar(CarAgent carAgent) {
    carAgents.add(carAgent);
  }

  public void initCars(AbstractEnvironment env) {
    for (var a: carAgents) {
      a.init(env);
    }
  }

  /**
   * Perform a step of the simulation using barriers to synchronize the agents
   */
  public void step() {
    actBarrier.waitBefore(simulation);
    this.carAgents.forEach(car -> car.senseAndDecide(this.dt));
    actBarrier.waitBefore(simulation);
    this.carAgents.forEach(car -> car.act());
  }

  /**
   * Main loop of the thread
   */
  public void run() {
    while(true) {
      stepBarrier.waitBefore(simulation);
      this.step();
    }
  }
}

