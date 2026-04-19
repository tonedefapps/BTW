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
import com.btw.app.data.local.entity.HandoffContactEntity;
import com.btw.app.data.local.entity.HandoffEventEntity;
import com.btw.app.data.local.entity.PickupWindowEntity;
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
public final class HandoffDao_Impl implements HandoffDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HandoffContactEntity> __insertionAdapterOfHandoffContactEntity;

  private final EntityInsertionAdapter<PickupWindowEntity> __insertionAdapterOfPickupWindowEntity;

  private final EntityInsertionAdapter<HandoffEventEntity> __insertionAdapterOfHandoffEventEntity;

  private final EntityDeletionOrUpdateAdapter<PickupWindowEntity> __updateAdapterOfPickupWindowEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteContact;

  private final SharedSQLiteStatement __preparedStmtOfDeleteWindow;

  private final SharedSQLiteStatement __preparedStmtOfUpdateOutcome;

  public HandoffDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHandoffContactEntity = new EntityInsertionAdapter<HandoffContactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `handoff_contacts` (`id`,`riderId`,`name`,`phone`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HandoffContactEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRiderId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getPhone());
      }
    };
    this.__insertionAdapterOfPickupWindowEntity = new EntityInsertionAdapter<PickupWindowEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pickup_windows` (`id`,`riderId`,`locationId`,`daysOfWeekBitmask`,`startHour`,`startMinute`,`windowMinutes`,`isActive`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PickupWindowEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRiderId());
        statement.bindLong(3, entity.getLocationId());
        statement.bindLong(4, entity.getDaysOfWeekBitmask());
        statement.bindLong(5, entity.getStartHour());
        statement.bindLong(6, entity.getStartMinute());
        statement.bindLong(7, entity.getWindowMinutes());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__insertionAdapterOfHandoffEventEntity = new EntityInsertionAdapter<HandoffEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `handoff_events` (`id`,`riderId`,`locationId`,`riderName`,`locationLabel`,`expectedAt`,`occurredAt`,`verifiedBy`,`outcome`,`smsSentTo`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HandoffEventEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRiderId());
        statement.bindLong(3, entity.getLocationId());
        statement.bindString(4, entity.getRiderName());
        statement.bindString(5, entity.getLocationLabel());
        statement.bindLong(6, entity.getExpectedAt());
        if (entity.getOccurredAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getOccurredAt());
        }
        if (entity.getVerifiedBy() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getVerifiedBy());
        }
        statement.bindString(9, entity.getOutcome());
        if (entity.getSmsSentTo() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getSmsSentTo());
        }
        statement.bindLong(11, entity.getCreatedAt());
      }
    };
    this.__updateAdapterOfPickupWindowEntity = new EntityDeletionOrUpdateAdapter<PickupWindowEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `pickup_windows` SET `id` = ?,`riderId` = ?,`locationId` = ?,`daysOfWeekBitmask` = ?,`startHour` = ?,`startMinute` = ?,`windowMinutes` = ?,`isActive` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PickupWindowEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRiderId());
        statement.bindLong(3, entity.getLocationId());
        statement.bindLong(4, entity.getDaysOfWeekBitmask());
        statement.bindLong(5, entity.getStartHour());
        statement.bindLong(6, entity.getStartMinute());
        statement.bindLong(7, entity.getWindowMinutes());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteContact = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM handoff_contacts WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteWindow = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pickup_windows WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateOutcome = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE handoff_events SET outcome = ?, verifiedBy = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertContact(final HandoffContactEntity contact,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfHandoffContactEntity.insertAndReturnId(contact);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertWindow(final PickupWindowEntity window,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPickupWindowEntity.insertAndReturnId(window);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertEvent(final HandoffEventEntity event,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfHandoffEventEntity.insertAndReturnId(event);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWindow(final PickupWindowEntity window,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPickupWindowEntity.handle(window);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteContact(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteContact.acquire();
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
          __preparedStmtOfDeleteContact.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteWindow(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteWindow.acquire();
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
          __preparedStmtOfDeleteWindow.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateOutcome(final long id, final String outcome, final String verifiedBy,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateOutcome.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, outcome);
        _argIndex = 2;
        if (verifiedBy == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, verifiedBy);
        }
        _argIndex = 3;
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
          __preparedStmtOfUpdateOutcome.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<HandoffContactEntity>> getContactsForRider(final long riderId) {
    final String _sql = "SELECT * FROM handoff_contacts WHERE riderId = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, riderId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"handoff_contacts"}, new Callable<List<HandoffContactEntity>>() {
      @Override
      @NonNull
      public List<HandoffContactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final List<HandoffContactEntity> _result = new ArrayList<HandoffContactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HandoffContactEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            _item = new HandoffContactEntity(_tmpId,_tmpRiderId,_tmpName,_tmpPhone);
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
  public Object getContactsListForRider(final long riderId,
      final Continuation<? super List<HandoffContactEntity>> $completion) {
    final String _sql = "SELECT * FROM handoff_contacts WHERE riderId = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, riderId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HandoffContactEntity>>() {
      @Override
      @NonNull
      public List<HandoffContactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final List<HandoffContactEntity> _result = new ArrayList<HandoffContactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HandoffContactEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            _item = new HandoffContactEntity(_tmpId,_tmpRiderId,_tmpName,_tmpPhone);
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
  public Flow<List<PickupWindowEntity>> getWindowsForRider(final long riderId) {
    final String _sql = "SELECT * FROM pickup_windows WHERE riderId = ? ORDER BY startHour ASC, startMinute ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, riderId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pickup_windows"}, new Callable<List<PickupWindowEntity>>() {
      @Override
      @NonNull
      public List<PickupWindowEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfLocationId = CursorUtil.getColumnIndexOrThrow(_cursor, "locationId");
          final int _cursorIndexOfDaysOfWeekBitmask = CursorUtil.getColumnIndexOrThrow(_cursor, "daysOfWeekBitmask");
          final int _cursorIndexOfStartHour = CursorUtil.getColumnIndexOrThrow(_cursor, "startHour");
          final int _cursorIndexOfStartMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "startMinute");
          final int _cursorIndexOfWindowMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "windowMinutes");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<PickupWindowEntity> _result = new ArrayList<PickupWindowEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PickupWindowEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final long _tmpLocationId;
            _tmpLocationId = _cursor.getLong(_cursorIndexOfLocationId);
            final int _tmpDaysOfWeekBitmask;
            _tmpDaysOfWeekBitmask = _cursor.getInt(_cursorIndexOfDaysOfWeekBitmask);
            final int _tmpStartHour;
            _tmpStartHour = _cursor.getInt(_cursorIndexOfStartHour);
            final int _tmpStartMinute;
            _tmpStartMinute = _cursor.getInt(_cursorIndexOfStartMinute);
            final int _tmpWindowMinutes;
            _tmpWindowMinutes = _cursor.getInt(_cursorIndexOfWindowMinutes);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new PickupWindowEntity(_tmpId,_tmpRiderId,_tmpLocationId,_tmpDaysOfWeekBitmask,_tmpStartHour,_tmpStartMinute,_tmpWindowMinutes,_tmpIsActive);
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
  public Object getActiveWindowsForLocation(final long locationId,
      final Continuation<? super List<PickupWindowEntity>> $completion) {
    final String _sql = "SELECT * FROM pickup_windows WHERE locationId = ? AND isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, locationId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PickupWindowEntity>>() {
      @Override
      @NonNull
      public List<PickupWindowEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfLocationId = CursorUtil.getColumnIndexOrThrow(_cursor, "locationId");
          final int _cursorIndexOfDaysOfWeekBitmask = CursorUtil.getColumnIndexOrThrow(_cursor, "daysOfWeekBitmask");
          final int _cursorIndexOfStartHour = CursorUtil.getColumnIndexOrThrow(_cursor, "startHour");
          final int _cursorIndexOfStartMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "startMinute");
          final int _cursorIndexOfWindowMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "windowMinutes");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<PickupWindowEntity> _result = new ArrayList<PickupWindowEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PickupWindowEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final long _tmpLocationId;
            _tmpLocationId = _cursor.getLong(_cursorIndexOfLocationId);
            final int _tmpDaysOfWeekBitmask;
            _tmpDaysOfWeekBitmask = _cursor.getInt(_cursorIndexOfDaysOfWeekBitmask);
            final int _tmpStartHour;
            _tmpStartHour = _cursor.getInt(_cursorIndexOfStartHour);
            final int _tmpStartMinute;
            _tmpStartMinute = _cursor.getInt(_cursorIndexOfStartMinute);
            final int _tmpWindowMinutes;
            _tmpWindowMinutes = _cursor.getInt(_cursorIndexOfWindowMinutes);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new PickupWindowEntity(_tmpId,_tmpRiderId,_tmpLocationId,_tmpDaysOfWeekBitmask,_tmpStartHour,_tmpStartMinute,_tmpWindowMinutes,_tmpIsActive);
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
  public Object getAllActiveWindows(
      final Continuation<? super List<PickupWindowEntity>> $completion) {
    final String _sql = "SELECT * FROM pickup_windows WHERE isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PickupWindowEntity>>() {
      @Override
      @NonNull
      public List<PickupWindowEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfLocationId = CursorUtil.getColumnIndexOrThrow(_cursor, "locationId");
          final int _cursorIndexOfDaysOfWeekBitmask = CursorUtil.getColumnIndexOrThrow(_cursor, "daysOfWeekBitmask");
          final int _cursorIndexOfStartHour = CursorUtil.getColumnIndexOrThrow(_cursor, "startHour");
          final int _cursorIndexOfStartMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "startMinute");
          final int _cursorIndexOfWindowMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "windowMinutes");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<PickupWindowEntity> _result = new ArrayList<PickupWindowEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PickupWindowEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final long _tmpLocationId;
            _tmpLocationId = _cursor.getLong(_cursorIndexOfLocationId);
            final int _tmpDaysOfWeekBitmask;
            _tmpDaysOfWeekBitmask = _cursor.getInt(_cursorIndexOfDaysOfWeekBitmask);
            final int _tmpStartHour;
            _tmpStartHour = _cursor.getInt(_cursorIndexOfStartHour);
            final int _tmpStartMinute;
            _tmpStartMinute = _cursor.getInt(_cursorIndexOfStartMinute);
            final int _tmpWindowMinutes;
            _tmpWindowMinutes = _cursor.getInt(_cursorIndexOfWindowMinutes);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new PickupWindowEntity(_tmpId,_tmpRiderId,_tmpLocationId,_tmpDaysOfWeekBitmask,_tmpStartHour,_tmpStartMinute,_tmpWindowMinutes,_tmpIsActive);
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
  public Flow<List<HandoffEventEntity>> getAllHandoffEvents() {
    final String _sql = "SELECT * FROM handoff_events ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"handoff_events"}, new Callable<List<HandoffEventEntity>>() {
      @Override
      @NonNull
      public List<HandoffEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfLocationId = CursorUtil.getColumnIndexOrThrow(_cursor, "locationId");
          final int _cursorIndexOfRiderName = CursorUtil.getColumnIndexOrThrow(_cursor, "riderName");
          final int _cursorIndexOfLocationLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "locationLabel");
          final int _cursorIndexOfExpectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedAt");
          final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurredAt");
          final int _cursorIndexOfVerifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedBy");
          final int _cursorIndexOfOutcome = CursorUtil.getColumnIndexOrThrow(_cursor, "outcome");
          final int _cursorIndexOfSmsSentTo = CursorUtil.getColumnIndexOrThrow(_cursor, "smsSentTo");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<HandoffEventEntity> _result = new ArrayList<HandoffEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HandoffEventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final long _tmpLocationId;
            _tmpLocationId = _cursor.getLong(_cursorIndexOfLocationId);
            final String _tmpRiderName;
            _tmpRiderName = _cursor.getString(_cursorIndexOfRiderName);
            final String _tmpLocationLabel;
            _tmpLocationLabel = _cursor.getString(_cursorIndexOfLocationLabel);
            final long _tmpExpectedAt;
            _tmpExpectedAt = _cursor.getLong(_cursorIndexOfExpectedAt);
            final Long _tmpOccurredAt;
            if (_cursor.isNull(_cursorIndexOfOccurredAt)) {
              _tmpOccurredAt = null;
            } else {
              _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
            }
            final String _tmpVerifiedBy;
            if (_cursor.isNull(_cursorIndexOfVerifiedBy)) {
              _tmpVerifiedBy = null;
            } else {
              _tmpVerifiedBy = _cursor.getString(_cursorIndexOfVerifiedBy);
            }
            final String _tmpOutcome;
            _tmpOutcome = _cursor.getString(_cursorIndexOfOutcome);
            final String _tmpSmsSentTo;
            if (_cursor.isNull(_cursorIndexOfSmsSentTo)) {
              _tmpSmsSentTo = null;
            } else {
              _tmpSmsSentTo = _cursor.getString(_cursorIndexOfSmsSentTo);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new HandoffEventEntity(_tmpId,_tmpRiderId,_tmpLocationId,_tmpRiderName,_tmpLocationLabel,_tmpExpectedAt,_tmpOccurredAt,_tmpVerifiedBy,_tmpOutcome,_tmpSmsSentTo,_tmpCreatedAt);
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
  public Object getPendingForRider(final long riderId,
      final Continuation<? super HandoffEventEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM handoff_events\n"
            + "        WHERE riderId = ? AND outcome = 'PENDING'\n"
            + "        ORDER BY createdAt DESC LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, riderId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<HandoffEventEntity>() {
      @Override
      @Nullable
      public HandoffEventEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfLocationId = CursorUtil.getColumnIndexOrThrow(_cursor, "locationId");
          final int _cursorIndexOfRiderName = CursorUtil.getColumnIndexOrThrow(_cursor, "riderName");
          final int _cursorIndexOfLocationLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "locationLabel");
          final int _cursorIndexOfExpectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedAt");
          final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurredAt");
          final int _cursorIndexOfVerifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedBy");
          final int _cursorIndexOfOutcome = CursorUtil.getColumnIndexOrThrow(_cursor, "outcome");
          final int _cursorIndexOfSmsSentTo = CursorUtil.getColumnIndexOrThrow(_cursor, "smsSentTo");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final HandoffEventEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final long _tmpLocationId;
            _tmpLocationId = _cursor.getLong(_cursorIndexOfLocationId);
            final String _tmpRiderName;
            _tmpRiderName = _cursor.getString(_cursorIndexOfRiderName);
            final String _tmpLocationLabel;
            _tmpLocationLabel = _cursor.getString(_cursorIndexOfLocationLabel);
            final long _tmpExpectedAt;
            _tmpExpectedAt = _cursor.getLong(_cursorIndexOfExpectedAt);
            final Long _tmpOccurredAt;
            if (_cursor.isNull(_cursorIndexOfOccurredAt)) {
              _tmpOccurredAt = null;
            } else {
              _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
            }
            final String _tmpVerifiedBy;
            if (_cursor.isNull(_cursorIndexOfVerifiedBy)) {
              _tmpVerifiedBy = null;
            } else {
              _tmpVerifiedBy = _cursor.getString(_cursorIndexOfVerifiedBy);
            }
            final String _tmpOutcome;
            _tmpOutcome = _cursor.getString(_cursorIndexOfOutcome);
            final String _tmpSmsSentTo;
            if (_cursor.isNull(_cursorIndexOfSmsSentTo)) {
              _tmpSmsSentTo = null;
            } else {
              _tmpSmsSentTo = _cursor.getString(_cursorIndexOfSmsSentTo);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new HandoffEventEntity(_tmpId,_tmpRiderId,_tmpLocationId,_tmpRiderName,_tmpLocationLabel,_tmpExpectedAt,_tmpOccurredAt,_tmpVerifiedBy,_tmpOutcome,_tmpSmsSentTo,_tmpCreatedAt);
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
  public Object getPendingEventForContactPhone(final String phone,
      final Continuation<? super HandoffEventEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT e.* FROM handoff_events e\n"
            + "        INNER JOIN handoff_contacts c ON c.riderId = e.riderId AND c.phone = ?\n"
            + "        WHERE e.outcome = 'PENDING'\n"
            + "        ORDER BY e.createdAt DESC LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, phone);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<HandoffEventEntity>() {
      @Override
      @Nullable
      public HandoffEventEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfLocationId = CursorUtil.getColumnIndexOrThrow(_cursor, "locationId");
          final int _cursorIndexOfRiderName = CursorUtil.getColumnIndexOrThrow(_cursor, "riderName");
          final int _cursorIndexOfLocationLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "locationLabel");
          final int _cursorIndexOfExpectedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedAt");
          final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurredAt");
          final int _cursorIndexOfVerifiedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedBy");
          final int _cursorIndexOfOutcome = CursorUtil.getColumnIndexOrThrow(_cursor, "outcome");
          final int _cursorIndexOfSmsSentTo = CursorUtil.getColumnIndexOrThrow(_cursor, "smsSentTo");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final HandoffEventEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRiderId;
            _tmpRiderId = _cursor.getLong(_cursorIndexOfRiderId);
            final long _tmpLocationId;
            _tmpLocationId = _cursor.getLong(_cursorIndexOfLocationId);
            final String _tmpRiderName;
            _tmpRiderName = _cursor.getString(_cursorIndexOfRiderName);
            final String _tmpLocationLabel;
            _tmpLocationLabel = _cursor.getString(_cursorIndexOfLocationLabel);
            final long _tmpExpectedAt;
            _tmpExpectedAt = _cursor.getLong(_cursorIndexOfExpectedAt);
            final Long _tmpOccurredAt;
            if (_cursor.isNull(_cursorIndexOfOccurredAt)) {
              _tmpOccurredAt = null;
            } else {
              _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
            }
            final String _tmpVerifiedBy;
            if (_cursor.isNull(_cursorIndexOfVerifiedBy)) {
              _tmpVerifiedBy = null;
            } else {
              _tmpVerifiedBy = _cursor.getString(_cursorIndexOfVerifiedBy);
            }
            final String _tmpOutcome;
            _tmpOutcome = _cursor.getString(_cursorIndexOfOutcome);
            final String _tmpSmsSentTo;
            if (_cursor.isNull(_cursorIndexOfSmsSentTo)) {
              _tmpSmsSentTo = null;
            } else {
              _tmpSmsSentTo = _cursor.getString(_cursorIndexOfSmsSentTo);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new HandoffEventEntity(_tmpId,_tmpRiderId,_tmpLocationId,_tmpRiderName,_tmpLocationLabel,_tmpExpectedAt,_tmpOccurredAt,_tmpVerifiedBy,_tmpOutcome,_tmpSmsSentTo,_tmpCreatedAt);
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
