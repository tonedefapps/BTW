package com.btw.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.btw.app.data.local.entity.VehicleEntity;
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
public final class VehicleDao_Impl implements VehicleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VehicleEntity> __insertionAdapterOfVehicleEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLocation;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public VehicleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVehicleEntity = new EntityInsertionAdapter<VehicleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `vehicles` (`id`,`name`,`bluetoothAddress`,`lastLatitude`,`lastLongitude`,`lastSeenAt`,`isPrimary`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VehicleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getBluetoothAddress());
        statement.bindDouble(4, entity.getLastLatitude());
        statement.bindDouble(5, entity.getLastLongitude());
        statement.bindLong(6, entity.getLastSeenAt());
        final int _tmp = entity.isPrimary() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__preparedStmtOfUpdateLocation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE vehicles SET lastLatitude = ?, lastLongitude = ?, lastSeenAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM vehicles WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final VehicleEntity vehicle, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfVehicleEntity.insertAndReturnId(vehicle);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLocation(final long id, final double lat, final double lng, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLocation.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, lat);
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, lng);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 4;
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
          __preparedStmtOfUpdateLocation.release(_stmt);
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
  public Flow<List<VehicleEntity>> getAllVehicles() {
    final String _sql = "SELECT * FROM vehicles ORDER BY isPrimary DESC, lastSeenAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vehicles"}, new Callable<List<VehicleEntity>>() {
      @Override
      @NonNull
      public List<VehicleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBluetoothAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "bluetoothAddress");
          final int _cursorIndexOfLastLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLatitude");
          final int _cursorIndexOfLastLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLongitude");
          final int _cursorIndexOfLastSeenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeenAt");
          final int _cursorIndexOfIsPrimary = CursorUtil.getColumnIndexOrThrow(_cursor, "isPrimary");
          final List<VehicleEntity> _result = new ArrayList<VehicleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VehicleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBluetoothAddress;
            _tmpBluetoothAddress = _cursor.getString(_cursorIndexOfBluetoothAddress);
            final double _tmpLastLatitude;
            _tmpLastLatitude = _cursor.getDouble(_cursorIndexOfLastLatitude);
            final double _tmpLastLongitude;
            _tmpLastLongitude = _cursor.getDouble(_cursorIndexOfLastLongitude);
            final long _tmpLastSeenAt;
            _tmpLastSeenAt = _cursor.getLong(_cursorIndexOfLastSeenAt);
            final boolean _tmpIsPrimary;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPrimary);
            _tmpIsPrimary = _tmp != 0;
            _item = new VehicleEntity(_tmpId,_tmpName,_tmpBluetoothAddress,_tmpLastLatitude,_tmpLastLongitude,_tmpLastSeenAt,_tmpIsPrimary);
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
  public Object getAllVehiclesList(final Continuation<? super List<VehicleEntity>> $completion) {
    final String _sql = "SELECT * FROM vehicles ORDER BY isPrimary DESC, lastSeenAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VehicleEntity>>() {
      @Override
      @NonNull
      public List<VehicleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBluetoothAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "bluetoothAddress");
          final int _cursorIndexOfLastLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLatitude");
          final int _cursorIndexOfLastLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLongitude");
          final int _cursorIndexOfLastSeenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeenAt");
          final int _cursorIndexOfIsPrimary = CursorUtil.getColumnIndexOrThrow(_cursor, "isPrimary");
          final List<VehicleEntity> _result = new ArrayList<VehicleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VehicleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBluetoothAddress;
            _tmpBluetoothAddress = _cursor.getString(_cursorIndexOfBluetoothAddress);
            final double _tmpLastLatitude;
            _tmpLastLatitude = _cursor.getDouble(_cursorIndexOfLastLatitude);
            final double _tmpLastLongitude;
            _tmpLastLongitude = _cursor.getDouble(_cursorIndexOfLastLongitude);
            final long _tmpLastSeenAt;
            _tmpLastSeenAt = _cursor.getLong(_cursorIndexOfLastSeenAt);
            final boolean _tmpIsPrimary;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPrimary);
            _tmpIsPrimary = _tmp != 0;
            _item = new VehicleEntity(_tmpId,_tmpName,_tmpBluetoothAddress,_tmpLastLatitude,_tmpLastLongitude,_tmpLastSeenAt,_tmpIsPrimary);
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
  public Object getVehicleById(final long id,
      final Continuation<? super VehicleEntity> $completion) {
    final String _sql = "SELECT * FROM vehicles WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VehicleEntity>() {
      @Override
      @Nullable
      public VehicleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBluetoothAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "bluetoothAddress");
          final int _cursorIndexOfLastLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLatitude");
          final int _cursorIndexOfLastLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLongitude");
          final int _cursorIndexOfLastSeenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeenAt");
          final int _cursorIndexOfIsPrimary = CursorUtil.getColumnIndexOrThrow(_cursor, "isPrimary");
          final VehicleEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBluetoothAddress;
            _tmpBluetoothAddress = _cursor.getString(_cursorIndexOfBluetoothAddress);
            final double _tmpLastLatitude;
            _tmpLastLatitude = _cursor.getDouble(_cursorIndexOfLastLatitude);
            final double _tmpLastLongitude;
            _tmpLastLongitude = _cursor.getDouble(_cursorIndexOfLastLongitude);
            final long _tmpLastSeenAt;
            _tmpLastSeenAt = _cursor.getLong(_cursorIndexOfLastSeenAt);
            final boolean _tmpIsPrimary;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPrimary);
            _tmpIsPrimary = _tmp != 0;
            _result = new VehicleEntity(_tmpId,_tmpName,_tmpBluetoothAddress,_tmpLastLatitude,_tmpLastLongitude,_tmpLastSeenAt,_tmpIsPrimary);
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
  public Object getByBluetoothAddress(final String address,
      final Continuation<? super VehicleEntity> $completion) {
    final String _sql = "SELECT * FROM vehicles WHERE bluetoothAddress = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, address);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VehicleEntity>() {
      @Override
      @Nullable
      public VehicleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBluetoothAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "bluetoothAddress");
          final int _cursorIndexOfLastLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLatitude");
          final int _cursorIndexOfLastLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLongitude");
          final int _cursorIndexOfLastSeenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeenAt");
          final int _cursorIndexOfIsPrimary = CursorUtil.getColumnIndexOrThrow(_cursor, "isPrimary");
          final VehicleEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBluetoothAddress;
            _tmpBluetoothAddress = _cursor.getString(_cursorIndexOfBluetoothAddress);
            final double _tmpLastLatitude;
            _tmpLastLatitude = _cursor.getDouble(_cursorIndexOfLastLatitude);
            final double _tmpLastLongitude;
            _tmpLastLongitude = _cursor.getDouble(_cursorIndexOfLastLongitude);
            final long _tmpLastSeenAt;
            _tmpLastSeenAt = _cursor.getLong(_cursorIndexOfLastSeenAt);
            final boolean _tmpIsPrimary;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPrimary);
            _tmpIsPrimary = _tmp != 0;
            _result = new VehicleEntity(_tmpId,_tmpName,_tmpBluetoothAddress,_tmpLastLatitude,_tmpLastLongitude,_tmpLastSeenAt,_tmpIsPrimary);
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
