package com.kemas.gitgowayo_test.util

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.kemas.gitgowayo_test.domain.model.TvShow
import kotlinx.coroutines.test.TestDispatcher

suspend fun PagingData<TvShow>.collectDataForTest(
    dispatcher: TestDispatcher
): List<TvShow> {

    val differ = AsyncPagingDataDiffer(
        diffCallback = object : DiffUtil.ItemCallback<TvShow>() {
            override fun areItemsTheSame(
                oldItem: TvShow,
                newItem: TvShow
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: TvShow,
                newItem: TvShow
            ): Boolean = oldItem == newItem
        },
        updateCallback = NoopListUpdateCallback(),
        mainDispatcher = dispatcher,
        workerDispatcher = dispatcher
    )

    differ.submitData(this)

    return differ.snapshot().items
}

private class NoopListUpdateCallback : ListUpdateCallback {

    override fun onInserted(position: Int, count: Int) {}

    override fun onRemoved(position: Int, count: Int) {}

    override fun onMoved(fromPosition: Int, toPosition: Int) {}

    override fun onChanged(
        position: Int,
        count: Int,
        payload: Any?
    ) {}
}