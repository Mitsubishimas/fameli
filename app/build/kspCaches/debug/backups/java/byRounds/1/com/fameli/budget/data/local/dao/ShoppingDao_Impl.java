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
import com.fameli.budget.data.local.entity.ShoppingItemEntity;
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
public final class ShoppingDao_Impl implements ShoppingDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ShoppingItemEntity> __insertionAdapterOfShoppingItemEntity;

  private final EntityDeletionOrUpdateAdapter<ShoppingItemEntity> __updateAdapterOfShoppingItemEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkPurchased;

  private final SharedSQLiteStatement __preparedStmtOfMarkUnpurchased;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  public ShoppingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfShoppingItemEntity = new EntityInsertionAdapter<ShoppingItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `shopping_items` (`id`,`cloudId`,`name`,`quantity`,`isPurchased`,`purchasedByUid`,`purchasedByName`,`purchasedAt`,`createdByUid`,`createdByName`,`createdAt`,`isDeleted`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ShoppingItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCloudId());
        statement.bindString(3, entity.getName());
        statement.bindLong(4, entity.getQuantity());
        final int _tmp = entity.isPurchased() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindString(6, entity.getPurchasedByUid());
        statement.bindString(7, entity.getPurchasedByName());
        statement.bindLong(8, entity.getPurchasedAt());
        statement.bindString(9, entity.getCreatedByUid());
        statement.bindString(10, entity.getCreatedByName());
        statement.bindLong(11, entity.getCreatedAt());
        final int _tmp_1 = entity.isDeleted() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
      }
    };
    this.__updateAdapterOfShoppingItemEntity = new EntityDeletionOrUpdateAdapter<ShoppingItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `shopping_items` SET `id` = ?,`cloudId` = ?,`name` = ?,`quantity` = ?,`isPurchased` = ?,`purchasedByUid` = ?,`purchasedByName` = ?,`purchasedAt` = ?,`createdByUid` = ?,`createdByName` = ?,`createdAt` = ?,`isDeleted` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ShoppingItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCloudId());
        statement.bindString(3, entity.getName());
        statement.bindLong(4, entity.getQuantity());
        final int _tmp = entity.isPurchased() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindString(6, entity.getPurchasedByUid());
        statement.bindString(7, entity.getPurchasedByName());
        statement.bindLong(8, entity.getPurchasedAt());
        statement.bindString(9, entity.getCreatedByUid());
        statement.bindString(10, entity.getCreatedByName());
        statement.bindLong(11, entity.getCreatedAt());
        final int _tmp_1 = entity.isDeleted() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfMarkPurchased = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE shopping_items SET isPurchased = 1, purchasedByUid = ?, purchasedByName = ?, purchasedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkUnpurchased = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE shopping_items SET isPurchased = 0, purchasedByUid = '', purchasedByName = '', purchasedAt = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE shopping_items SET isDeleted = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ShoppingItemEntity item,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfShoppingItemEntity.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ShoppingItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfShoppingItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markPurchased(final long id, final String uid, final String userName,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkPurchased.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, uid);
        _argIndex = 2;
        _stmt.bindString(_argIndex, userName);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfMarkPurchased.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markUnpurchased(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkUnpurchased.acquire();
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
          __preparedStmtOfMarkUnpurchased.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object softDelete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDelete.acquire();
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
          __preparedStmtOfSoftDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ShoppingItemEntity>> getAll() {
    final String _sql = "SELECT * FROM shopping_items WHERE isDeleted = 0 ORDER BY isPurchased ASC, createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"shopping_items"}, new Callable<List<ShoppingItemEntity>>() {
      @Override
      @NonNull
      public List<ShoppingItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCloudId = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfIsPurchased = CursorUtil.getColumnIndexOrThrow(_cursor, "isPurchased");
          final int _cursorIndexOfPurchasedByUid = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedByUid");
          final int _cursorIndexOfPurchasedByName = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedByName");
          final int _cursorIndexOfPurchasedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedAt");
          final int _cursorIndexOfCreatedByUid = CursorUtil.getColumnIndexOrThrow(_cursor, "createdByUid");
          final int _cursorIndexOfCreatedByName = CursorUtil.getColumnIndexOrThrow(_cursor, "createdByName");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final List<ShoppingItemEntity> _result = new ArrayList<ShoppingItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ShoppingItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCloudId;
            _tmpCloudId = _cursor.getString(_cursorIndexOfCloudId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final boolean _tmpIsPurchased;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPurchased);
            _tmpIsPurchased = _tmp != 0;
            final String _tmpPurchasedByUid;
            _tmpPurchasedByUid = _cursor.getString(_cursorIndexOfPurchasedByUid);
            final String _tmpPurchasedByName;
            _tmpPurchasedByName = _cursor.getString(_cursorIndexOfPurchasedByName);
            final long _tmpPurchasedAt;
            _tmpPurchasedAt = _cursor.getLong(_cursorIndexOfPurchasedAt);
            final String _tmpCreatedByUid;
            _tmpCreatedByUid = _cursor.getString(_cursorIndexOfCreatedByUid);
            final String _tmpCreatedByName;
            _tmpCreatedByName = _cursor.getString(_cursorIndexOfCreatedByName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsDeleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_1 != 0;
            _item = new ShoppingItemEntity(_tmpId,_tmpCloudId,_tmpName,_tmpQuantity,_tmpIsPurchased,_tmpPurchasedByUid,_tmpPurchasedByName,_tmpPurchasedAt,_tmpCreatedByUid,_tmpCreatedByName,_tmpCreatedAt,_tmpIsDeleted);
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
  public Object getByCloudId(final String cloudId,
      final Continuation<? super ShoppingItemEntity> $completion) {
    final String _sql = "SELECT * FROM shopping_items WHERE cloudId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, cloudId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ShoppingItemEntity>() {
      @Override
      @Nullable
      public ShoppingItemEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCloudId = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfIsPurchased = CursorUtil.getColumnIndexOrThrow(_cursor, "isPurchased");
          final int _cursorIndexOfPurchasedByUid = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedByUid");
          final int _cursorIndexOfPurchasedByName = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedByName");
          final int _cursorIndexOfPurchasedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedAt");
          final int _cursorIndexOfCreatedByUid = CursorUtil.getColumnIndexOrThrow(_cursor, "createdByUid");
          final int _cursorIndexOfCreatedByName = CursorUtil.getColumnIndexOrThrow(_cursor, "createdByName");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsDeleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isDeleted");
          final ShoppingItemEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCloudId;
            _tmpCloudId = _cursor.getString(_cursorIndexOfCloudId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final boolean _tmpIsPurchased;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPurchased);
            _tmpIsPurchased = _tmp != 0;
            final String _tmpPurchasedByUid;
            _tmpPurchasedByUid = _cursor.getString(_cursorIndexOfPurchasedByUid);
            final String _tmpPurchasedByName;
            _tmpPurchasedByName = _cursor.getString(_cursorIndexOfPurchasedByName);
            final long _tmpPurchasedAt;
            _tmpPurchasedAt = _cursor.getLong(_cursorIndexOfPurchasedAt);
            final String _tmpCreatedByUid;
            _tmpCreatedByUid = _cursor.getString(_cursorIndexOfCreatedByUid);
            final String _tmpCreatedByName;
            _tmpCreatedByName = _cursor.getString(_cursorIndexOfCreatedByName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsDeleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsDeleted);
            _tmpIsDeleted = _tmp_1 != 0;
            _result = new ShoppingItemEntity(_tmpId,_tmpCloudId,_tmpName,_tmpQuantity,_tmpIsPurchased,_tmpPurchasedByUid,_tmpPurchasedByName,_tmpPurchasedAt,_tmpCreatedByUid,_tmpCreatedByName,_tmpCreatedAt,_tmpIsDeleted);
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
