package pcd.ass02.simengineconcur;

import pcd.ass02.simengineseq.AbstractSimulation;

/**
 * Interface for a barrier to synchronize threads
 *
 */
public interface Barrier {
  
  public void waitBefore(AbstractSimulation isStopped);

  void signalAll();
}
