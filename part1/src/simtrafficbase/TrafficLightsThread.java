package pcd.ass01.simtrafficbase;

import pcd.ass01.simengineconcur.Barrier;
import pcd.ass01.simengineseq.AbstractEnvironment;
import pcd.ass01.simengineseq.AbstractSimulation;

import java.util.LinkedList;
import java.util.List;

/**
 * Thread managing the traffic lights
 */
public class TrafficLightsThread extends Thread {

    private final Barrier actBarrier;
    private final Barrier stepBarrier;
    private final List<TrafficLight> trafficLights;
    private final AbstractSimulation sim;
    private final int dt;


    public TrafficLightsThread(Barrier actBarrier, Barrier stepBarrier, AbstractSimulation sim, int dt) {
        this.actBarrier = actBarrier;
        this.stepBarrier = stepBarrier;
        this.trafficLights = new LinkedList<>();
        this.sim = sim;
        this.dt = dt;
    }

    public void init() {
        this.trafficLights.forEach(TrafficLight::init);
    }

    public void addTrafficLight(TrafficLight tl) {
        this.trafficLights.add(tl);
    }

    public void run() {
        while(true) {
            stepBarrier.waitBefore(sim);
            this.step();
        }
    }

    public void step() {
        this.trafficLights.forEach(tl -> tl.step(this.dt));
        actBarrier.waitBefore(sim);
        actBarrier.waitBefore(sim);
    }
}
