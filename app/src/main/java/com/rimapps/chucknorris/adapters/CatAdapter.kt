package com.rimapps.chucknorris.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rimapps.chucknorris.R
import com.rimapps.chucknorris.databinding.CellBinding
import com.rimapps.chucknorris.model.Category
import com.rimapps.chucknorris.model.Cell
import kotlinx.android.synthetic.main.category.view.*
import kotlinx.android.synthetic.main.cell.view.*
import android.content.Intent
import android.widget.PopupMenu

import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.rimapps.chucknorris.activities.PopUpActivity
import java.security.AccessController.getContext


class CatAdapter(private val category: ArrayList<Category>) : RecyclerView.Adapter<CatAdapter.ItemViewHolder>() {
    private val mContext: Context? = null
    class ItemViewHolder(var viewBinding: View) : RecyclerView.ViewHolder(viewBinding){
        var tvCat = viewBinding.tv_cat
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatAdapter.ItemViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.category, parent, false)
        return CatAdapter.ItemViewHolder(view)
    }
    override fun onBindViewHolder(holder: CatAdapter.ItemViewHolder, position: Int) {
        var item = category[position]
        holder.tvCat.text = item.category


        holder.itemView.setOnClickListener {
            val context: Context = holder.itemView.getContext()
            val intent = Intent(context, PopUpActivity::class.java)
            intent.putExtra("category", item)
            intent.putExtra("FROM", 1);
            context.startActivity(intent)
        }
    }

        override fun getItemCount(): Int  = category.size
}

private fun Intent.putExtra(s: String, get: Category) {

}
