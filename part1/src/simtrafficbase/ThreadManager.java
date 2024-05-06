package pcd.ass01.simtrafficbase;

import pcd.ass01.simengineconcur.Barrier;
import pcd.ass01.simengineconcur.BarrierImpl;
import pcd.ass01.simengineseq.AbstractSimulation;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.concurrent.*;

/**
 * Thread that manages all the threads of the simulation
 */
public class ThreadManager {

    private final int nThreadsPerCars;
    private final int nThreadsPerTrafficLights;

    private RoadsEnv env;
    private List<CarAgent> carAgents;
    private List<TrafficLight> trafficLights;
    private final Barrier stepBarrier;
    private final Barrier actBarrier;
    private final AbstractSimulation sim;
    private int nSteps = 0;
    private long currentWallTime;
    private int nCyclesPerSec = 0;
    private long totalTime = 0;
    private final ExecutorService executor;


    public ThreadManager(int nThreadsPerCars, int nThreadsPerTrafficLights, AbstractSimulation sim, RoadsEnv env) {
        this.nThreadsPerCars = nThreadsPerCars;
        this.nThreadsPerTrafficLights = nThreadsPerTrafficLights;
        this.stepBarrier = new BarrierImpl(nThreadsPerCars + nThreadsPerTrafficLights + 1);
        this.actBarrier = new BarrierImpl(nThreadsPerCars + nThreadsPerTrafficLights);
        this.sim = sim;
        this.carAgents = new LinkedList<>();
        this.trafficLights = new LinkedList<>();
        this.env = env;
        executor = Executors.newFixedThreadPool(nThreadsPerCars+nThreadsPerTrafficLights);
    }

    /**
     * Generate the threads for the cars
     *
     * @param carAgents
     * @param dt
     */
    public void generateCars(List<CarAgent> carAgents, int dt) {

        this.carAgents.clear();
        this.carAgents = carAgents;
        var iter = carAgents.iterator(); // Iterator of cars.
//        final int carsPerThread = carAgents.size() / nThreadsPerCars;
//        int remainingCars = carAgents.size() % nThreadsPerCars;

        /*for (int i = 0; i < 1_000; i++) {
            try {

                agentsThreads.add(at);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        /*for (int i = 0; i < nThreadsPerCars; i++) {

            AgentsThread at = new AgentsThread(actBarrier, stepBarrier, dt, sim);

            IntStream.range(0, carsPerThread).forEach(j -> at.addCar(iter.next()));

            if (remainingCars > 0) {
                remainingCars--;
                at.addCar(iter.next());
            }
        }*/
    }

    /**
     * Generate the threads for the traffic lights
     *
     * @param trafficLights
     * @param dt
     */
    public void generateTrafficLight(List<TrafficLight> trafficLights, int dt) {
        this.trafficLights.clear();
        this.trafficLights = trafficLights;
        final var iter = trafficLights.iterator();
//        final int trafficLightsPerThread = trafficLights.size() / this.nThreadsPerTrafficLights;
//        int remainingTrafficLights = trafficLights.size() % this.nThreadsPerTrafficLights;
//
//        for (int i = 0; i < nThreadsPerTrafficLights; i++) {
//
//            TrafficLightsThread tlt = new TrafficLightsThread(actBarrier, stepBarrier, sim, dt);
//            this.trafficLightsThreads.add(tlt);
//
//            IntStream.range(0, trafficLightsPerThread).forEach(j -> tlt.addTrafficLight(iter.next()));
//
//            if (remainingTrafficLights > 0) {
//                remainingTrafficLights--;
//                tlt.addTrafficLight(iter.next());
//            }
//        }
    }
    /**
     * Start the agents' threads
     *
     * @param dt
     */
    public void startThreads(int dt) {
        carAgents.forEach(ca -> {
            ca.init(env, dt);
            executor.submit(ca);
        });

        if (trafficLights != null) {
            trafficLights.forEach(tl -> {
                tl.init();
                executor.submit(tl);
            });
        }

        // Create the thread that will manage the simulation
        new Thread(() -> {
            int actualSteps = 0;
            long startWallTime = System.currentTimeMillis();
            int t = 0;
            long timePerStep = 0;
            long startStepTime = 0;

            while (actualSteps < this.nSteps) {
                System.out.println("MAIN THREAD: Entra nella barrier");
                this.stepBarrier.waitBefore(sim);

                if (startStepTime != 0) {
                    timePerStep += System.currentTimeMillis() - startStepTime;
                }
                t += dt;
                currentWallTime = System.currentTimeMillis();
                this.sim.notifyNewStep(t, env);

                if (nCyclesPerSec > 0) {
                    sim.syncWithWallTime(currentWallTime);
                }
                actualSteps++;
                startStepTime = System.currentTimeMillis();
                System.out.println("STEPS: " + actualSteps);
            }
            this.stepBarrier.waitBefore(sim);
            timePerStep += System.currentTimeMillis() - startStepTime;
            totalTime = System.currentTimeMillis() - startWallTime;
            System.out.println("Finish: " + actualSteps);
            System.out.println("Completed in " + totalTime + "ms");

        }).start();
    }

    public void setSteps(int nSteps) {
        this.nSteps = nSteps;
    }

    public Barrier getStepBarrier() {
        return stepBarrier;
    }

    public Barrier getActBarrier() {
        return actBarrier;
    }

    public void setnCyclesPerSec(int nCyclesPerSec) {
        this.nCyclesPerSec = nCyclesPerSec;
    }

    public long getTotalTime() {
        return totalTime;
    }
}