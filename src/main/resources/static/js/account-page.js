const button = $('#submitButton');
const orig = [];
const searchFiltersInput = $('#searchFiltersInput');

$.fn.getType = function () {
    return this[0].tagName === "INPUT" ? $(this[0]).attr("type").toLowerCase() : this[0].tagName.toLowerCase();
}

$("form :input").each(function () {
    const type = $(this).getType();
    const tmp = {
        'type': type,
        'value': $(this).val()
    };
    if (type === 'radio' || type === 'checkbox') {
        tmp.checked = $(this).is(':checked');
    }
    if ($(this).attr('id') !== undefined) orig[$(this).attr('id')] = tmp;
});

function onFormChange() {
    let disable = true;
    $("form :input").each(function () {
        let type = $(this).getType();
        let id = $(this).attr('id');

        if (id !== undefined) {
            if (id.search(/searchFilter\d*$/) === -1) {
                if (type === 'text' || type === 'select') {
                    disable = (orig[id].value === $(this).val());
                } else if (type === 'radio' || type === 'checkbox') {
                    disable = (orig[id].checked === $(this).is(':checked'));
                } else if (id === 'googleMapsLong' || id === 'googleMapsLat') {
                    disable = parseFloat(orig[id].value) === parseFloat($(this).val());
                } else if (id === 'searchFiltersInput') {
                    disable = (orig[id].value === $(this).val());
                }
            }
        }

        if (!disable) {
            return false; // break out of loop
        }
    });

    button.prop('disabled', disable);
}

$('form').bind('change keyup', onFormChange);

$('#salary').on("keypress keyup blur", function (event) {
    $(this).val($(this).val().replace(/[^\d].+/, ""));
    if (event.which < 48 || event.which > 57) {
        event.preventDefault();
    }
});

$(document).ready(function() {
    initFiltTable()
});

function initFiltTable() {
    if (searchFiltersInput.val() !== "") {
        const searchFilters = searchFiltersInput.val().split(";");
        const table = document.getElementById("searchFiltersTable");
        for (let i = 0; i < searchFilters.length; i++) {
            insRow();
            table.rows[i + 1].cells[1].getElementsByTagName('input')[0].value = searchFilters[i];
        }
    }
}

function refreshFiltInp() {
    let table = document.getElementById('searchFiltersTable');
    let str = '';
    for (let i = 1; i < table.rows.length; i++) {
        let filterVal = table.rows[i].cells[1].getElementsByTagName('input')[0].value;
        if (filterVal !== '') {
            str += filterVal + ';';
        }
    }
    document.getElementById('searchFiltersInput').value = str.slice(0, str.length - 1);
}

function deleteRow(row) {
    let table = document.getElementById('searchFiltersTable');
    let i = row.parentNode.parentNode.rowIndex;
    table.deleteRow(i);

    for (let i = 1; i < table.rows.length; i++) {
        table.rows[i].cells[0].innerHTML = i;
    }
    refreshFiltInp();
    onFormChange();
}


function insRow() {
    const table = document.getElementById('searchFiltersTable');
    if (table.rows.length === 1) {
        let newRow = table.insertRow(1);
        let cell1 = newRow.insertCell(0);
        cell1.innerHTML = '1';

        let cell2 = newRow.insertCell(1);
        cell2.innerHTML = `<input size=25 type="text" id="searchFilter" />`;

        let cell2FilterInput = cell2.getElementsByTagName('input')[0];
        cell2FilterInput.addEventListener('input', onFilterInput);
        cell2FilterInput.addEventListener('keyup', refreshFiltInp);
        cell2FilterInput.addEventListener('blur', refreshFiltInp);

        let cell3 = newRow.insertCell(2);
        cell3.innerHTML = `<input type="button" id="deleteFilter" class="btn btn-danger" value="Удалить" ` +
            `onclick="deleteRow(this)" />`;
    } else {
        let newRow = table.rows[1].cloneNode(true);
        let len = table.rows.length;
        newRow.cells[0].innerHTML = len.toString();

        let inp1 = newRow.cells[1].getElementsByTagName('input')[0];
        inp1.id += len;
        inp1.value = '';
        inp1.addEventListener('input', onFilterInput);
        inp1.addEventListener('keyup', refreshFiltInp);
        inp1.addEventListener('blur', refreshFiltInp);

        table.appendChild(newRow);
    }
}

function onFilterInput(event) {
    let delimiterEntered = $(this).val().search(/;/) !== -1
    $(this).val($(this).val().replace(/;/, ""));
    // Delimiter ';' is not allowed for user
    if (delimiterEntered) {
        event.preventDefault();
    }
}
