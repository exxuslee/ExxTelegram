package com.exxxuslee.exxtelegram.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.exxxuslee.exxtelegram.databinding.RecyclerChatsBinding
import org.drinkless.td.libcore.telegram.TdApi.Chat

class ChatsAdapter:  RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    private var chats: List<Chat> = listOf()
    var onCheckClickListener: ((Int) -> Unit)? = null

    inner class ViewHolder(val binding: RecyclerChatsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            RecyclerChatsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.text = chats[position].title
    }

    override fun getItemCount() = chats.size

    fun updateAdapter(newChats: List<Chat>) {
        val toDoDiffUtil = DiffCallBack(chats, newChats)
        val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil)
        chats = newChats
        toDoDiffResult.dispatchUpdatesTo(this)
    }
}
