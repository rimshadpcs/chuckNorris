package com.rimapps.chucknorris.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rimapps.chucknorris.model.Cell
import com.rimapps.chucknorris.R
import kotlinx.android.synthetic.main.cell.view.*

class RVAdapter(private val cell: ArrayList<Cell>) : RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {

    class ItemViewHolder(var viewBinding: View) : RecyclerView.ViewHolder(viewBinding){
        var tvvv = viewBinding.tv_search_joke
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.cell, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var item = cell[position]
        holder.tvvv.text = item.joke
    }

    override fun getItemCount(): Int  = cell.size

}