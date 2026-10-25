package com.fameli.budget.data.local.dao;

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
import com.fameli.budget.data.local.entity.BudgetEntity;
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
public final class BudgetDao_Impl implements BudgetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BudgetEntity> __insertionAdapterOfBudgetEntity;

  private final EntityDeletionOrUpdateAdapter<BudgetEntity> __updateAdapterOfBudgetEntity;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLimit;

  public BudgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBudgetEntity = new EntityInsertionAdapter<BudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `budgets` (`id`,`cloudId`,`categoryId`,`categoryName`,`limitAmount`,`month`,`alertThreshold`,`isDeleted`,`lastModified`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCloudId());
        if (entity.getCategoryId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getCategoryId());
        }
        statement.bindString(4, entity.getCategoryName());
        statement.bindDouble(5, entity.getLimitAmount());
        statement.bindString(6, entity.getMonth());
        statement.bindDouble(7, entity.getAlertThreshold());
        final int _tmp = entity.isDeleted() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getLastModified());
      }
    };
    this.__updateAdapterOfBudgetEntity = new EntityDeletionOrUpdateAdapter<BudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `budgets` SET `id` = ?,`cloudId` = ?,`categoryId` = ?,`categoryName` = ?,`limitAmount` = ?,`month` = ?,`alertThreshold` = ?,`isDeleted` = ?,`lastModified` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCloudId());
        if (entity.getCategoryId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getCategoryId());
        }
        statement.bindString(4, entity.getCategoryName());
        statement.bindDouble(5, entity.getLimitAmount());
        statement.bindString(6, entity.getMonth());
        statement.bindDouble(7, entity.getAlertThreshold());
        final int _tmp = entity.isDeleted() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getLastModified());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE budgets SET isDeleted = 1, lastModified = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLimit = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE budgets SET limitAmount = ?, lastModified = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BudgetEntity budget, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBudgetEntity.insertAndReturnId(budget);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final BudgetEntity budget, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBudgetEntity.handle(budget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object softDelete(final long id, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDelete.acquire();
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
          __preparedStmtOfSoftDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLimit(final long id, final double amount, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLimit.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, amount);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, now);
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
          __preparedStmtOfUpdateLimit.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BudgetEntity>> getForMonth(final String month) {
    final String _sql = "SELECT * FROM budgets WHERE isDeleted = 0 AND month = ? ORDER BY categoryName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, month);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"budgets"}, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCloudId = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudId");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCategoryName = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryName");
          final int _cursorIndexOfLimitAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitAmount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfAlertThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "alertThreshold");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final List<BudgetEntity> _result = new ArrayList<BudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BudgetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCloudId;
            _tmpCloudId = _cursor.getString(_cursorIndexOfCloudId);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpCategoryName;
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            final double _tmpLimitAmount;
            _tmpLimitAmount = _cursor.getDouble(_cursorIndexOfLimitAmount);
            final String _tmpMonth;
            _tmpMonth = _cursor.getString(_cursorIndexOfMonth);
            final float _tmpAlertThreshold;
            _tmpAlertThreshold = _cursor.getFloat(_cursorIndexOfAlertThreshold);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _item = new BudgetEntity(_tmpId,_tmpCloudId,_tmpCategoryId,_tmpCategoryName,_tmpLimitAmount,_tmpMonth,_tmpAlertThreshold,_tmpIsDeleted,_tmpLastModified);
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
  public Object getForMonthOnce(final String month,
      final Continuation<? super List<BudgetEntity>> $completion) {
    final String _sql = "SELECT * FROM budgets WHERE isDeleted = 0 AND month = ? ORDER BY categoryName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, month);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCloudId = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudId");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCategoryName = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryName");
          final int _cursorIndexOfLimitAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitAmount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfAlertThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "alertThreshold");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final List<BudgetEntity> _result = new ArrayList<BudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BudgetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCloudId;
            _tmpCloudId = _cursor.getString(_cursorIndexOfCloudId);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpCategoryName;
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            final double _tmpLimitAmount;
            _tmpLimitAmount = _cursor.getDouble(_cursorIndexOfLimitAmount);
            final String _tmpMonth;
            _tmpMonth = _cursor.getString(_cursorIndexOfMonth);
            final float _tmpAlertThreshold;
            _tmpAlertThreshold = _cursor.getFloat(_cursorIndexOfAlertThreshold);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _item = new BudgetEntity(_tmpId,_tmpCloudId,_tmpCategoryId,_tmpCategoryName,_tmpLimitAmount,_tmpMonth,_tmpAlertThreshold,_tmpIsDeleted,_tmpLastModified);
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
  public Object getAllIncludingDeleted(final Continuation<? super List<BudgetEntity>> $completion) {
    final String _sql = "SELECT * FROM budgets";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCloudId = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudId");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCategoryName = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryName");
          final int _cursorIndexOfLimitAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitAmount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfAlertThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "alertThreshold");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final List<BudgetEntity> _result = new ArrayList<BudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BudgetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCloudId;
            _tmpCloudId = _cursor.getString(_cursorIndexOfCloudId);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpCategoryName;
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            final double _tmpLimitAmount;
            _tmpLimitAmount = _cursor.getDouble(_cursorIndexOfLimitAmount);
            final String _tmpMonth;
            _tmpMonth = _cursor.getString(_cursorIndexOfMonth);
            final float _tmpAlertThreshold;
            _tmpAlertThreshold = _cursor.getFloat(_cursorIndexOfAlertThreshold);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _item = new BudgetEntity(_tmpId,_tmpCloudId,_tmpCategoryId,_tmpCategoryName,_tmpLimitAmount,_tmpMonth,_tmpAlertThreshold,_tmpIsDeleted,_tmpLastModified);
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
  public Object getAllActive(final Continuation<? super List<BudgetEntity>> $completion) {
    final String _sql = "SELECT * FROM budgets WHERE isDeleted = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCloudId = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudId");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCategoryName = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryName");
          final int _cursorIndexOfLimitAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitAmount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfAlertThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "alertThreshold");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final List<BudgetEntity> _result = new ArrayList<BudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BudgetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCloudId;
            _tmpCloudId = _cursor.getString(_cursorIndexOfCloudId);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpCategoryName;
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            final double _tmpLimitAmount;
            _tmpLimitAmount = _cursor.getDouble(_cursorIndexOfLimitAmount);
            final String _tmpMonth;
            _tmpMonth = _cursor.getString(_cursorIndexOfMonth);
            final float _tmpAlertThreshold;
            _tmpAlertThreshold = _cursor.getFloat(_cursorIndexOfAlertThreshold);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _item = new BudgetEntity(_tmpId,_tmpCloudId,_tmpCategoryId,_tmpCategoryName,_tmpLimitAmount,_tmpMonth,_tmpAlertThreshold,_tmpIsDeleted,_tmpLastModified);
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
  public Object getById(final long id, final Continuation<? super BudgetEntity> $completion) {
    final String _sql = "SELECT * FROM budgets WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BudgetEntity>() {
      @Override
      @Nullable
      public BudgetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCloudId = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudId");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCategoryName = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryName");
          final int _cursorIndexOfLimitAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitAmount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfAlertThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "alertThreshold");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final BudgetEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCloudId;
            _tmpCloudId = _cursor.getString(_cursorIndexOfCloudId);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpCategoryName;
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            final double _tmpLimitAmount;
            _tmpLimitAmount = _cursor.getDouble(_cursorIndexOfLimitAmount);
            final String _tmpMonth;
            _tmpMonth = _cursor.getString(_cursorIndexOfMonth);
            final float _tmpAlertThreshold;
            _tmpAlertThreshold = _cursor.getFloat(_cursorIndexOfAlertThreshold);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _result = new BudgetEntity(_tmpId,_tmpCloudId,_tmpCategoryId,_tmpCategoryName,_tmpLimitAmount,_tmpMonth,_tmpAlertThreshold,_tmpIsDeleted,_tmpLastModified);
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
  public Object getByCloudId(final String cloudId,
      final Continuation<? super BudgetEntity> $completion) {
    final String _sql = "SELECT * FROM budgets WHERE cloudId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, cloudId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BudgetEntity>() {
      @Override
      @Nullable
      public BudgetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCloudId = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudId");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCategoryName = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryName");
          final int _cursorIndexOfLimitAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitAmount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfAlertThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "alertThreshold");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final BudgetEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCloudId;
            _tmpCloudId = _cursor.getString(_cursorIndexOfCloudId);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpCategoryName;
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            final double _tmpLimitAmount;
            _tmpLimitAmount = _cursor.getDouble(_cursorIndexOfLimitAmount);
            final String _tmpMonth;
            _tmpMonth = _cursor.getString(_cursorIndexOfMonth);
            final float _tmpAlertThreshold;
            _tmpAlertThreshold = _cursor.getFloat(_cursorIndexOfAlertThreshold);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _result = new BudgetEntity(_tmpId,_tmpCloudId,_tmpCategoryId,_tmpCategoryName,_tmpLimitAmount,_tmpMonth,_tmpAlertThreshold,_tmpIsDeleted,_tmpLastModified);
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
  public Object getByMonthAndCategory(final String month, final String categoryName,
      final Continuation<? super BudgetEntity> $completion) {
    final String _sql = "SELECT * FROM budgets WHERE isDeleted = 0 AND month = ? AND categoryName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, month);
    _argIndex = 2;
    _statement.bindString(_argIndex, categoryName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BudgetEntity>() {
      @Override
      @Nullable
      public BudgetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCloudId = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudId");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCategoryName = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryName");
          final int _cursorIndexOfLimitAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "limitAmount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfAlertThreshold = CursorUtil.getColumnIndexOrThrow(_cursor, "alertThreshold");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final BudgetEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCloudId;
            _tmpCloudId = _cursor.getString(_cursorIndexOfCloudId);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpCategoryName;
            _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            final double _tmpLimitAmount;
            _tmpLimitAmount = _cursor.getDouble(_cursorIndexOfLimitAmount);
            final String _tmpMonth;
            _tmpMonth = _cursor.getString(_cursorIndexOfMonth);
            final float _tmpAlertThreshold;
            _tmpAlertThreshold = _cursor.getFloat(_cursorIndexOfAlertThreshold);
            final boolean _tmpIsDeleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _result = new BudgetEntity(_tmpId,_tmpCloudId,_tmpCategoryId,_tmpCategoryName,_tmpLimitAmount,_tmpMonth,_tmpAlertThreshold,_tmpIsDeleted,_tmpLastModified);
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
