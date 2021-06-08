package com.capstone.fresco.core.model

import com.google.gson.annotations.SerializedName

data class FruitResponse(

	@field:SerializedName("genus")
	val genus: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("family")
	val family: String? = null,

	@field:SerializedName("order")
	val order: String? = null,

	@field:SerializedName("nutritions")
	val nutritions: Nutritions? = null
)

data class Nutritions(

	@field:SerializedName("carbohydrates")
	val carbohydrates: Double? = null,

	@field:SerializedName("protein")
	val protein: Double? = null,

	@field:SerializedName("fat")
	val fat: Double? = null,

	@field:SerializedName("calories")
	val calories: Int? = null,

	@field:SerializedName("sugar")
	val sugar: Double? = null
)
