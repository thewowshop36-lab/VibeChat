package com.example.vibechat.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vibechat.data.model.ChatMessage
import com.example.vibechat.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UserProfile::class, ChatMessage::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vibechat_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.userDao(), database.messageDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(userDao: UserDao, messageDao: MessageDao) {
            val now = System.currentTimeMillis()
            val hour = 3600 * 1000L
            val min = 60 * 1000L

            val demoUser = UserProfile(
                id = "user_me",
                username = "VibeExplorer",
                email = "user@vibechat.io",
                statusMessage = "Always vibing ✨",
                avatarColorHex = 0xFF00A884,
                isOnline = true,
                lastSeen = now
            )

            val contacts = listOf(
                UserProfile(
                    id = "user_alex",
                    username = "Alex Rivera",
                    email = "alex@vibechat.io",
                    statusMessage = "Coding the future 🚀",
                    avatarColorHex = 0xFF128C7E,
                    isOnline = true,
                    lastSeen = now
                ),
                UserProfile(
                    id = "user_sarah",
                    username = "Sarah Jenkins",
                    email = "sarah@vibechat.io",
                    statusMessage = "Designing vibrant interfaces 🎨",
                    avatarColorHex = 0xFF075E54,
                    isOnline = true,
                    lastSeen = now - 5 * min
                ),
                UserProfile(
                    id = "user_jordan",
                    username = "Jordan Chen",
                    email = "jordan@vibechat.io",
                    statusMessage = "Coffee first, questions later ☕",
                    avatarColorHex = 0xFF25D366,
                    isOnline = false,
                    lastSeen = now - 45 * min
                ),
                UserProfile(
                    id = "user_emma",
                    username = "Emma Watson",
                    email = "emma@vibechat.io",
                    statusMessage = "Out exploring the trails 🌲",
                    avatarColorHex = 0xFF34B7F1,
                    isOnline = true,
                    lastSeen = now
                ),
                UserProfile(
                    id = "user_priya",
                    username = "Priya Patel",
                    email = "priya@vibechat.io",
                    statusMessage = "In a meeting, ping for urgent 📱",
                    avatarColorHex = 0xFF9C27B0,
                    isOnline = false,
                    lastSeen = now - 3 * hour
                ),
                UserProfile(
                    id = "user_lucas",
                    username = "Lucas Miller",
                    email = "lucas@vibechat.io",
                    statusMessage = "Music is life 🎧",
                    avatarColorHex = 0xFFE91E63,
                    isOnline = true,
                    lastSeen = now
                )
            )

            userDao.insertUser(demoUser)
            userDao.insertUsers(contacts)

            val initialMessages = listOf(
                // Conversation with Alex
                ChatMessage(senderId = "user_alex", receiverId = "user_me", content = "Hey! Welcome to VibeChat! How do you like the interface?", createdAt = now - 2 * hour),
                ChatMessage(senderId = "user_me", receiverId = "user_alex", content = "It looks ultra sleek! The WhatsApp-inspired styling is so crisp.", createdAt = now - 1 * hour - 45 * min),
                ChatMessage(senderId = "user_alex", receiverId = "user_me", content = "Awesome! Real-time messaging, presence dots, and search work seamlessly!", createdAt = now - 1 * hour - 40 * min),

                // Conversation with Sarah
                ChatMessage(senderId = "user_sarah", receiverId = "user_me", content = "Did you check out the dark mode theme?", createdAt = now - 30 * min),
                ChatMessage(senderId = "user_me", receiverId = "user_sarah", content = "Yes! The deep dark emerald palette is super clean.", createdAt = now - 25 * min),
                ChatMessage(senderId = "user_sarah", receiverId = "user_me", content = "Glad you love it! Let me know if you need any adjustments ✨", createdAt = now - 20 * min),

                // Conversation with Jordan
                ChatMessage(senderId = "user_jordan", receiverId = "user_me", content = "Hey, are you joining the team call later?", createdAt = now - 50 * min)
            )

            messageDao.insertMessages(initialMessages)
        }
    }
}
