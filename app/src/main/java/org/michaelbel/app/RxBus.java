package org.michaelbel.app;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Date: 02 APR 2018
 * Time: 21:52 MSK
 *
 * @author Michael Bel
 */

public class RxBus {

    public RxBus() {}

    private PublishSubject<Object> publishSubject = PublishSubject.create();

    public void send(Object object) {
        publishSubject.onNext(object);
    }

    public Observable <Object> toObservable() {
        return publishSubject;
    }
}