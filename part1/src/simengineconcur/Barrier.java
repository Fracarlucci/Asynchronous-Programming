package pcd.ass01.simengineconcur;

import pcd.ass01.simengineseq.AbstractSimulation;

/**
 * Interface for a barrier to synchronize threads
 *
 */
public interface Barrier {
  
  public void waitBefore(AbstractSimulation isStopped);

  void signalAll();
}
