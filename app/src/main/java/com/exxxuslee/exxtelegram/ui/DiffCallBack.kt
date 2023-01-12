package com.exxxuslee.exxtelegram.ui

import androidx.recyclerview.widget.DiffUtil
import org.drinkless.td.libcore.telegram.TdApi

class DiffCallBack(
    private val oldList: List<TdApi.Chat>,
    private val newList: List<TdApi.Chat>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}