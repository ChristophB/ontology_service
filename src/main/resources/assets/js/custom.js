function toggleValueDefinition() {
	$('#ucum-form-group, #is-decimal-form-group, #formula-form-group').addClass('hidden');

	if ($('#datatype').val() == 'numeric' || $('#datatype').val() == 'calculation')
		$('#ucum-form-group, #is-decimal-form-group').removeClass('hidden');
	if ($('#datatype').val() == 'calculation')
	    $('#formula-form-group').removeClass('hidden');
}

function addRow(id) {
	var row = $('form:not(.hidden) ' + id + ' .hidden').clone();
	row.removeClass('hidden');
	$('form:not(.hidden) ' + id).append(row);
}

function showMessage(text, state) {
    $('#messages-div').empty();
    $('#messages-div').append(
        '<div class="alert alert-' + state + '">' + text + '</div>'
    );
}

function createPhenotypeTree(id, url, withContext) {
    $('#' + id).jstree({
        core: {
            multiple: false,
            data: {
                url: url
            }
        },
        plugins: [ 'contextmenu', 'dnd' ],
        contextmenu: { items: withContext ? customMenu : null }
    });

	$(document).on('dnd_move.vakata', function (e, data) {
		var t = $(data.event.target);
		var attributes = data.element.attributes;

		if (!t.closest('.jstree').length && t.closest('.drop').length) { // field with class "drop" outside of jstree
            if (attributes.type.value === "null" && t.closest('.drop').hasClass('category')) {
                data.helper.find('.jstree-icon').removeClass('jstree-er').addClass('jstree-ok');
                return;
            } else if (attributes.type.value !== "null" && t.closest('.drop').hasClass('phenotype')){
                if (t.closest('.drop')[0].id === 'reason-form-drop-area') {
                    if (attributes.abstractPhenotype.value === "true" && attributes.singlePhenotype.value == "true") {
                        data.helper.find('.jstree-icon').removeClass('jstree-er').addClass('jstree-ok');
                        return;
                    }
                } else if (t.closest('.drop')[0].id !== 'formula' || ['string', 'boolean'].indexOf(attributes.type.value) == -1) {
                    data.helper.find('.jstree-icon').removeClass('jstree-er').addClass('jstree-ok');
                    return; // formula does not accepts string or boolean
                }
            }
        }
        data.helper.find('.jstree-icon').removeClass('jstree-ok').addClass('jstree-er');
	}).on('dnd_stop.vakata', function (e, data) {
		var t = $(data.event.target);
		var attributes = data.element.attributes;

		if (!t.closest('.jstree').length && t.closest('.drop').length) { // field with class "drop" outside of jstree
		    if (attributes.type.value === "null" && t.closest('.drop').hasClass('category')) {
		        t.closest('.drop').val(t.closest('.drop').val() + ' ' + data.element.text + ' ');
		        focusInputEnd(t.closest('.drop'));
		    } else if (attributes.type.value !== "null" && t.closest('.drop').hasClass('phenotype')) {
		        if (t.closest('.drop')[0].id === 'reason-form-drop-area') {
		            if (attributes.abstractPhenotype.value === "true" && attributes.singlePhenotype.value == "true")
		                appendFormField(data.element.id, t.closest('.drop')[0]);
		        } else if (t.closest('.drop')[0].id !== 'formula' || ['string', 'boolean'].indexOf(attributes.type.value) === -1) {
                    t.closest('.drop').val(t.closest('.drop').val() + ' ' + data.element.text + ' ');
                    focusInputEnd(t.closest('.drop'));
                } // else: formula does not accept string or boolean
		    }
		}
	});
}

function appendFormField(phenotypeId, target) {
    phenotypeId = phenotypeId.replace("_anchor", "");

    var html
        = '<div class="form-group row">'
            + '<label for="' + phenotypeId + '" class="control-label col-sm-2">' + phenotypeId + '</label>'
            + '<div class="col-sm-6">'
                + '<input type="text" class="form-control" name="' + phenotypeId + '">'
            + '</div>'
            + '<a class="btn btn-danger" href="#" onclick="$(this).parent().remove()">'
                + '<i class="fa fa-times fa-lg"></i>'
            + '</a>'
        + '</div>';

    $(target).append(html);
}

function hidePhenotypeForms() {
    $('#abstract-phenotype-form, #phenotype-category-form').addClass('hidden');
    $('#numeric-phenotype-form, #string-phenotype-form, #date-phenotype-form').addClass('hidden');
    $('#calculation-phenotype-form, #boolean-phenotype-form').addClass('hidden');
}

function customMenu(node) {
	var items = {
		showCategoryForm: {
		    label: 'Create Sub Category',
		    action: function() {
		        hidePhenotypeForms();
		        $('#phenotype-category-form').removeClass('hidden');
		        $('#super-category').val(node.text);
		    }
		},
		showAbstractPhenotypeForm: {
		    label: 'Create Abstract Phenotype',
		    action: function() {
		        hidePhenotypeForms();
		        $('#abstract-phenotype-form').removeClass('hidden');
		        $('#categories').val(node.text);
		    }
		},
		showRestrictedPhenotypeForm: {
		    label: 'Create Restricted Phenotype',
		    action: function() {
		        hidePhenotypeForms();
		        switch (node.a_attr.type) {
		            case 'date': $('#date-phenotype-form').removeClass('hidden'); break;
		            case 'string': $('#string-phenotype-form').removeClass('hidden'); break;
		            case 'numeric': $('#numeric-phenotype-form').removeClass('hidden'); break;
		            case 'boolean': $('#boolean-phenotype-form').removeClass('hidden'); break;
		            case 'calculation': $('#calculation-phenotype-form').removeClass('hidden'); break;
		            default: return;
		        }

                $('form:not(.hidden) #super-phenotype').val(node.text);
		    }
		},
		inspect: {
		    label: 'Inspect',
		    action: function() {
		        console.log(node);
		        // $.getJSON(node.text, function(data) { console.log(data) });
		    }
		}
	};
	
	if (!node.a_attr.phenotype) {
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