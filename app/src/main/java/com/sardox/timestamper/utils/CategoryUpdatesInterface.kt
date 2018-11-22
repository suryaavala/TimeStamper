package com.sardox.timestamper.utils

import com.sardox.timestamper.objects.Category

interface CategoryUpdatesInterface {
    fun onCategoryChanged(selectedCategory: Category)
    fun onCategoryAdded()
    fun onCategoryRemove(selectedCategory: Category)
}
