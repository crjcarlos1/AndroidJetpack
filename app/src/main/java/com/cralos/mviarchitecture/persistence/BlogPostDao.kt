package com.cralos.mviarchitecture.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cralos.mviarchitecture.models.BlogPost
import com.cralos.mviarchitecture.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface BlogPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogPost: BlogPost): Long

    @Query(
        """
        SELECT * FROM blog_post 
        WHERE title LIKE '%' || :query || '%' 
        OR body LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        LIMIT (:page * :pageSize)
        """
    )
    fun getAllBlogPosts(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): LiveData<List<BlogPost>>

}