package com.mikeschvedov.fooddiary.Data.Repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mikeschvedov.fooddiary.Presentation.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepository {

    companion object {

        /* ------------- Using DataStore ------------- */
        private val dateWhenInstalled = stringPreferencesKey("dateWhenInstalled")
        private val isDateAlreadyCreated = booleanPreferencesKey("isDateAlreadyCreated")

        suspend fun setIsFirstDateExists(context: Context, bool: Boolean) {
            context.dataStore.edit { preference ->
                // Then we save the string into to "dateWhenInstalled" variable
                preference[isDateAlreadyCreated] = bool
            }
        }

        fun readIsFirstDateExists(context: Context) : Flow<Boolean>{
            return context.dataStore.data
                .map { preferences ->
                    preferences[isDateAlreadyCreated] ?: false
                }
        }


        suspend fun setFirstDate(context: Context, dateAsString: String) {
            context.dataStore.edit { preference ->
                // We save the string into to "dateWhenInstalled" variable
                preference[dateWhenInstalled] = dateAsString
            }
        }

        fun readFirstDate(context: Context) : Flow<String>{
            return context.dataStore.data
                .map { preferences ->
                    preferences[dateWhenInstalled] ?: "empty"
                }
        }

    }
}