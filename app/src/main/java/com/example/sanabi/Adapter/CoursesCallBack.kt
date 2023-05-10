package com.example.sanabi.Adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.sanabi.model.BasketProductModelData


class CoursesCallBack(
    private val oldList: List<BasketProductModelData>,
    private val newList: List<BasketProductModelData>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldList[oldItemPosition].amount != newList[newItemPosition].amount->{
                false
            }
            oldList[oldItemPosition].id != newList[newItemPosition].id->{
                false
            }
            oldList[oldItemPosition].price != newList[newItemPosition].price->{
                false
            }else-> true
        }
    }
}