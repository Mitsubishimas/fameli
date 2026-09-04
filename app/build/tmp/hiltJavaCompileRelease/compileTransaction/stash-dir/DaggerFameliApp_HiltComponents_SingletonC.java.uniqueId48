package com.fameli.budget;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.fameli.budget.data.local.FameliDatabase;
import com.fameli.budget.data.local.dao.CategoryDao;
import com.fameli.budget.data.local.dao.GoalDao;
import com.fameli.budget.data.local.dao.ShoppingDao;
import com.fameli.budget.data.local.dao.TaskDao;
import com.fameli.budget.data.local.dao.TransactionDao;
import com.fameli.budget.data.repository.FamilyManager;
import com.fameli.budget.data.repository.FamilySyncRepository;
import com.fameli.budget.di.DatabaseModule_ProvideCategoryDaoFactory;
import com.fameli.budget.di.DatabaseModule_ProvideDatabaseFactory;
import com.fameli.budget.di.DatabaseModule_ProvideGoalDaoFactory;
import com.fameli.budget.di.DatabaseModule_ProvideShoppingDaoFactory;
import com.fameli.budget.di.DatabaseModule_ProvideTaskDaoFactory;
import com.fameli.budget.di.DatabaseModule_ProvideTransactionDaoFactory;
import com.fameli.budget.di.FirebaseModule_ProvideAuthFactory;
import com.fameli.budget.firebase.FameliFirebaseMessagingService;
import com.fameli.budget.firebase.FirebaseAuthRepository;
import com.fameli.budget.ui.screens.auth.AuthViewModel;
import com.fameli.budget.ui.screens.auth.AuthViewModel_HiltModules;
import com.fameli.budget.ui.screens.categories.CategoriesViewModel;
import com.fameli.budget.ui.screens.categories.CategoriesViewModel_HiltModules;
import com.fameli.budget.ui.screens.dashboard.DashboardViewModel;
import com.fameli.budget.ui.screens.dashboard.DashboardViewModel_HiltModules;
import com.fameli.budget.ui.screens.family.FamilyViewModel;
import com.fameli.budget.ui.screens.family.FamilyViewModel_HiltModules;
import com.fameli.budget.ui.screens.goals.GoalViewModel;
import com.fameli.budget.ui.screens.goals.GoalViewModel_HiltModules;
import com.fameli.budget.ui.screens.planner.PlannerViewModel;
import com.fameli.budget.ui.screens.planner.PlannerViewModel_HiltModules;
import com.fameli.budget.ui.screens.settings.SettingsViewModel;
import com.fameli.budget.ui.screens.settings.SettingsViewModel_HiltModules;
import com.fameli.budget.ui.screens.shopping.ShoppingViewModel;
import com.fameli.budget.ui.screens.shopping.ShoppingViewModel_HiltModules;
import com.fameli.budget.ui.screens.statistics.StatisticsViewModel;
import com.fameli.budget.ui.screens.statistics.StatisticsViewModel_HiltModules;
import com.fameli.budget.ui.screens.transaction.AddTransactionViewModel;
import com.fameli.budget.ui.screens.transaction.AddTransactionViewModel_HiltModules;
import com.fameli.budget.widget.BudgetWidgetProvider;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.firebase.auth.FirebaseAuth;
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
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
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
    "KotlinInternalInJava",
    "cast"
})
public final class DaggerFameliApp_HiltComponents_SingletonC {
  private DaggerFameliApp_HiltComponents_SingletonC() {
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

    public FameliApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements FameliApp_HiltComponents.ActivityRetainedC.Builder {
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
    public FameliApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements FameliApp_HiltComponents.ActivityC.Builder {
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
    public FameliApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements FameliApp_HiltComponents.FragmentC.Builder {
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
    public FameliApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements FameliApp_HiltComponents.ViewWithFragmentC.Builder {
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
    public FameliApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements FameliApp_HiltComponents.ViewC.Builder {
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
    public FameliApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements FameliApp_HiltComponents.ViewModelC.Builder {
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
    public FameliApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements FameliApp_HiltComponents.ServiceC.Builder {
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
    public FameliApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends FameliApp_HiltComponents.ViewWithFragmentC {
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

  private static final class FragmentCImpl extends FameliApp_HiltComponents.FragmentC {
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

  private static final class ViewCImpl extends FameliApp_HiltComponents.ViewC {
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

  private static final class ActivityCImpl extends FameliApp_HiltComponents.ActivityC {
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
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(10).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_transaction_AddTransactionViewModel, AddTransactionViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_auth_AuthViewModel, AuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_categories_CategoriesViewModel, CategoriesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_dashboard_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_family_FamilyViewModel, FamilyViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_goals_GoalViewModel, GoalViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_planner_PlannerViewModel, PlannerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_shopping_ShoppingViewModel, ShoppingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_statistics_StatisticsViewModel, StatisticsViewModel_HiltModules.KeyModule.provide()).build());
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

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_fameli_budget_ui_screens_goals_GoalViewModel = "com.fameli.budget.ui.screens.goals.GoalViewModel";

      static String com_fameli_budget_ui_screens_family_FamilyViewModel = "com.fameli.budget.ui.screens.family.FamilyViewModel";

      static String com_fameli_budget_ui_screens_planner_PlannerViewModel = "com.fameli.budget.ui.screens.planner.PlannerViewModel";

      static String com_fameli_budget_ui_screens_shopping_ShoppingViewModel = "com.fameli.budget.ui.screens.shopping.ShoppingViewModel";

      static String com_fameli_budget_ui_screens_auth_AuthViewModel = "com.fameli.budget.ui.screens.auth.AuthViewModel";

      static String com_fameli_budget_ui_screens_settings_SettingsViewModel = "com.fameli.budget.ui.screens.settings.SettingsViewModel";

      static String com_fameli_budget_ui_screens_statistics_StatisticsViewModel = "com.fameli.budget.ui.screens.statistics.StatisticsViewModel";

      static String com_fameli_budget_ui_screens_categories_CategoriesViewModel = "com.fameli.budget.ui.screens.categories.CategoriesViewModel";

      static String com_fameli_budget_ui_screens_transaction_AddTransactionViewModel = "com.fameli.budget.ui.screens.transaction.AddTransactionViewModel";

      static String com_fameli_budget_ui_screens_dashboard_DashboardViewModel = "com.fameli.budget.ui.screens.dashboard.DashboardViewModel";

      @KeepFieldType
      GoalViewModel com_fameli_budget_ui_screens_goals_GoalViewModel2;

      @KeepFieldType
      FamilyViewModel com_fameli_budget_ui_screens_family_FamilyViewModel2;

      @KeepFieldType
      PlannerViewModel com_fameli_budget_ui_screens_planner_PlannerViewModel2;

      @KeepFieldType
      ShoppingViewModel com_fameli_budget_ui_screens_shopping_ShoppingViewModel2;

      @KeepFieldType
      AuthViewModel com_fameli_budget_ui_screens_auth_AuthViewModel2;

      @KeepFieldType
      SettingsViewModel com_fameli_budget_ui_screens_settings_SettingsViewModel2;

      @KeepFieldType
      StatisticsViewModel com_fameli_budget_ui_screens_statistics_StatisticsViewModel2;

      @KeepFieldType
      CategoriesViewModel com_fameli_budget_ui_screens_categories_CategoriesViewModel2;

      @KeepFieldType
      AddTransactionViewModel com_fameli_budget_ui_screens_transaction_AddTransactionViewModel2;

      @KeepFieldType
      DashboardViewModel com_fameli_budget_ui_screens_dashboard_DashboardViewModel2;
    }
  }

  private static final class ViewModelCImpl extends FameliApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AddTransactionViewModel> addTransactionViewModelProvider;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<CategoriesViewModel> categoriesViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<FamilyViewModel> familyViewModelProvider;

    private Provider<GoalViewModel> goalViewModelProvider;

    private Provider<PlannerViewModel> plannerViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<ShoppingViewModel> shoppingViewModelProvider;

    private Provider<StatisticsViewModel> statisticsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.addTransactionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.categoriesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.familyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.goalViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.plannerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.shoppingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.statisticsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(10).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_transaction_AddTransactionViewModel, ((Provider) addTransactionViewModelProvider)).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_auth_AuthViewModel, ((Provider) authViewModelProvider)).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_categories_CategoriesViewModel, ((Provider) categoriesViewModelProvider)).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_dashboard_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_family_FamilyViewModel, ((Provider) familyViewModelProvider)).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_goals_GoalViewModel, ((Provider) goalViewModelProvider)).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_planner_PlannerViewModel, ((Provider) plannerViewModelProvider)).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_shopping_ShoppingViewModel, ((Provider) shoppingViewModelProvider)).put(LazyClassKeyProvider.com_fameli_budget_ui_screens_statistics_StatisticsViewModel, ((Provider) statisticsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_fameli_budget_ui_screens_statistics_StatisticsViewModel = "com.fameli.budget.ui.screens.statistics.StatisticsViewModel";

      static String com_fameli_budget_ui_screens_categories_CategoriesViewModel = "com.fameli.budget.ui.screens.categories.CategoriesViewModel";

      static String com_fameli_budget_ui_screens_goals_GoalViewModel = "com.fameli.budget.ui.screens.goals.GoalViewModel";

      static String com_fameli_budget_ui_screens_settings_SettingsViewModel = "com.fameli.budget.ui.screens.settings.SettingsViewModel";

      static String com_fameli_budget_ui_screens_family_FamilyViewModel = "com.fameli.budget.ui.screens.family.FamilyViewModel";

      static String com_fameli_budget_ui_screens_planner_PlannerViewModel = "com.fameli.budget.ui.screens.planner.PlannerViewModel";

      static String com_fameli_budget_ui_screens_dashboard_DashboardViewModel = "com.fameli.budget.ui.screens.dashboard.DashboardViewModel";

      static String com_fameli_budget_ui_screens_shopping_ShoppingViewModel = "com.fameli.budget.ui.screens.shopping.ShoppingViewModel";

      static String com_fameli_budget_ui_screens_transaction_AddTransactionViewModel = "com.fameli.budget.ui.screens.transaction.AddTransactionViewModel";

      static String com_fameli_budget_ui_screens_auth_AuthViewModel = "com.fameli.budget.ui.screens.auth.AuthViewModel";

      @KeepFieldType
      StatisticsViewModel com_fameli_budget_ui_screens_statistics_StatisticsViewModel2;

      @KeepFieldType
      CategoriesViewModel com_fameli_budget_ui_screens_categories_CategoriesViewModel2;

      @KeepFieldType
      GoalViewModel com_fameli_budget_ui_screens_goals_GoalViewModel2;

      @KeepFieldType
      SettingsViewModel com_fameli_budget_ui_screens_settings_SettingsViewModel2;

      @KeepFieldType
      FamilyViewModel com_fameli_budget_ui_screens_family_FamilyViewModel2;

      @KeepFieldType
      PlannerViewModel com_fameli_budget_ui_screens_planner_PlannerViewModel2;

      @KeepFieldType
      DashboardViewModel com_fameli_budget_ui_screens_dashboard_DashboardViewModel2;

      @KeepFieldType
      ShoppingViewModel com_fameli_budget_ui_screens_shopping_ShoppingViewModel2;

      @KeepFieldType
      AddTransactionViewModel com_fameli_budget_ui_screens_transaction_AddTransactionViewModel2;

      @KeepFieldType
      AuthViewModel com_fameli_budget_ui_screens_auth_AuthViewModel2;
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
          case 0: // com.fameli.budget.ui.screens.transaction.AddTransactionViewModel 
          return (T) new AddTransactionViewModel(singletonCImpl.transactionDao(), singletonCImpl.categoryDao());

          case 1: // com.fameli.budget.ui.screens.auth.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.firebaseAuthRepositoryProvider.get());

          case 2: // com.fameli.budget.ui.screens.categories.CategoriesViewModel 
          return (T) new CategoriesViewModel(singletonCImpl.categoryDao());

          case 3: // com.fameli.budget.ui.screens.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.transactionDao());

          case 4: // com.fameli.budget.ui.screens.family.FamilyViewModel 
          return (T) new FamilyViewModel(singletonCImpl.familySyncRepositoryProvider.get(), singletonCImpl.familyManagerProvider.get());

          case 5: // com.fameli.budget.ui.screens.goals.GoalViewModel 
          return (T) new GoalViewModel(singletonCImpl.goalDao(), singletonCImpl.firebaseAuthRepositoryProvider.get());

          case 6: // com.fameli.budget.ui.screens.planner.PlannerViewModel 
          return (T) new PlannerViewModel(singletonCImpl.taskDao(), singletonCImpl.firebaseAuthRepositoryProvider.get());

          case 7: // com.fameli.budget.ui.screens.settings.SettingsViewModel 
          return (T) new SettingsViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.firebaseAuthRepositoryProvider.get());

          case 8: // com.fameli.budget.ui.screens.shopping.ShoppingViewModel 
          return (T) new ShoppingViewModel(singletonCImpl.shoppingDao(), singletonCImpl.firebaseAuthRepositoryProvider.get());

          case 9: // com.fameli.budget.ui.screens.statistics.StatisticsViewModel 
          return (T) new StatisticsViewModel(singletonCImpl.transactionDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends FameliApp_HiltComponents.ActivityRetainedC {
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

  private static final class ServiceCImpl extends FameliApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectFameliFirebaseMessagingService(
        FameliFirebaseMessagingService fameliFirebaseMessagingService) {
    }
  }

  private static final class SingletonCImpl extends FameliApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<FameliDatabase> provideDatabaseProvider;

    private Provider<FamilyManager> familyManagerProvider;

    private Provider<FamilySyncRepository> familySyncRepositoryProvider;

    private Provider<FirebaseAuth> provideAuthProvider;

    private Provider<FirebaseAuthRepository> firebaseAuthRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private TransactionDao transactionDao() {
      return DatabaseModule_ProvideTransactionDaoFactory.provideTransactionDao(provideDatabaseProvider.get());
    }

    private CategoryDao categoryDao() {
      return DatabaseModule_ProvideCategoryDaoFactory.provideCategoryDao(provideDatabaseProvider.get());
    }

    private TaskDao taskDao() {
      return DatabaseModule_ProvideTaskDaoFactory.provideTaskDao(provideDatabaseProvider.get());
    }

    private ShoppingDao shoppingDao() {
      return DatabaseModule_ProvideShoppingDaoFactory.provideShoppingDao(provideDatabaseProvider.get());
    }

    private GoalDao goalDao() {
      return DatabaseModule_ProvideGoalDaoFactory.provideGoalDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<FameliDatabase>(singletonCImpl, 0));
      this.familyManagerProvider = DoubleCheck.provider(new SwitchingProvider<FamilyManager>(singletonCImpl, 2));
      this.familySyncRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FamilySyncRepository>(singletonCImpl, 1));
      this.provideAuthProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuth>(singletonCImpl, 4));
      this.firebaseAuthRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuthRepository>(singletonCImpl, 3));
    }

    @Override
    public void injectFameliApp(FameliApp fameliApp) {
      injectFameliApp2(fameliApp);
    }

    @Override
    public void injectBudgetWidgetProvider(BudgetWidgetProvider budgetWidgetProvider) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private FameliApp injectFameliApp2(FameliApp instance) {
      FameliApp_MembersInjector.injectDatabase(instance, provideDatabaseProvider.get());
      FameliApp_MembersInjector.injectFamilySyncRepository(instance, familySyncRepositoryProvider.get());
      FameliApp_MembersInjector.injectFamilyManager(instance, familyManagerProvider.get());
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
          case 0: // com.fameli.budget.data.local.FameliDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.fameli.budget.data.repository.FamilySyncRepository 
          return (T) new FamilySyncRepository(singletonCImpl.transactionDao(), singletonCImpl.categoryDao(), singletonCImpl.taskDao(), singletonCImpl.shoppingDao(), singletonCImpl.familyManagerProvider.get());

          case 2: // com.fameli.budget.data.repository.FamilyManager 
          return (T) new FamilyManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.fameli.budget.firebase.FirebaseAuthRepository 
          return (T) new FirebaseAuthRepository(singletonCImpl.provideAuthProvider.get());

          case 4: // com.google.firebase.auth.FirebaseAuth 
          return (T) FirebaseModule_ProvideAuthFactory.provideAuth();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
