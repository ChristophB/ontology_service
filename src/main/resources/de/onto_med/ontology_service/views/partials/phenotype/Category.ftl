<div class="form-group row">
	<label for="category" class="control-label col-sm-2">Category</label>
	<div class="col-sm-3">
		<select id="category" name="category" class="form-control" onchange="toggleNewCategoryField()">
			<option />
			<option value="new_category">Create new Category</option>
			<option value="Category_1">Category 1</option>
			<option value="Category_2">Category 2</option>
		</select>
	</div>
	<div id="new-category-div" class="col-sm-6 hidden">
		<input type="text" id="new-category" name="new-category" class="form-control" placeholder="New Category Name">
	</div>
</div>