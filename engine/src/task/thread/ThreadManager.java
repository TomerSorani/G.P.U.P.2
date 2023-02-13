package task.thread;

import data.structure.Target;
import data.structure.TargetsSerialSet;
import javafx.beans.property.SimpleBooleanProperty;
import task.impl.AbstractTaskManager;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class ThreadManager {
    private Map<Target, AbstractTaskManager.RunningState> targetRunningStateMap;
    private LinkedList<Target> sortedTargetList;
    private boolean allDone;
    private final Object lock,pauseLock;
    private ThreadPoolExecutor threadPool;
   // private ExecutorService threadExecutor;
    private SimpleBooleanProperty isPauseProperty;

    public ThreadManager(Map<Target, AbstractTaskManager.RunningState> targetRunningStateMapIn, LinkedList<Target> sortedTargetListIn,
                             Integer maxThreadsAmount,SimpleBooleanProperty pausePropertyIn)
    {
        targetRunningStateMap = targetRunningStateMapIn;
        sortedTargetList = sortedTargetListIn;
        allDone = false;
        lock = new Object();
        pauseLock=new Object();
       // threadExecutor = Executors.newFixedThreadPool(maxThreadsAmount);
        threadPool= new ThreadPoolExecutor(2,maxThreadsAmount,1000,TimeUnit.MINUTES, new LinkedBlockingQueue<>());
         isPauseProperty=new SimpleBooleanProperty(false);
        pausePropertyIn.addListener((obsev)-> {
            if (pausePropertyIn.getValue()) {
                 isPauseProperty.setValue(true);
            }
            else {
                isPauseProperty.setValue(false);
                synchronized (pauseLock) {
                    pauseLock.notifyAll();
                }
            }
        });

    }
    public ThreadPoolExecutor getThreadPool() {return threadPool;}
    public boolean checkIfPaused() {return isPauseProperty.get();}

    synchronized public void handleIfDoneAllTasks() {
        for (Target t : sortedTargetList) {//todo add dont check here if we add it to the running state!
            if (!(targetRunningStateMap.get(t).equals(AbstractTaskManager.RunningState.FINISHED) ||
                    targetRunningStateMap.get(t).equals(AbstractTaskManager.RunningState.SKIPPED)))
                return;
        }
        allDone = true;
        synchronized (lock) {
            lock.notifyAll();
            //threadExecutor.shutdown();
            threadPool.shutdown();
        }
    }
    public boolean allDone() {
        return allDone;
    }

    public Object getLock() {
        return lock;
    }
    public Object getPauseLock() {
        return pauseLock;
    }

    public Boolean isTargetLockedFromSet (Target t){
        Set<TargetsSerialSet> theTargetSets=t.getTheSetsThatTheTargetBelongsTo();
        for(TargetsSerialSet set: theTargetSets){
            if(set.isSetLocked()) {
                return true;
            }
        }
        return false;
    }

    public void lockAllSetsOfTheTarget (Target t){
        for(TargetsSerialSet set: t.getTheSetsThatTheTargetBelongsTo()){
            set.lockSet();
            set.setLockedBy(t.getName());
        }
    }

    public void unlockAllSetsOfTheTarget (Target t){
        for(TargetsSerialSet set: t.getTheSetsThatTheTargetBelongsTo()){
            if(set.getLockedBy().equals(t.getName())){
                set.unlockSet();
            }
        }
    }


}
