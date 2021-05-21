package com.btavares.comics.main.presentation.favorites.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.btavares.comics.R
import com.btavares.comics.app.delegate.observer
import com.btavares.comics.app.presentation.extension.setOnDebouncedClickListener
import com.btavares.comics.main.domain.model.ComicDomainModel
import kotlinx.android.synthetic.main.comic_item.view.*

internal class FavoritesComicAdapter: RecyclerView.Adapter<FavoritesComicAdapter.ViewHolder>() {

    var comics: List<ComicDomainModel> by observer(listOf()) {
        notifyDataSetChanged()
    }

    private var mOnDebouncedClickListener: ((comic : ComicDomainModel) -> Unit)? = null

    internal inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(comicDataModel: ComicDomainModel) {
            itemView.floatingCheck.visibility = View.GONE
            itemView.floatingUncheck.visibility = View.GONE
            itemView.tvSearchTitle.text = comicDataModel.title
            itemView.tvNumber.text = comicDataModel.number.toString()
            itemView.pvImage.load(comicDataModel.comicBitmap)

            itemView.ivArrow.setOnDebouncedClickListener { mOnDebouncedClickListener?.invoke(comicDataModel) }
            itemView.tvMore.setOnDebouncedClickListener { mOnDebouncedClickListener?.invoke(comicDataModel) }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comic_item, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = comics.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(comics[position])
    }

    fun setOnDebouncedClickListener(listener: (comic : ComicDomainModel) -> Unit) {
        this.mOnDebouncedClickListener = listener
    }

}