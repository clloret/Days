package com.clloret.days.domain.utils;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RxImmediateSchedulerRule implements TestRule {

  private Scheduler immediate = new Scheduler() {
    @Override
    public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
      // this prevents StackOverflowErrors when scheduling with a delay
      return super.scheduleDirect(run, 0, unit);
    }

    @Override
    public Worker createWorker() {

      return new ExecutorScheduler.ExecutorWorker(Runnable::run, false);
    }
  };

  @Override
  public Statement apply(final Statement base, Description description) {

    return new Statement() {
      @Override
      public void evaluate() throws Throwable {

        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        //RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);

        try {
          base.evaluate();
        } finally {
          RxJavaPlugins.reset();
          //RxAndroidPlugins.reset();
        }
      }
    };
  }
}