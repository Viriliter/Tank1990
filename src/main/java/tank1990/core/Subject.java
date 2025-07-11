package tank1990.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    List<Observer> observers;

    public Subject() {
        observers = new ArrayList<>();
    }   

    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    protected void notify(EventType event) {
        for(Observer subscriber: observers) {
            subscriber.eventFilter(event);
        }
    }
}
