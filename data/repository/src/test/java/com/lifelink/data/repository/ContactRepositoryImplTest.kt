package com.lifelink.data.repository

import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseUser
import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.local.dao.ContactDao
import com.lifelink.data.local.entity.ContactEntity
import com.lifelink.data.remote.FirebaseAuthSource
import com.lifelink.data.remote.FirestoreSyncSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Demonstrates the module's testing convention: DAO and remote sources are mocked so the
 * repository's business logic (offline-first write, sync-marks-as-synced) is verified in
 * isolation, with zero real Room/Firestore instances involved.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ContactRepositoryImplTest {

    private val dao: ContactDao = mock()
    private val remote: FirestoreSyncSource = mock()
    private val authSource: FirebaseAuthSource = mock()

    private lateinit var repository: ContactRepositoryImpl

    @Before
    fun setUp() {
        repository = ContactRepositoryImpl(dao, remote, authSource)
    }

    @Test
    fun `upsertContact saves locally with isSynced false`() = runTest {
        val contact = Contact(id = "1", name = "Amma", phoneNumber = "9999999999", relationship = "Mother", priority = 0)

        val result = repository.upsertContact(contact)

        assertThat(result).isInstanceOf(LifeLinkResult.Success::class.java)

        val captor = argumentCaptor<ContactEntity>()
        verify(dao).upsert(captor.capture())
        val saved = captor.firstValue
        assertThat(saved.id).isEqualTo("1")
        assertThat(saved.name).isEqualTo("Amma")
        assertThat(saved.isSynced).isFalse()
    }

    @Test
    fun `hasAtLeastOneContact returns false when dao count is zero`() = runTest {
        whenever(dao.count()).thenReturn(0)

        assertThat(repository.hasAtLeastOneContact()).isFalse()
    }

    @Test
    fun `syncPendingContacts marks entities synced after successful upload`() = runTest {
        val entity = ContactEntity("1", "Amma", "999", "Mother", 0, updatedAt = 100L, isSynced = false)
        val user: FirebaseUser = mock()
        whenever(user.uid).thenReturn("uid-1")
        whenever(authSource.currentUser).thenReturn(user)
        whenever(dao.getUnsynced()).thenReturn(listOf(entity))

        val result = repository.syncPendingContacts()

        assertThat(result).isInstanceOf(LifeLinkResult.Success::class.java)
        verify(remote).uploadContact(uid = "uid-1", id = "1", data = any())
        verify(dao).upsert(entity.copy(isSynced = true))
    }

    @Test
    fun `syncPendingContacts returns error when not signed in`() = runTest {
        whenever(authSource.currentUser).thenReturn(null)

        val result = repository.syncPendingContacts()

        assertThat(result).isInstanceOf(LifeLinkResult.Error::class.java)
    }
}
