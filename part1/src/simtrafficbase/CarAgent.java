package pcd.ass01.simtrafficbase;

import java.util.Optional;
import java.util.concurrent.Callable;

import pcd.ass01.simengineconcur.Barrier;
import pcd.ass01.simengineseq.*;

/**
 * Base class modeling the skeleton of an agent modeling a car in the traffic environment
 */
public abstract class CarAgent extends AbstractAgent implements Callable<Boolean> {

  /* car model */
  protected double maxSpeed;
  protected double currentSpeed;
  protected double acceleration;
  protected double deceleration;

  /* percept and action retrieved and submitted at each step */
  protected CarPercept currentPercept;
  protected Optional<Action> selectedAction;

  private final Barrier actBarrier;   // Barrier before doing an action.
  private final Barrier stepBarrier;  // Barrier before doing next step.
  private final AbstractSimulation simulation;  // Barrier before doing next step.


  public CarAgent(String id, RoadsEnv env, Road road,
                  double initialPos,
                  double acc,
                  double dec,
                  double vmax, Barrier actBarrier, Barrier stepBarrier, AbstractSimulation simulation) {
    super(id);
    this.acceleration = acc;
    this.deceleration = dec;
    this.maxSpeed = vmax;
    this.actBarrier = actBarrier;
    this.stepBarrier = stepBarrier;
    this.simulation = simulation;
    env.registerNewCar(this, road, initialPos);
  }

  /**
   * Sense and decide the action to be taken
   * @param dt
   */
  public void senseAndDecide(int dt) {
    AbstractEnvironment env = this.getEnv();
    currentPercept = (CarPercept) env.getCurrentPercepts(getAgentId());

    /* decide */
    selectedAction = Optional.empty();
    decide(dt);
  }

  /**
   * Perform the selected action
   */
  public void act() {
      selectedAction.ifPresent(action -> this.getEnv().doAction(super.getAgentId(), action));
  }

  /**
   * Base method to define the behaviour strategy of the car
   *
   * @param dt
   */
  protected abstract void decide(int dt);

  public double getCurrentSpeed() {
    return currentSpeed;
  }

  public Boolean call() {
    try {
      while (true) {
        log("Si avvia il ciclo");
        stepBarrier.waitBefore(simulation);
        log("Si sveglia");
        this.step();
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return null;
  }
  protected void log(String msg) {
    System.out.println("[CAR " + this.getAgentId() + "] " + msg);
  }


  public void step() {
    actBarrier.waitBefore(simulation);
    log("Decide");
    this.senseAndDecide(getDt());
    actBarrier.waitBefore(simulation);
    log("Act");
    this.act();
    log("" +this.currentSpeed);
  }
}
