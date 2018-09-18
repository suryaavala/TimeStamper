package com.sardox.timestamper.utils

import com.sardox.timestamper.objects.Category

interface CategoryUpdatesInterface {
    fun onCategoryChanges(selectedCategory: Category)
    fun onCategoryAdded()
}
