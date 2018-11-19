package me.shouheng.commons.event;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private static volatile RxBus rxBus;

    private final Subject<Object> subject = PublishSubject.create().toSerialized();

    private final Map<String, CompositeDisposable> disposableMap = new HashMap<>();

    public static RxBus getRxBus() {
        if (rxBus == null) {
            synchronized (RxBus.class) {
                if(rxBus == null) {
                    rxBus = new RxBus();
                }
            }
        }
        return rxBus;
    }

    public void post(Object o){
        subject.onNext(o);
    }

    public <T> Flowable<T> getObservable(Class<T> type) {
        return subject.toFlowable(BackpressureStrategy.BUFFER).ofType(type);
    }

    public <T extends RxMessage> Flowable<T> getObservable(Class<T> type, int code) {
        return subject.toFlowable(BackpressureStrategy.BUFFER)
                .ofType(type)
                .filter(t -> t.code == code);
    }

    public <T extends RxMessage> Disposable doSubscribe(Class<T> type, int code, Consumer<T> next, Consumer<Throwable> error) {
        return getObservable(type, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        return getObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    public boolean hasObservers() {
        return subject.hasObservers();
    }

    public void addSubscription(Object o, Disposable disposable) {
        String key = String.valueOf(o.hashCode());
        if (disposableMap.get(key) != null) {
            disposableMap.get(key).add(disposable);
        } else {
            CompositeDisposable disposables = new CompositeDisposable();
            disposables.add(disposable);
            disposableMap.put(key, disposables);
        }
    }

    public void unSubscribe(Object o) {
        String key = String.valueOf(o.hashCode());
        if (!disposableMap.containsKey(key)){
            return;
        }
        if (disposableMap.get(key) != null) {
            disposableMap.get(key).dispose();
        }

        disposableMap.remove(key);
    }
}
