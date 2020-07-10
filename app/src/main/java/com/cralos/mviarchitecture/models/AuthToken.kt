package com.cralos.mviarchitecture.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Esta es la tabla hija de AccountProperties, se relaciona con el campo 'account_pk'
 */

@Entity(
    tableName = "auth_token",
    foreignKeys = [
        ForeignKey(
            entity = AccountProperties::class,
            parentColumns = ["pk"],             // -> campo 'pk' en la Clase AccountProperties
            childColumns = ["account_pk"],      // -> campo 'account_pk' de esta clase
            onDelete = CASCADE
        )
    ]
)
data class AuthToken(

    @PrimaryKey
    @ColumnInfo(name = "account_pk")
    var account_pk: Int? = -1,

    @SerializedName("token")
    @Expose
    @ColumnInfo(name = "token")
    var token: String? = null

) {}