function toggleValueDefinition() {
	var selectedValue = $('#datatype').val();
	$('#datatype-specification>div').hide();
	$('#formula, #expression').removeAttr('required');

	if (selectedValue == 'expression') {
		$('#expression-form-group, #boolean-form-group').show();
		$('#expression').attr('required', true);
	} else if (selectedValue == 'formula') {
		$('#formula-form-group, #ucum-form-group, #range-form-group').show();
		$('#formula').attr('required', true);
	} else if (selectedValue == 'integer' || selectedValue == 'double') {
		$('#ucum-form-group, #range-form-group').show();
	} else if (selectedValue == 'string') {
		$('#enum-form-group').show();
	}
}

function toggleNewCategoryField() {
	if ($('#category').val() == 'new_category')
		$('#new-category-form-group').show();
	else $('#new-category-form-group').hide();
}

function addTextField(parent, name, placeholder) {
	$(parent).append('<input type="text" class="form-control" name="' + name + '" placeholder="' + placeholder + '">');
}

function addRow(id) {
	var row = $('#' + id + ' .row.hidden').clone();
	row.removeClass('hidden');
	$('#' + id).append(row);
}

function createPhenotypeTree(id, url) {
	$.getJSON(url, function(data) {
		data.forEach(function(node) {
			if (node.a_attr && node.a_attr.type === 'category' && node.children) {
				node.children.forEach(function(node) {
					node.icon = 'glyphicon glyphicon-leaf';
					node.a_attr.type = 'phenotype';
				})
			} else {
				node.icon = 'glyphicon glyphicon-leaf';
				node.a_attr.type = 'phenotype';
			} 
		});
		$('#' + id).jstree({
			core : {
				multiple : false,
				data : data
			},
			plugins : [ 'contextmenu', 'dnd' ],
			contextmenu : { items : customMenu }
		});
	});
	$(document).on('dnd_move.vakata', function (e, data) {
		var t = $(data.event.target);
		var attributes = data.element.attributes;
		if (!t.closest('.jstree').length) {
			if (t.closest('.drop').length && t.closest('.drop').hasClass(attributes.type.value)
				&& !(t.closest('.drop')[0].id === 'formula' && ['string', 'expression'].indexOf(attributes.datatype.value) != -1)
			) {
				data.helper.find('.jstree-icon').removeClass('jstree-er').addClass('jstree-ok');
			} else {
				data.helper.find('.jstree-icon').removeClass('jstree-ok').addClass('jstree-er');
			}
		}
	}).on('dnd_stop.vakata', function (e, data) {
		var t = $(data.event.target);
		var attributes = data.element.attributes;
		if (!t.closest('.jstree').length && t.closest('.drop').length && t.closest('.drop').hasClass(attributes.type.value)) {
			if (t.closest('.drop')[0].id === 'formula' && ['string', 'expression'].indexOf(attributes.datatype.value) != -1)
				return;
			t.closest('.drop').val(t.closest('.drop').val() + ' ' + data.element.text + ' ');
			focusInputEnd(t.closest('.drop'));
		}
	});
}

function toggleSuperPhenotype() {
	var div = $('#super-phenotype-div');
	if ($('#has-super-phenotype').prop('checked') === true) {
		div.removeClass('hidden');
	} else {
		$('#super-phenotype').val(null);
		if (!div.hasClass('hidden')) div.addClass('hidden');
	}
}

function customMenu(node) {
	var items = {
		asSuperPhenotypeItem : {
			label  : 'As Super-Phenotype',
			action : function() { $('#super-phenotype').val(node.text); $('#has-super-phenotype').prop('checked', true).change(); }
		},
		asCategory : {
			label  : 'As Category',
			action : function() { $('#category').val(node.text) }
		}
	};
	
	if (node.a_attr.type == 'category') {
		delete items.asSuperPhenotypeItem;
	} else {
		delete items.asCategory;
	}
	return items;
}

function focusInputEnd(input) {
	var length = input.val().length * 2;
	input.focus();
	input[0].setSelectionRange(length, length);
}