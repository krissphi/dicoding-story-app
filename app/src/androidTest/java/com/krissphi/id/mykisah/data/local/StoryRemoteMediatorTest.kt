package com.krissphi.id.mykisah.data.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.krissphi.id.mykisah.data.remote.api.ApiService
import com.krissphi.id.mykisah.data.remote.response.LoginResponse
import com.krissphi.id.mykisah.data.remote.response.LoginResult
import com.krissphi.id.mykisah.data.remote.response.RegisterResponse
import com.krissphi.id.mykisah.data.remote.response.StoryCreateResponse
import com.krissphi.id.mykisah.data.remote.response.StoryDetailResponse
import com.krissphi.id.mykisah.data.remote.response.StoryItem
import com.krissphi.id.mykisah.data.remote.response.StoryResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, StoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {

    override suspend fun getStories(page: Int, size: Int): StoryResponse {
        val items = mutableListOf<StoryItem>()
        for (i in 0..100) {
            val story = StoryItem(
                id = "story-id-$i",
                name = "User $i",
                description = "Description for story $i",
                photoUrl = "https://example.com/photo$i.jpg",
                createdAt = "2023-01-01T00:00:00Z",
                lat = 0.0,
                lon = 0.0
            )
            items.add(story)
        }

        val startIndex = (page - 1) * size
        val endIndex = minOf(startIndex + size, items.size)
        val sublist = if (startIndex >= items.size) emptyList() else items.subList(startIndex, endIndex)

        return StoryResponse(error = false, message = "Stories fetched successfully", listStory = sublist)
    }

    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return RegisterResponse(error = false, message = "User Created")
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        val loginResult = LoginResult(
            userId = "user-123",
            name = "Fake User",
            token = "fake-token-for-testing"
        )
        return LoginResponse(loginResult = loginResult, error = false, message = "success")
    }

    override suspend fun getDetailStory(id: String): StoryDetailResponse {
        val story = StoryItem(
            id = id,
            name = "User Detail",
            description = "Description for detail story.",
            photoUrl = "https://example.com/photo_detail.jpg",
            createdAt = "2023-01-01T00:00:00Z",
            lat = -6.175392,
            lon = 106.827153
        )
        return StoryDetailResponse(error = false, message = "Story fetched successfully", story = story)
    }

    override suspend fun createNewStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): StoryCreateResponse {
        return StoryCreateResponse(error = false, message = "success")
    }

    override suspend fun getStoriesWithLocation(location: Int): StoryResponse {
        val items = mutableListOf<StoryItem>()
        for (i in 0..10) {
            val story = StoryItem(
                id = "story-loc-$i",
                name = "User Loc $i",
                description = "Story with location $i",
                photoUrl = "https://example.com/photo_loc.jpg",
                createdAt = "2023-01-01T00:00:00Z",
                lat = -6.175392 + (i * 0.01),
                lon = 106.827153 + (i * 0.01)
            )
            items.add(story)
        }
        return StoryResponse(error = false, message = "Stories with location fetched successfully", listStory = items)
    }
}