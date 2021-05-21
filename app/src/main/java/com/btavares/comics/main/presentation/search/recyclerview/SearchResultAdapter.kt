package com.btavares.comics.main.presentation.search.recyclerview

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
import kotlinx.android.synthetic.main.search_fragment_item.view.*
import kotlinx.android.synthetic.main.search_item.view.*

internal class SearchResultAdapter: RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    var comics: MutableList<ComicDomainModel> by observer(mutableListOf()) {
        notifyDataSetChanged()
    }

    private var mOnDebouncedClickListener: ((comic : ComicDomainModel) -> Unit)? = null

    internal inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(comicDataModel: ComicDomainModel) {
            itemView.tvComicTitle.text = comicDataModel.title
            itemView.tvComicNumber.text = comicDataModel.number.toString()
            itemView.tvComicDescription.text = comicDataModel.description
            itemView.ivComic.load(comicDataModel.imageUrl)
            itemView.setOnDebouncedClickListener { mOnDebouncedClickListener?.invoke(comicDataModel) }
        }
    }

    fun clearData () {
        comics.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_fragment_item, parent,false)
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