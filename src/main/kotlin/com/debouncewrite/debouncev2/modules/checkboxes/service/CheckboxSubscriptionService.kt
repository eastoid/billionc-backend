package com.debouncewrite.debouncev2.modules.checkboxes.service

import org.springframework.stereotype.Service

@Service
class CheckboxSubscriptionService {

    private val idToUsers = mutableMapOf<Int, MutableSet<Int>>()
    private val userToIds = mutableMapOf<Int, MutableSet<Int>>()
    private val userToCount = mutableMapOf<Int, Int>() // Counter for each user's subscriptions

    // Subscribe a user to a chunk
    fun subscribe(userId: Int, id: Int) {
        val subCount = userToCount[userId]
        if (subCount != null && subCount > 250) {
            val subscriptions = userToIds[userId] ?: return unsubscribeAll(userId)

        }
        idToUsers.computeIfAbsent(id) { mutableSetOf() }.add(userId)
        if (userToIds.computeIfAbsent(userId) { mutableSetOf() }.add(id)) {
            // Increment counter only if the subscription is new
            userToCount[userId] = userToCount.getOrDefault(userId, 0) + 1
        }
    }

    // Unsubscribe a user from a chunk
    fun unsubscribe(userId: Int, id: Int) {
        idToUsers[id]?.remove(userId)
        if (idToUsers[id]?.isEmpty() == true) idToUsers.remove(id)

        if (userToIds[userId]?.remove(id) == true) {
            // Decrement counter only if the subscription existed
            userToCount[userId] = userToCount.getOrDefault(userId, 0) - 1
            if (userToIds[userId]?.isEmpty() == true) {
                userToIds.remove(userId)
                userToCount.remove(userId) // Remove counter when user has no subscriptions
            }
        }
    }

    // Unsubscribe a user from all chunks
    fun unsubscribeAll(userId: Int) {
        val subscribedIds = userToIds.remove(userId) ?: return
        for (id in subscribedIds) {
            idToUsers[id]?.remove(userId)
            if (idToUsers[id]?.isEmpty() == true) idToUsers.remove(id)
        }
        userToCount.remove(userId) // Reset counter for the user
    }

    // Get all users subscribed to a chunk
    fun getSubscribedUsers(id: Int): Set<Int> = idToUsers[id] ?: emptySet()

    // Get all chunks a user is subscribed to
    fun getSubscribedChunks(userId: Int): Set<Int> = userToIds[userId] ?: emptySet()

    // Get the number of chunks a user is subscribed to
    fun getSubscriptionCount(userId: Int): Int = userToCount.getOrDefault(userId, 0)
}
