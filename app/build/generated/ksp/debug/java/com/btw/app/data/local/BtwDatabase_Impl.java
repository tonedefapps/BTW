package com.btw.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.btw.app.data.local.dao.AlertDao;
import com.btw.app.data.local.dao.AlertDao_Impl;
import com.btw.app.data.local.dao.HandoffDao;
import com.btw.app.data.local.dao.HandoffDao_Impl;
import com.btw.app.data.local.dao.LocationDao;
import com.btw.app.data.local.dao.LocationDao_Impl;
import com.btw.app.data.local.dao.RiderDao;
import com.btw.app.data.local.dao.RiderDao_Impl;
import com.btw.app.data.local.dao.VehicleDao;
import com.btw.app.data.local.dao.VehicleDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BtwDatabase_Impl extends BtwDatabase {
  private volatile RiderDao _riderDao;

  private volatile VehicleDao _vehicleDao;

  private volatile AlertDao _alertDao;

  private volatile LocationDao _locationDao;

  private volatile HandoffDao _handoffDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `riders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `emoji` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `bluetoothAddress` TEXT NOT NULL, `lastLatitude` REAL NOT NULL, `lastLongitude` REAL NOT NULL, `lastSeenAt` INTEGER NOT NULL, `isPrimary` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `alert_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `riderId` INTEGER NOT NULL, `riderName` TEXT NOT NULL, `vehicleId` INTEGER NOT NULL, `vehicleName` TEXT NOT NULL, `triggeredAt` INTEGER NOT NULL, `acknowledgedAt` INTEGER, `outcome` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_locations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lat` REAL NOT NULL, `lng` REAL NOT NULL, `radiusMeters` REAL NOT NULL, `label` TEXT NOT NULL, `emoji` TEXT NOT NULL, `source` TEXT NOT NULL, `confidence` REAL NOT NULL, `visitCount` INTEGER NOT NULL, `lastVisited` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `rider_location_stats` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `riderId` INTEGER NOT NULL, `locationId` INTEGER NOT NULL, `presentCount` INTEGER NOT NULL, `absentCount` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_rider_location_stats_riderId_locationId` ON `rider_location_stats` (`riderId`, `locationId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `handoff_contacts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `riderId` INTEGER NOT NULL, `name` TEXT NOT NULL, `phone` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pickup_windows` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `riderId` INTEGER NOT NULL, `locationId` INTEGER NOT NULL, `daysOfWeekBitmask` INTEGER NOT NULL, `startHour` INTEGER NOT NULL, `startMinute` INTEGER NOT NULL, `windowMinutes` INTEGER NOT NULL, `isActive` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `handoff_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `riderId` INTEGER NOT NULL, `locationId` INTEGER NOT NULL, `riderName` TEXT NOT NULL, `locationLabel` TEXT NOT NULL, `expectedAt` INTEGER NOT NULL, `occurredAt` INTEGER, `verifiedBy` TEXT, `outcome` TEXT NOT NULL, `smsSentTo` TEXT, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '726f81499462657307e7f70cb4f4fba2')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `riders`");
        db.execSQL("DROP TABLE IF EXISTS `vehicles`");
        db.execSQL("DROP TABLE IF EXISTS `alert_events`");
        db.execSQL("DROP TABLE IF EXISTS `saved_locations`");
        db.execSQL("DROP TABLE IF EXISTS `rider_location_stats`");
        db.execSQL("DROP TABLE IF EXISTS `handoff_contacts`");
        db.execSQL("DROP TABLE IF EXISTS `pickup_windows`");
        db.execSQL("DROP TABLE IF EXISTS `handoff_events`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRiders = new HashMap<String, TableInfo.Column>(5);
        _columnsRiders.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiders.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiders.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiders.put("emoji", new TableInfo.Column("emoji", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiders.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRiders = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRiders = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRiders = new TableInfo("riders", _columnsRiders, _foreignKeysRiders, _indicesRiders);
        final TableInfo _existingRiders = TableInfo.read(db, "riders");
        if (!_infoRiders.equals(_existingRiders)) {
          return new RoomOpenHelper.ValidationResult(false, "riders(com.btw.app.data.local.entity.RiderEntity).\n"
                  + " Expected:\n" + _infoRiders + "\n"
                  + " Found:\n" + _existingRiders);
        }
        final HashMap<String, TableInfo.Column> _columnsVehicles = new HashMap<String, TableInfo.Column>(7);
        _columnsVehicles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("bluetoothAddress", new TableInfo.Column("bluetoothAddress", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("lastLatitude", new TableInfo.Column("lastLatitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("lastLongitude", new TableInfo.Column("lastLongitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("lastSeenAt", new TableInfo.Column("lastSeenAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("isPrimary", new TableInfo.Column("isPrimary", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVehicles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVehicles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVehicles = new TableInfo("vehicles", _columnsVehicles, _foreignKeysVehicles, _indicesVehicles);
        final TableInfo _existingVehicles = TableInfo.read(db, "vehicles");
        if (!_infoVehicles.equals(_existingVehicles)) {
          return new RoomOpenHelper.ValidationResult(false, "vehicles(com.btw.app.data.local.entity.VehicleEntity).\n"
                  + " Expected:\n" + _infoVehicles + "\n"
                  + " Found:\n" + _existingVehicles);
        }
        final HashMap<String, TableInfo.Column> _columnsAlertEvents = new HashMap<String, TableInfo.Column>(10);
        _columnsAlertEvents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("riderId", new TableInfo.Column("riderId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("riderName", new TableInfo.Column("riderName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("vehicleName", new TableInfo.Column("vehicleName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("triggeredAt", new TableInfo.Column("triggeredAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("acknowledgedAt", new TableInfo.Column("acknowledgedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("outcome", new TableInfo.Column("outcome", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertEvents.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAlertEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAlertEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAlertEvents = new TableInfo("alert_events", _columnsAlertEvents, _foreignKeysAlertEvents, _indicesAlertEvents);
        final TableInfo _existingAlertEvents = TableInfo.read(db, "alert_events");
        if (!_infoAlertEvents.equals(_existingAlertEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "alert_events(com.btw.app.data.local.entity.AlertEntity).\n"
                  + " Expected:\n" + _infoAlertEvents + "\n"
                  + " Found:\n" + _existingAlertEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsSavedLocations = new HashMap<String, TableInfo.Column>(10);
        _columnsSavedLocations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedLocations.put("lat", new TableInfo.Column("lat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedLocations.put("lng", new TableInfo.Column("lng", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedLocations.put("radiusMeters", new TableInfo.Column("radiusMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedLocations.put("label", new TableInfo.Column("label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedLocations.put("emoji", new TableInfo.Column("emoji", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedLocations.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedLocations.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedLocations.put("visitCount", new TableInfo.Column("visitCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedLocations.put("lastVisited", new TableInfo.Column("lastVisited", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSavedLocations = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSavedLocations = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSavedLocations = new TableInfo("saved_locations", _columnsSavedLocations, _foreignKeysSavedLocations, _indicesSavedLocations);
        final TableInfo _existingSavedLocations = TableInfo.read(db, "saved_locations");
        if (!_infoSavedLocations.equals(_existingSavedLocations)) {
          return new RoomOpenHelper.ValidationResult(false, "saved_locations(com.btw.app.data.local.entity.SavedLocationEntity).\n"
                  + " Expected:\n" + _infoSavedLocations + "\n"
                  + " Found:\n" + _existingSavedLocations);
        }
        final HashMap<String, TableInfo.Column> _columnsRiderLocationStats = new HashMap<String, TableInfo.Column>(5);
        _columnsRiderLocationStats.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiderLocationStats.put("riderId", new TableInfo.Column("riderId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiderLocationStats.put("locationId", new TableInfo.Column("locationId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiderLocationStats.put("presentCount", new TableInfo.Column("presentCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiderLocationStats.put("absentCount", new TableInfo.Column("absentCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRiderLocationStats = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRiderLocationStats = new HashSet<TableInfo.Index>(1);
        _indicesRiderLocationStats.add(new TableInfo.Index("index_rider_location_stats_riderId_locationId", true, Arrays.asList("riderId", "locationId"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoRiderLocationStats = new TableInfo("rider_location_stats", _columnsRiderLocationStats, _foreignKeysRiderLocationStats, _indicesRiderLocationStats);
        final TableInfo _existingRiderLocationStats = TableInfo.read(db, "rider_location_stats");
        if (!_infoRiderLocationStats.equals(_existingRiderLocationStats)) {
          return new RoomOpenHelper.ValidationResult(false, "rider_location_stats(com.btw.app.data.local.entity.RiderLocationStatsEntity).\n"
                  + " Expected:\n" + _infoRiderLocationStats + "\n"
                  + " Found:\n" + _existingRiderLocationStats);
        }
        final HashMap<String, TableInfo.Column> _columnsHandoffContacts = new HashMap<String, TableInfo.Column>(4);
        _columnsHandoffContacts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffContacts.put("riderId", new TableInfo.Column("riderId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffContacts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffContacts.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHandoffContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHandoffContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHandoffContacts = new TableInfo("handoff_contacts", _columnsHandoffContacts, _foreignKeysHandoffContacts, _indicesHandoffContacts);
        final TableInfo _existingHandoffContacts = TableInfo.read(db, "handoff_contacts");
        if (!_infoHandoffContacts.equals(_existingHandoffContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "handoff_contacts(com.btw.app.data.local.entity.HandoffContactEntity).\n"
                  + " Expected:\n" + _infoHandoffContacts + "\n"
                  + " Found:\n" + _existingHandoffContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsPickupWindows = new HashMap<String, TableInfo.Column>(8);
        _columnsPickupWindows.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPickupWindows.put("riderId", new TableInfo.Column("riderId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPickupWindows.put("locationId", new TableInfo.Column("locationId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPickupWindows.put("daysOfWeekBitmask", new TableInfo.Column("daysOfWeekBitmask", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPickupWindows.put("startHour", new TableInfo.Column("startHour", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPickupWindows.put("startMinute", new TableInfo.Column("startMinute", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPickupWindows.put("windowMinutes", new TableInfo.Column("windowMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPickupWindows.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPickupWindows = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPickupWindows = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPickupWindows = new TableInfo("pickup_windows", _columnsPickupWindows, _foreignKeysPickupWindows, _indicesPickupWindows);
        final TableInfo _existingPickupWindows = TableInfo.read(db, "pickup_windows");
        if (!_infoPickupWindows.equals(_existingPickupWindows)) {
          return new RoomOpenHelper.ValidationResult(false, "pickup_windows(com.btw.app.data.local.entity.PickupWindowEntity).\n"
                  + " Expected:\n" + _infoPickupWindows + "\n"
                  + " Found:\n" + _existingPickupWindows);
        }
        final HashMap<String, TableInfo.Column> _columnsHandoffEvents = new HashMap<String, TableInfo.Column>(11);
        _columnsHandoffEvents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("riderId", new TableInfo.Column("riderId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("locationId", new TableInfo.Column("locationId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("riderName", new TableInfo.Column("riderName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("locationLabel", new TableInfo.Column("locationLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("expectedAt", new TableInfo.Column("expectedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("occurredAt", new TableInfo.Column("occurredAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("verifiedBy", new TableInfo.Column("verifiedBy", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("outcome", new TableInfo.Column("outcome", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("smsSentTo", new TableInfo.Column("smsSentTo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHandoffEvents.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHandoffEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHandoffEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHandoffEvents = new TableInfo("handoff_events", _columnsHandoffEvents, _foreignKeysHandoffEvents, _indicesHandoffEvents);
        final TableInfo _existingHandoffEvents = TableInfo.read(db, "handoff_events");
        if (!_infoHandoffEvents.equals(_existingHandoffEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "handoff_events(com.btw.app.data.local.entity.HandoffEventEntity).\n"
                  + " Expected:\n" + _infoHandoffEvents + "\n"
                  + " Found:\n" + _existingHandoffEvents);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "726f81499462657307e7f70cb4f4fba2", "bcda9fe24c26b17e028c6aed33f3e90d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "riders","vehicles","alert_events","saved_locations","rider_location_stats","handoff_contacts","pickup_windows","handoff_events");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `riders`");
      _db.execSQL("DELETE FROM `vehicles`");
      _db.execSQL("DELETE FROM `alert_events`");
      _db.execSQL("DELETE FROM `saved_locations`");
      _db.execSQL("DELETE FROM `rider_location_stats`");
      _db.execSQL("DELETE FROM `handoff_contacts`");
      _db.execSQL("DELETE FROM `pickup_windows`");
      _db.execSQL("DELETE FROM `handoff_events`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RiderDao.class, RiderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VehicleDao.class, VehicleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AlertDao.class, AlertDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LocationDao.class, LocationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HandoffDao.class, HandoffDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RiderDao riderDao() {
    if (_riderDao != null) {
      return _riderDao;
    } else {
      synchronized(this) {
        if(_riderDao == null) {
          _riderDao = new RiderDao_Impl(this);
        }
        return _riderDao;
      }
    }
  }

  @Override
  public VehicleDao vehicleDao() {
    if (_vehicleDao != null) {
      return _vehicleDao;
    } else {
      synchronized(this) {
        if(_vehicleDao == null) {
          _vehicleDao = new VehicleDao_Impl(this);
        }
        return _vehicleDao;
      }
    }
  }

  @Override
  public AlertDao alertDao() {
    if (_alertDao != null) {
      return _alertDao;
    } else {
      synchronized(this) {
        if(_alertDao == null) {
          _alertDao = new AlertDao_Impl(this);
        }
        return _alertDao;
      }
    }
  }

  @Override
  public LocationDao locationDao() {
    if (_locationDao != null) {
      return _locationDao;
    } else {
      synchronized(this) {
        if(_locationDao == null) {
          _locationDao = new LocationDao_Impl(this);
        }
        return _locationDao;
      }
    }
  }

  @Override
  public HandoffDao handoffDao() {
    if (_handoffDao != null) {
      return _handoffDao;
    } else {
      synchronized(this) {
        if(_handoffDao == null) {
          _handoffDao = new HandoffDao_Impl(this);
        }
        return _handoffDao;
      }
    }
  }
}
