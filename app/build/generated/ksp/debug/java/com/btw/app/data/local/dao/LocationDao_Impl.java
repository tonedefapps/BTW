package com.btw.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.btw.app.data.local.entity.RiderLocationStatsEntity;
import com.btw.app.data.local.entity.SavedLocationEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LocationDao_Impl implements LocationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SavedLocationEntity> __insertionAdapterOfSavedLocationEntity;

  private final EntityInsertionAdapter<RiderLocationStatsEntity> __insertionAdapterOfRiderLocationStatsEntity;

  private final EntityDeletionOrUpdateAdapter<SavedLocationEntity> __updateAdapterOfSavedLocationEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfRecordVisit;

  private final SharedSQLiteStatement __preparedStmtOfIncrementPresent;

  private final SharedSQLiteStatement __preparedStmtOfIncrementAbsent;

  public LocationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSavedLocationEntity = new EntityInsertionAdapter<SavedLocationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `saved_locations` (`id`,`lat`,`lng`,`radiusMeters`,`label`,`emoji`,`source`,`confidence`,`visitCount`,`lastVisited`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedLocationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getLat());
        statement.bindDouble(3, entity.getLng());
        statement.bindDouble(4, entity.getRadiusMeters());
        statement.bindString(5, entity.getLabel());
        statement.bindString(6, entity.getEmoji());
        statement.bindString(7, entity.getSource());
        statement.bindDouble(8, entity.getConfidence());
        statement.bindLong(9, entity.getVisitCount());
        if (entity.getLastVisited() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getLastVisited());
        }
      }
    };
    this.__insertionAdapterOfRiderLocationStatsEntity = new EntityInsertionAdapter<RiderLocationStatsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `rider_location_stats` (`id`,`riderId`,`locationId`,`presentCount`,`absentCount`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RiderLocationStatsEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRiderId());
        statement.bindLong(3, entity.getLocationId());
        statement.bindLong(4, entity.getPresentCount());
        statement.bindLong(5, entity.getAbsentCount());
      }
    };
    this.__updateAdapterOfSavedLocationEntity = new EntityDeletionOrUpdateAdapter<SavedLocationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `saved_locations` SET `id` = ?,`lat` = ?,`lng` = ?,`radiusMeters` = ?,`label` = ?,`emoji` = ?,`source` = ?,`confidence` = ?,`visitCount` = ?,`lastVisited` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedLocationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getLat());
        statement.bindDouble(3, entity.getLng());
        statement.bindDouble(4, entity.getRadiusMeters());
        statement.bindString(5, entity.getLabel());
        statement.bindString(6, entity.getEmoji());
        statement.bindString(7, entity.getSource());
        statement.bindDouble(8, entity.getConfidence());
        statement.bindLong(9, entity.getVisitCount());
        if (entity.getLastVisited() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getLastVisited());
        }
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM saved_locations WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRecordVisit = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE saved_locations\n"
                + "        SET visitCount = visitCount + 1,\n"
                + "            confidence = CASE source\n"
                + "                WHEN 'MANUAL' THEN 1.0\n"
                + "                ELSE MIN(1.0, (visitCount + 1) * 0.1)\n"
                + "            END,\n"
                + "            lastVisited = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementPresent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE rider_location_stats SET presentCount = presentCount + 1 WHERE riderId = ? AND locationId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementAbsent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE rider_location_stats SET absentCount = absentCount + 1 WHERE riderId = ? AND locationId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SavedLocationEntity location,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSavedLocationEntity.insertAndReturnId(location);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertStat(final RiderLocationStatsEntity stat,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRiderLocationStatsEntity.insertAndReturnId(stat);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final SavedLocationEntity location,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSavedLocationEntity.handle(location);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object recordVisit(final long id, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRecordVisit.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRecordVisit.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementPresent(final long riderId, final long locationId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementPresent.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, riderId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, locationId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementPresent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementAbsent(final long riderId, final long locationId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementAbsent.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, riderId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, locationId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementAbsent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SavedLocationEntity>> getAllLocations() {
    final String _sql = "SELECT * FROM saved_locations ORDER BY confidence DESC, visitCount DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_locations"}, new Callable<List<SavedLocationEntity>>() {
      @Override
      @NonNull
      public List<SavedLocationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLng = CursorUtil.getColumnIndexOrThrow(_cursor, "lng");
          final int _cursorIndexOfRadiusMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusMeters");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfEmoji = CursorUtil.getColumnIndexOrThrow(_cursor, "emoji");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfLastVisited = CursorUtil.getColumnIndexOrThrow(_cursor, "lastVisited");
          final List<SavedLocationEntity> _result = new ArrayList<SavedLocationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedLocationEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLng;
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng);
            final float _tmpRadiusMeters;
            _tmpRadiusMeters = _cursor.getFloat(_cursorIndexOfRadiusMeters);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final String _tmpEmoji;
            _tmpEmoji = _cursor.getString(_cursorIndexOfEmoji);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final Long _tmpLastVisited;
            if (_cursor.isNull(_cursorIndexOfLastVisited)) {
              _tmpLastVisited = null;
            } else {
              _tmpLastVisited = _cursor.getLong(_cursorIndexOfLastVisited);
            }
            _item = new SavedLocationEntity(_tmpId,_tmpLat,_tmpLng,_tmpRadiusMeters,_tmpLabel,_tmpEmoji,_tmpSource,_tmpConfidence,_tmpVisitCount,_tmpLastVisited);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id,
      final Continuation<? super SavedLocationEntity> $completion) {
    final String _sql = "SELECT * FROM saved_locations WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SavedLocationEntity>() {
      @Override
      @Nullable
      public SavedLocationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLng = CursorUtil.getColumnIndexOrThrow(_cursor, "lng");
          final int _cursorIndexOfRadiusMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusMeters");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfEmoji = CursorUtil.getColumnIndexOrThrow(_cursor, "emoji");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfLastVisited = CursorUtil.getColumnIndexOrThrow(_cursor, "lastVisited");
          final SavedLocationEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLng;
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng);
            final float _tmpRadiusMeters;
            _tmpRadiusMeters = _cursor.getFloat(_cursorIndexOfRadiusMeters);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final String _tmpEmoji;
            _tmpEmoji = _cursor.getString(_cursorIndexOfEmoji);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final Long _tmpLastVisited;
            if (_cursor.isNull(_cursorIndexOfLastVisited)) {
              _tmpLastVisited = null;
            } else {
              _tmpLastVisited = _cursor.getLong(_cursorIndexOfLastVisited);
            }
            _result = new SavedLocationEntity(_tmpId,_tmpLat,_tmpLng,_tmpRadiusMeters,_tmpLabel,_tmpEmoji,_tmpSource,_tmpConfidence,_tmpVisitCount,_tmpLastVisited);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getWithinBoundingBox(final double minLat, final double maxLat, final double minLng,
      final double maxLng, final Continuation<? super List<SavedLocationEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM saved_locations\n"
            + "        WHERE lat BETWEEN ? AND ?\n"
            + "          AND lng BETWEEN ? AND ?\n"
            + "        ORDER BY confidence DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, minLat);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, maxLat);
    _argIndex = 3;
    _statement.bindDouble(_argIndex, minLng);
    _argIndex = 4;
    _statement.bindDouble(_argIndex, maxLng);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SavedLocationEntity>>() {
      @Override
      @NonNull
      public List<SavedLocationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLng = CursorUtil.getColumnIndexOrThrow(_cursor, "lng");
          final int _cursorIndexOfRadiusMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusMeters");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfEmoji = CursorUtil.getColumnIndexOrThrow(_cursor, "emoji");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfVisitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "visitCount");
          final int _cursorIndexOfLastVisited = CursorUtil.getColumnIndexOrThrow(_cursor, "lastVisited");
          final List<SavedLocationEntity> _result = new ArrayList<SavedLocationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedLocationEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLng;
            _tmpLng = _cursor.getDouble(_cursorIndexOfLng);
            final float _tmpRadiusMeters;
            _tmpRadiusMeters = _cursor.getFloat(_cursorIndexOfRadiusMeters);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final String _tmpEmoji;
            _tmpEmoji = _cursor.getString(_cursorIndexOfEmoji);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final int _tmpVisitCount;
            _tmpVisitCount = _cursor.getInt(_cursorIndexOfVisitCount);
            final Long _tmpLastVisited;
            if (_cursor.isNull(_cursorIndexOfLastVisited)) {
              _tmpLastVisited = null;
            } else {
              _tmpLastVisited = _cursor.getLong(_cursorIndexOfLastVisited);
            }
            _item = new SavedLocationEntity(_tmpId,_tmpLat,_tmpLng,_tmpRadiusMeters,_tmpLabel,_tmpEmoji,_tmpSource,_tmpConfidence,_tmpVisitCount,_tmpLastVisited);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RiderLocationStatsEntity>> getStatsForRider(final long riderId) {
    final String _sql = "SELECT * FROM rider_location_stats WHERE riderId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, riderId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rider_location_stats"}, new Callable<List<RiderLocationStatsEntity>>() {
      @Override
      @NonNull
      public List<RiderLocationStatsEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfLocationId = CursorUtil.getColumnIndexOrThrow(_cursor, "locationId");
          final int _cursorIndexOfPresentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "presentCount");
          final int _cursorIndexOfAbsentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "absentCount");
          final List<RiderLocationStatsEntity> _result = new ArrayList<RiderLocationStatsEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RiderLocationStatsEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final long _tmpLocationId;
            _tmpLocationId = _cursor.getLong(_cursorIndexOfLocationId);
            final int _tmpPresentCount;
            _tmpPresentCount = _cursor.getInt(_cursorIndexOfPresentCount);
            final int _tmpAbsentCount;
            _tmpAbsentCount = _cursor.getInt(_cursorIndexOfAbsentCount);
            _item = new RiderLocationStatsEntity(_tmpId,_tmpRiderId,_tmpLocationId,_tmpPresentCount,_tmpAbsentCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getStat(final long riderId, final long locationId,
      final Continuation<? super RiderLocationStatsEntity> $completion) {
    final String _sql = "SELECT * FROM rider_location_stats WHERE riderId = ? AND locationId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, riderId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, locationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RiderLocationStatsEntity>() {
      @Override
      @Nullable
      public RiderLocationStatsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfLocationId = CursorUtil.getColumnIndexOrThrow(_cursor, "locationId");
          final int _cursorIndexOfPresentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "presentCount");
          final int _cursorIndexOfAbsentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "absentCount");
          final RiderLocationStatsEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final long _tmpLocationId;
            _tmpLocationId = _cursor.getLong(_cursorIndexOfLocationId);
            final int _tmpPresentCount;
            _tmpPresentCount = _cursor.getInt(_cursorIndexOfPresentCount);
            final int _tmpAbsentCount;
            _tmpAbsentCount = _cursor.getInt(_cursorIndexOfAbsentCount);
            _result = new RiderLocationStatsEntity(_tmpId,_tmpRiderId,_tmpLocationId,_tmpPresentCount,_tmpAbsentCount);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
