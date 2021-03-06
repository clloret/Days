package com.clloret.days.dagger.modules

import com.clloret.days.domain.injection.TypeNamed
import com.clloret.days.domain.utils.ThreadSchedulers
import com.clloret.days.utils.ThreadSchedulersImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class TestThreadingModule {

  @Binds
  abstract fun bindThreadSchedulers(impl: ThreadSchedulersImpl): ThreadSchedulers

  @Module
  companion object {

    @Provides
    @Singleton
    @Named(TypeNamed.EXECUTOR_SCHEDULER)
    fun provideExecutorThread(): Scheduler = Schedulers.trampoline()

    @Provides
    @Singleton
    @Named(TypeNamed.UI_SCHEDULER)
    fun provideUiThread(): Scheduler = Schedulers.trampoline()
  }

}