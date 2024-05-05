package pcd.ass01.simtrafficbase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import pcd.ass01.simengineconcur.Barrier;
import pcd.ass01.simengineseq.AbstractEnvironment;
import pcd.ass01.simengineseq.AbstractSimulation;

/**
 * 
 * Thread managing the agents assigned to it
 * 
 */
public class AgentsThread implements Callable<Boolean> {

  private final Barrier actBarrier;   // Barrier before doing an action.
  private final Barrier stepBarrier;  // Barrier before doing next step.
  private final List<CarAgent> carAgents;
  private final AbstractSimulation simulation;
  private final int dt;

  public AgentsThread(Barrier actBarrier, Barrier eventBarrier, int dt, AbstractSimulation simulation, AbstractEnvironment env){
    super();
    this.actBarrier = actBarrier;
    this.stepBarrier = eventBarrier;
    this.carAgents = new ArrayList<>();
    this.simulation = simulation;
    this.dt = dt;

    for (var a: carAgents) {
      a.init(env);
    }
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
    this.carAgents.forEach(CarAgent::act);
  }

  /**
   * Main loop of the thread
   */
  public Boolean call() {
    try {
      while (true) {
        stepBarrier.waitBefore(simulation);
        this.step();
      }
    } catch (Exception e) {
      System.out.println("EFROROROR");
    }
      return null;
  }
}

