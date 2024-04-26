package pcd.ass01.simengineseq;

import java.util.concurrent.locks.Lock;

/**
 * 
 * Base  class for defining types of agents taking part to the simulation
 * 
 */
public abstract class AbstractAgent {
	
	private String myId;
	private AbstractEnvironment env;
	private int dt;
	
	/**
	 * Each agent has an identifier
	 * 
	 * @param id
	 */
	protected AbstractAgent(String id) {
		this.myId = id;
		this.dt = 0;
	}
	
	/**
	 * This method is called at the beginning of the simulation
	 * 
	 * @param env
	 */
	 public void init(AbstractEnvironment env) {
		this.env = env;
	}

	public  String getAgentId() {
		return myId;
	}
	
	protected AbstractEnvironment getEnv() {
		return this.env;
	}

	public void setDt(int dt) { this.dt = dt; }
	public int getDt() { return this.dt; }
}
