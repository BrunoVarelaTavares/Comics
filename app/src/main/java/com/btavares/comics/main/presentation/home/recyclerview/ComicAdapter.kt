package com.btavares.comics.main.presentation.home.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.btavares.comics.R
import com.btavares.comics.app.delegate.observer
import com.btavares.comics.app.presentation.extension.setOnDebouncedClickListener
import com.btavares.comics.main.domain.model.ComicDomainModel
import kotlinx.android.synthetic.main.comic_item.view.*

internal class ComicAdapter: RecyclerView.Adapter<ComicAdapter.ViewHolder>() {

    var comics: List<ComicDomainModel> by observer(listOf()) {
        notifyDataSetChanged()
    }

    private var mOnDebouncedClickListener: ((comic : ComicDomainModel) -> Unit)? = null
    private var mOnAddFavoriteClickListener: ((comic : ComicDomainModel) -> Unit)? = null
    private var mOnRemoveFavoriteClickListener: ((comic : ComicDomainModel) -> Unit)? = null

    internal inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(comicDomainModel: ComicDomainModel) {
            itemView.tvSearchTitle.text = comicDomainModel.title
            itemView.tvNumber.text = comicDomainModel.number.toString()
            itemView.pvImage.load(comicDomainModel.imageUrl)
            if (comicDomainModel.favorite) {
                itemView.floatingUncheck.isVisible = false
                itemView.floatingCheck.isVisible = true
            } else {
                itemView.floatingCheck.isVisible = false
                itemView.floatingUncheck.isVisible = true
            }

            itemView.floatingUncheck.setOnDebouncedClickListener {
                comicDomainModel.favorite = true
                mOnAddFavoriteClickListener?.invoke(comicDomainModel)
            }

            itemView.floatingCheck.setOnDebouncedClickListener {
                comicDomainModel.favorite = false
                mOnRemoveFavoriteClickListener?.invoke(comicDomainModel)
            }

            itemView.ivArrow.setOnDebouncedClickListener { mOnDebouncedClickListener?.invoke(comicDomainModel) }
            itemView.tvMore.setOnDebouncedClickListener { mOnDebouncedClickListener?.invoke(comicDomainModel) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comic_item, parent,false)
        return ViewHolder(view)
    }

    fun updateData(notify : Boolean) {
        if(notify)
            notifyDataSetChanged()
    }

    override fun getItemCount(): Int = comics.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(comics[position])
    }

    fun setOnDebouncedClickListener(listener: (comic : ComicDomainModel) -> Unit) {
        this.mOnDebouncedClickListener = listener
    }

    fun setOnAddFavoriteDebouncedClickListener(listener: (comic : ComicDomainModel) -> Unit) {
        this.mOnAddFavoriteClickListener = listener
    }

    fun setOnRemoveFavoriteDebouncedClickListener(listener: (comic : ComicDomainModel) -> Unit) {
        this.mOnRemoveFavoriteClickListener = listener
    }




}