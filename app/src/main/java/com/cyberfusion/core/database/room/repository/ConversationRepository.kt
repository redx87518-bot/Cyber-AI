package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.ConversationDao
import com.cyberfusion.core.database.room.entity.ConversationEntity
import com.cyberfusion.core.database.room.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ConversationRepository(private val conversationDao: ConversationDao) {
    fun getAllConversations(): Flow<List<ConversationEntity>> = conversationDao.getAllConversations()
    suspend fun getConversationById(id: Long): ConversationEntity? = conversationDao.getConversationById(id)
    suspend fun getActiveConversation(): ConversationEntity? = conversationDao.getActiveConversation()
    
    suspend fun createConversation(title: String): Long {
        val conversationId = conversationDao.insertConversation(
            ConversationEntity(title = title, isActive = true)
        )
        conversationDao.deactivateOtherConversations(conversationId)
        return conversationId
    }
    
    suspend fun setActiveConversation(conversationId: Long) {
        conversationDao.deactivateOtherConversations(conversationId)
    }
    
    fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>> = 
        conversationDao.getMessagesForConversation(conversationId)
    
    suspend fun insertMessage(message: MessageEntity): Long = conversationDao.insertMessage(message)
    
    suspend fun deleteConversation(conversationId: Long) {
        conversationDao.deleteMessagesForConversation(conversationId)
    }
}
