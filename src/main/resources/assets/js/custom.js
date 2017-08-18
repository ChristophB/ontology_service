function toggleValueDefinition() {
	$('#ucum-form-group, #is-decimal-form-group, #formula-form-group').addClass('hidden');

	if ($('#datatype').val() == 'numeric' || $('#datatype').val() == 'calculated')
		$('#ucum-form-group, #is-decimal-form-group').removeClass('hidden');
	if ($('#datatype').val() == 'calculated')
	    $('#formula-form-group').removeClass('hidden');
}

function addTextField(parent, name, placeholder) {
	$(parent).append('<input type="text" class="form-control" name="' + name + '" placeholder="' + placeholder + '">');
}

function addRow(id) {
	var row = $('form:not(.hidden) ' + id + ' .hidden').clone();
	row.removeClass('hidden');
	$('form:not(.hidden) ' + id).append(row);
}

function preProcessPhenotype(node) {
	if (node.children) {
		node.children.forEach(preProcessPhenotype);
	}
	if (node.a_attr.type != 'category')	node.icon = 'glyphicon glyphicon-leaf';
}

function transformToPhenotypeTree(node) {
	var tree = { text: node.name, children: [], a_attr: { id: node.name, type: null } };

	if (node.category != null) {
	    if (node.category.abstractBooleanPhenotype || node.category.abstractCalculationPhenotype || node.category.abstractSinglePhenotype)
	        tree.icon = 'glyphicon glyphicon-leaf text-primary';
	    else if (node.category.restrictedBooleanPhenotype || node.category.restrictedCalculationPhenotype || node.category.restrictedSinglePhenotype)
	        tree.icon = 'glyphicon glyphicon-leaf text-success';
	}
	
	node.children.forEach(function(child) {
		tree.children.push(transformToPhenotypeTree(child));
	});
	
	return tree;
}

function createPhenotypeTree(id, url) {
	$.getJSON(url, function(data) {
	    var tree = transformToPhenotypeTree(data);
	    tree.state = { opened : true };

		$('#' + id).jstree({
			core : {
				multiple : false,
				data : tree
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
	var help = $('#has-super-phenotype-help');
	if ($('#has-super-phenotype').prop('checked') === true) {
		div.removeClass('hidden');
		if (!help.hasClass('hidden')) help.addClass('hidden');
	} else {
		$('#super-phenotype').val(null);
		if (!div.hasClass('hidden')) div.addClass('hidden');
		help.removeClass('hidden');
	}
}

function hidePhenotypeForms() {
    $('#abstract-phenotype-form, #phenotype-category-form').addClass('hidden');
    $('#numeric-phenotype-form, #string-phenotype-form, #date-phenotype-form').addClass('hidden');
    $('#calculated-phenotype-form, #boolean-phenotype-form').addClass('hidden');
}

function customMenu(node) {
	var items = {
		showCategoryForm : {
		    label : 'Create Sub Category',
		    action : function() {
		        hidePhenotypeForms();
		        $('#phenotype-category-form').removeClass('hidden');
		        $('#super-category').val(node.text);
		    }
		},
		showAbstractPhenotypeForm : {
		    label : 'Create Abstract Phenotype',
		    action : function() {
		        hidePhenotypeForms();
		        $('#abstract-phenotype-form').removeClass('hidden');
		        $('#categories').val(node.text);
		    }
		},
		showRestrictedPhenotypeForm : {
		    label : 'Create Restricted Phenotype',
		    action : function() {
		        hidePhenotypeForms();
		        switch (node.a_attr.type) {
		            case 'XSD_STRING': $('#string-phenotype-form').removeClass('hidden'); break;
		            case 'XSD_INTEGER': $('#numeric-phenotype-form').removeClass('hidden'); break;
		            case 'XSD_DOUBLE': $('#numeric-phenotype-form').removeClass('hidden'); break;
		            case 'XSD_BOOLEAN': $('#boolean-phenotype-form').removeClass('hidden'); break;
		            case 'formula': $('#formula-phenotype-form').removeClass('hidden'); break; // TODO: node type is null
		            default: return;
		        }

                $('#super-phenotype').val(node.text);
		    }
		}
	};
	
	if (node.a_attr.type == null) {
		delete items.showRestrictedPhenotypeForm;
	} else {
		delete items.showCategoryForm;
		delete items.showAbstractPhenotypeForm;
	}
	return items;
}

function focusInputEnd(input) {
	var length = input.val().length * 2;
	input.focus();
	input[0].setSelectionRange(length, length);
}