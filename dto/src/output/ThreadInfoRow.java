package output;

import javafx.beans.property.SimpleStringProperty;

public class ThreadInfoRow {
    private final SimpleStringProperty absoluteTimestamp,timeFromStartingTheTask,
            amountOfAvailableThreads,amountOfThreadsInTheQueue;

    public ThreadInfoRow(String absoluteTimestampIn,String timeFromStartingTheTaskIn,
                        Integer amountOfAvailableThreadsIn,Integer amountOfThreadsInTheQueueIn)
    {
        absoluteTimestamp=new SimpleStringProperty(absoluteTimestampIn);
        timeFromStartingTheTask=new SimpleStringProperty(timeFromStartingTheTaskIn);
        amountOfAvailableThreads=new SimpleStringProperty (String.valueOf(amountOfAvailableThreadsIn));
        amountOfThreadsInTheQueue=new SimpleStringProperty (String.valueOf(amountOfThreadsInTheQueueIn));
    }
    public SimpleStringProperty absoluteTimestampProperty() {return absoluteTimestamp;}
    public SimpleStringProperty timeFromStartingTheTaskProperty() {return timeFromStartingTheTask;}
    public SimpleStringProperty amountOfAvailableThreadsProperty() {return amountOfAvailableThreads;}
    public SimpleStringProperty amountOfThreadsInTheQueueProperty() {return amountOfThreadsInTheQueue;}
}

