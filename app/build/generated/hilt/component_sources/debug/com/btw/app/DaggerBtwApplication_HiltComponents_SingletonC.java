package com.btw.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import com.btw.app.data.local.BtwDatabase;
import com.btw.app.data.local.dao.AlertDao;
import com.btw.app.data.local.dao.HandoffDao;
import com.btw.app.data.local.dao.LocationDao;
import com.btw.app.data.local.dao.RiderDao;
import com.btw.app.data.local.dao.VehicleDao;
import com.btw.app.data.preferences.BtwPreferences;
import com.btw.app.data.repository.AlertRepositoryImpl;
import com.btw.app.data.repository.HandoffRepositoryImpl;
import com.btw.app.data.repository.LocationRepositoryImpl;
import com.btw.app.data.repository.PreferencesRepositoryImpl;
import com.btw.app.data.repository.RiderRepositoryImpl;
import com.btw.app.data.repository.VehicleRepositoryImpl;
import com.btw.app.di.DatabaseModule_ProvideAlertDaoFactory;
import com.btw.app.di.DatabaseModule_ProvideDatabaseFactory;
import com.btw.app.di.DatabaseModule_ProvideHandoffDaoFactory;
import com.btw.app.di.DatabaseModule_ProvideLocationDaoFactory;
import com.btw.app.di.DatabaseModule_ProvidePreferencesFactory;
import com.btw.app.di.DatabaseModule_ProvideRiderDaoFactory;
import com.btw.app.di.DatabaseModule_ProvideVehicleDaoFactory;
import com.btw.app.di.DatabaseModule_ProvideWorkManagerFactory;
import com.btw.app.domain.repository.AlertRepository;
import com.btw.app.domain.repository.HandoffRepository;
import com.btw.app.domain.repository.LocationRepository;
import com.btw.app.domain.repository.PreferencesRepository;
import com.btw.app.domain.repository.RiderRepository;
import com.btw.app.domain.repository.VehicleRepository;
import com.btw.app.domain.usecase.AddRiderUseCase;
import com.btw.app.domain.usecase.CheckExpectedPickupUseCase;
import com.btw.app.domain.usecase.GetAlertHistoryUseCase;
import com.btw.app.domain.usecase.GetLocationsUseCase;
import com.btw.app.domain.usecase.GetNearbyLocationUseCase;
import com.btw.app.domain.usecase.GetRidersUseCase;
import com.btw.app.domain.usecase.GetVehiclesUseCase;
import com.btw.app.domain.usecase.RecordLocationVisitUseCase;
import com.btw.app.service.AlertEscalationWorker;
import com.btw.app.service.AlertEscalationWorker_AssistedFactory;
import com.btw.app.service.BtwMonitorService;
import com.btw.app.service.BtwMonitorService_MembersInjector;
import com.btw.app.service.HandoffMonitorWorker;
import com.btw.app.service.HandoffMonitorWorker_AssistedFactory;
import com.btw.app.service.SmsVerificationReceiver;
import com.btw.app.service.SmsVerificationReceiver_MembersInjector;
import com.btw.app.ui.handoff.HandoffViewModel;
import com.btw.app.ui.handoff.HandoffViewModel_HiltModules_KeyModule_ProvideFactory;
import com.btw.app.ui.home.HomeViewModel;
import com.btw.app.ui.home.HomeViewModel_HiltModules_KeyModule_ProvideFactory;
import com.btw.app.ui.locations.LocationsViewModel;
import com.btw.app.ui.locations.LocationsViewModel_HiltModules_KeyModule_ProvideFactory;
import com.btw.app.ui.onboarding.OnboardingViewModel;
import com.btw.app.ui.onboarding.OnboardingViewModel_HiltModules_KeyModule_ProvideFactory;
import com.btw.app.ui.settings.SettingsViewModel;
import com.btw.app.ui.settings.SettingsViewModel_HiltModules_KeyModule_ProvideFactory;
import com.btw.app.ui.setup.AddRiderViewModel;
import com.btw.app.ui.setup.AddRiderViewModel_HiltModules_KeyModule_ProvideFactory;
import com.btw.app.ui.setup.AlertPrefsViewModel;
import com.btw.app.ui.setup.AlertPrefsViewModel_HiltModules_KeyModule_ProvideFactory;
import com.btw.app.ui.setup.PairVehicleViewModel;
import com.btw.app.ui.setup.PairVehicleViewModel_HiltModules_KeyModule_ProvideFactory;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideApplicationFactory;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SetBuilder;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DaggerBtwApplication_HiltComponents_SingletonC {
  private DaggerBtwApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public BtwApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements BtwApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public BtwApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements BtwApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public BtwApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements BtwApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public BtwApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements BtwApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public BtwApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements BtwApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public BtwApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements BtwApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public BtwApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements BtwApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public BtwApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends BtwApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends BtwApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends BtwApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends BtwApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Set<String> getViewModelKeys() {
      return SetBuilder.<String>newSetBuilder(8).add(AddRiderViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(AlertPrefsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(HandoffViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(HomeViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(LocationsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(OnboardingViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(PairVehicleViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(SettingsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).build();
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectPreferencesRepository(instance, singletonCImpl.bindPreferencesRepositoryProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends BtwApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AddRiderViewModel> addRiderViewModelProvider;

    private Provider<AlertPrefsViewModel> alertPrefsViewModelProvider;

    private Provider<HandoffViewModel> handoffViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LocationsViewModel> locationsViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<PairVehicleViewModel> pairVehicleViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private AddRiderUseCase addRiderUseCase() {
      return new AddRiderUseCase(singletonCImpl.bindRiderRepositoryProvider.get());
    }

    private GetLocationsUseCase getLocationsUseCase() {
      return new GetLocationsUseCase(singletonCImpl.bindLocationRepositoryProvider.get());
    }

    private GetRidersUseCase getRidersUseCase() {
      return new GetRidersUseCase(singletonCImpl.bindRiderRepositoryProvider.get());
    }

    private GetVehiclesUseCase getVehiclesUseCase() {
      return new GetVehiclesUseCase(singletonCImpl.bindVehicleRepositoryProvider.get());
    }

    private GetAlertHistoryUseCase getAlertHistoryUseCase() {
      return new GetAlertHistoryUseCase(singletonCImpl.bindAlertRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.addRiderViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.alertPrefsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.handoffViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.locationsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.pairVehicleViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<String, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(8).put("com.btw.app.ui.setup.AddRiderViewModel", ((Provider) addRiderViewModelProvider)).put("com.btw.app.ui.setup.AlertPrefsViewModel", ((Provider) alertPrefsViewModelProvider)).put("com.btw.app.ui.handoff.HandoffViewModel", ((Provider) handoffViewModelProvider)).put("com.btw.app.ui.home.HomeViewModel", ((Provider) homeViewModelProvider)).put("com.btw.app.ui.locations.LocationsViewModel", ((Provider) locationsViewModelProvider)).put("com.btw.app.ui.onboarding.OnboardingViewModel", ((Provider) onboardingViewModelProvider)).put("com.btw.app.ui.setup.PairVehicleViewModel", ((Provider) pairVehicleViewModelProvider)).put("com.btw.app.ui.settings.SettingsViewModel", ((Provider) settingsViewModelProvider)).build();
    }

    @Override
    public Map<String, Object> getHiltViewModelAssistedMap() {
      return Collections.<String, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.btw.app.ui.setup.AddRiderViewModel 
          return (T) new AddRiderViewModel(viewModelCImpl.addRiderUseCase());

          case 1: // com.btw.app.ui.setup.AlertPrefsViewModel 
          return (T) new AlertPrefsViewModel(singletonCImpl.bindPreferencesRepositoryProvider.get());

          case 2: // com.btw.app.ui.handoff.HandoffViewModel 
          return (T) new HandoffViewModel(singletonCImpl.bindHandoffRepositoryProvider.get(), viewModelCImpl.getLocationsUseCase());

          case 3: // com.btw.app.ui.home.HomeViewModel 
          return (T) new HomeViewModel(ApplicationContextModule_ProvideApplicationFactory.provideApplication(singletonCImpl.applicationContextModule), viewModelCImpl.getRidersUseCase(), viewModelCImpl.getVehiclesUseCase(), singletonCImpl.bindAlertRepositoryProvider.get(), singletonCImpl.bindPreferencesRepositoryProvider.get());

          case 4: // com.btw.app.ui.locations.LocationsViewModel 
          return (T) new LocationsViewModel(viewModelCImpl.getLocationsUseCase(), singletonCImpl.bindLocationRepositoryProvider.get());

          case 5: // com.btw.app.ui.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.bindPreferencesRepositoryProvider.get());

          case 6: // com.btw.app.ui.setup.PairVehicleViewModel 
          return (T) new PairVehicleViewModel(singletonCImpl.bindVehicleRepositoryProvider.get());

          case 7: // com.btw.app.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(viewModelCImpl.getRidersUseCase(), viewModelCImpl.getAlertHistoryUseCase(), singletonCImpl.bindPreferencesRepositoryProvider.get(), singletonCImpl.bindRiderRepositoryProvider.get(), viewModelCImpl.addRiderUseCase());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends BtwApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends BtwApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    private GetNearbyLocationUseCase getNearbyLocationUseCase() {
      return new GetNearbyLocationUseCase(singletonCImpl.bindLocationRepositoryProvider.get());
    }

    private RecordLocationVisitUseCase recordLocationVisitUseCase() {
      return new RecordLocationVisitUseCase(singletonCImpl.bindLocationRepositoryProvider.get());
    }

    private CheckExpectedPickupUseCase checkExpectedPickupUseCase() {
      return new CheckExpectedPickupUseCase(singletonCImpl.bindHandoffRepositoryProvider.get());
    }

    @Override
    public void injectBtwMonitorService(BtwMonitorService btwMonitorService) {
      injectBtwMonitorService2(btwMonitorService);
    }

    private BtwMonitorService injectBtwMonitorService2(BtwMonitorService instance) {
      BtwMonitorService_MembersInjector.injectDatabase(instance, singletonCImpl.provideDatabaseProvider.get());
      BtwMonitorService_MembersInjector.injectWorkManager(instance, singletonCImpl.provideWorkManagerProvider.get());
      BtwMonitorService_MembersInjector.injectGetNearbyLocation(instance, getNearbyLocationUseCase());
      BtwMonitorService_MembersInjector.injectRecordLocationVisit(instance, recordLocationVisitUseCase());
      BtwMonitorService_MembersInjector.injectCheckExpectedPickup(instance, checkExpectedPickupUseCase());
      return instance;
    }
  }

  private static final class SingletonCImpl extends BtwApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<BtwDatabase> provideDatabaseProvider;

    private Provider<BtwPreferences> providePreferencesProvider;

    private Provider<AlertEscalationWorker_AssistedFactory> alertEscalationWorker_AssistedFactoryProvider;

    private Provider<HandoffRepositoryImpl> handoffRepositoryImplProvider;

    private Provider<HandoffRepository> bindHandoffRepositoryProvider;

    private Provider<RiderRepositoryImpl> riderRepositoryImplProvider;

    private Provider<RiderRepository> bindRiderRepositoryProvider;

    private Provider<LocationRepositoryImpl> locationRepositoryImplProvider;

    private Provider<LocationRepository> bindLocationRepositoryProvider;

    private Provider<HandoffMonitorWorker_AssistedFactory> handoffMonitorWorker_AssistedFactoryProvider;

    private Provider<PreferencesRepositoryImpl> preferencesRepositoryImplProvider;

    private Provider<PreferencesRepository> bindPreferencesRepositoryProvider;

    private Provider<VehicleRepositoryImpl> vehicleRepositoryImplProvider;

    private Provider<VehicleRepository> bindVehicleRepositoryProvider;

    private Provider<AlertRepositoryImpl> alertRepositoryImplProvider;

    private Provider<AlertRepository> bindAlertRepositoryProvider;

    private Provider<WorkManager> provideWorkManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private HandoffDao handoffDao() {
      return DatabaseModule_ProvideHandoffDaoFactory.provideHandoffDao(provideDatabaseProvider.get());
    }

    private RiderDao riderDao() {
      return DatabaseModule_ProvideRiderDaoFactory.provideRiderDao(provideDatabaseProvider.get());
    }

    private LocationDao locationDao() {
      return DatabaseModule_ProvideLocationDaoFactory.provideLocationDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return MapBuilder.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>newMapBuilder(2).put("com.btw.app.service.AlertEscalationWorker", ((Provider) alertEscalationWorker_AssistedFactoryProvider)).put("com.btw.app.service.HandoffMonitorWorker", ((Provider) handoffMonitorWorker_AssistedFactoryProvider)).build();
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private VehicleDao vehicleDao() {
      return DatabaseModule_ProvideVehicleDaoFactory.provideVehicleDao(provideDatabaseProvider.get());
    }

    private AlertDao alertDao() {
      return DatabaseModule_ProvideAlertDaoFactory.provideAlertDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<BtwDatabase>(singletonCImpl, 1));
      this.providePreferencesProvider = DoubleCheck.provider(new SwitchingProvider<BtwPreferences>(singletonCImpl, 2));
      this.alertEscalationWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<AlertEscalationWorker_AssistedFactory>(singletonCImpl, 0));
      this.handoffRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 4);
      this.bindHandoffRepositoryProvider = DoubleCheck.provider((Provider) handoffRepositoryImplProvider);
      this.riderRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 5);
      this.bindRiderRepositoryProvider = DoubleCheck.provider((Provider) riderRepositoryImplProvider);
      this.locationRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 6);
      this.bindLocationRepositoryProvider = DoubleCheck.provider((Provider) locationRepositoryImplProvider);
      this.handoffMonitorWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<HandoffMonitorWorker_AssistedFactory>(singletonCImpl, 3));
      this.preferencesRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 7);
      this.bindPreferencesRepositoryProvider = DoubleCheck.provider((Provider) preferencesRepositoryImplProvider);
      this.vehicleRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 8);
      this.bindVehicleRepositoryProvider = DoubleCheck.provider((Provider) vehicleRepositoryImplProvider);
      this.alertRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 9);
      this.bindAlertRepositoryProvider = DoubleCheck.provider((Provider) alertRepositoryImplProvider);
      this.provideWorkManagerProvider = DoubleCheck.provider(new SwitchingProvider<WorkManager>(singletonCImpl, 10));
    }

    @Override
    public void injectBtwApplication(BtwApplication btwApplication) {
      injectBtwApplication2(btwApplication);
    }

    @Override
    public void injectSmsVerificationReceiver(SmsVerificationReceiver smsVerificationReceiver) {
      injectSmsVerificationReceiver2(smsVerificationReceiver);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private BtwApplication injectBtwApplication2(BtwApplication instance) {
      BtwApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private SmsVerificationReceiver injectSmsVerificationReceiver2(
        SmsVerificationReceiver instance) {
      SmsVerificationReceiver_MembersInjector.injectDatabase(instance, provideDatabaseProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.btw.app.service.AlertEscalationWorker_AssistedFactory 
          return (T) new AlertEscalationWorker_AssistedFactory() {
            @Override
            public AlertEscalationWorker create(Context context, WorkerParameters params) {
              return new AlertEscalationWorker(context, params, singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.providePreferencesProvider.get());
            }
          };

          case 1: // com.btw.app.data.local.BtwDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.btw.app.data.preferences.BtwPreferences 
          return (T) DatabaseModule_ProvidePreferencesFactory.providePreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.btw.app.service.HandoffMonitorWorker_AssistedFactory 
          return (T) new HandoffMonitorWorker_AssistedFactory() {
            @Override
            public HandoffMonitorWorker create(Context context2, WorkerParameters params2) {
              return new HandoffMonitorWorker(context2, params2, singletonCImpl.bindHandoffRepositoryProvider.get(), singletonCImpl.bindRiderRepositoryProvider.get(), singletonCImpl.bindLocationRepositoryProvider.get());
            }
          };

          case 4: // com.btw.app.data.repository.HandoffRepositoryImpl 
          return (T) new HandoffRepositoryImpl(singletonCImpl.handoffDao());

          case 5: // com.btw.app.data.repository.RiderRepositoryImpl 
          return (T) new RiderRepositoryImpl(singletonCImpl.riderDao());

          case 6: // com.btw.app.data.repository.LocationRepositoryImpl 
          return (T) new LocationRepositoryImpl(singletonCImpl.locationDao());

          case 7: // com.btw.app.data.repository.PreferencesRepositoryImpl 
          return (T) new PreferencesRepositoryImpl(singletonCImpl.providePreferencesProvider.get());

          case 8: // com.btw.app.data.repository.VehicleRepositoryImpl 
          return (T) new VehicleRepositoryImpl(singletonCImpl.vehicleDao());

          case 9: // com.btw.app.data.repository.AlertRepositoryImpl 
          return (T) new AlertRepositoryImpl(singletonCImpl.alertDao());

          case 10: // androidx.work.WorkManager 
          return (T) DatabaseModule_ProvideWorkManagerFactory.provideWorkManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
