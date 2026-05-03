package backend.academy.linktracker.bot.support;

import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;

public class TestStreamObserver<T> implements StreamObserver<T> {

    private final List<T> values = new ArrayList<>();
    private Throwable error;
    private boolean completed;

    @Override
    public void onNext(T value) {
        values.add(value);
    }

    @Override
    public void onError(Throwable t) {
        this.error = t;
    }

    @Override
    public void onCompleted() {
        this.completed = true;
    }

    public List<T> values() {
        return values;
    }

    public Throwable error() {
        return error;
    }

    public boolean completed() {
        return completed;
    }
}
