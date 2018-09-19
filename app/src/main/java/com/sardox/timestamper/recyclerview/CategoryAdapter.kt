package com.sardox.timestamper.recyclerview

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.sardox.timestamper.R
import com.sardox.timestamper.objects.Category
import com.sardox.timestamper.utils.CategoryUpdatesInterface
import com.sardox.timestamper.utils.TimestampIcon

class CategoryAdapter(private val categories: List<Category>, private val categoryChangedCallback: CategoryUpdatesInterface, private val icons: List<TimestampIcon>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val CATEGORY_ITEM = 0
    private val ADD_BUTTON_ITEM = 1

    private var selectedCategory = Category.Default

    fun selectedCategoryPosition(): Int {
        val pos = categories.indexOf(selectedCategory)
        return if (pos == -1)
            0
        else
            pos
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CATEGORY_ITEM -> {
                CategoryViewHolder(LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.recyclerview_category, viewGroup,
                                false))
            }
            else -> {
                AddCategoryButtonViewHolder(LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.recyclerview_category_add_button, viewGroup,
                                false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            CATEGORY_ITEM -> {
                val category = categories[position]
                val categoryViewHolder = holder as CategoryViewHolder
                categoryViewHolder.mCategoryTextView.text = category.name
                categoryViewHolder.categoryIcon.setImageDrawable(ContextCompat.getDrawable(holder.categoryIcon.context, icons[category.icon_id].drawable_id))
                categoryViewHolder.categoryContainer.isSelected = selectedCategory.categoryID == category.categoryID

                if (selectedCategory.categoryID == category.categoryID) {
                    categoryViewHolder.categoryContainer.isSelected = true
                    categoryViewHolder.categoryRemove.visibility = View.VISIBLE
                } else {
                    categoryViewHolder.categoryContainer.isSelected = false
                    categoryViewHolder.categoryRemove.visibility = View.GONE
                }
            }
            ADD_BUTTON_ITEM -> {
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (categories.size == position) {
            ADD_BUTTON_ITEM
        } else {
            CATEGORY_ITEM
        }
    }

    fun setSelectedCategory(selectedCategory: Category) {
        this.selectedCategory = selectedCategory
    }

    override fun getItemCount(): Int {
        return categories.size + 1
    }

    internal inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var mCategoryTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        var categoryIcon: ImageView = itemView.findViewById(R.id.categoryImage)
        var categoryRemove: ImageView = itemView.findViewById(R.id.recycler_menu_remove)
        var categoryContainer: LinearLayout = itemView.findViewById(R.id.categoryContainer)

        init {
            mCategoryTextView.setOnClickListener(this)
            categoryRemove.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            when (view) {
                mCategoryTextView -> {
                    selectedCategory = categories[adapterPosition]
                    categoryChangedCallback.onCategoryChanged(selectedCategory)
                    notifyDataSetChanged()
                }
                categoryRemove -> categoryChangedCallback.onCategoryRemove(selectedCategory)
            }
        }
    }

    internal inner class AddCategoryButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val mCategoryAdd: TextView = itemView.findViewById(R.id.category_item_button)

        init {
            mCategoryAdd.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            categoryChangedCallback.onCategoryAdded()
        }
    }
}