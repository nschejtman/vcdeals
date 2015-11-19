var hub = (function(){
    var $updateDealModal = $('#updateDealModal');
    var $updateDealModalBody = $updateDealModal.find('.modal-body');
    var $updateTemplate = $('#update-template');

    function toggleUpdateModal(deal){
        $updateDealModalBody.html(Mustache.render($updateTemplate.html(), deal));
        if(deal.verified) $updateDealModalBody.find('input[type=checkbox]').prop('checked', true);
        else $updateDealModalBody.find('input[type=checkbox]').prop('checked', false);
        $updateDealModal.modal('toggle');
    }

    function updateDeal(){
        var data = {
            id: $($updateDealModalBody.find('input')[0]).val(),
            name: $($updateDealModalBody.find('input')[1]).val(),
            url: $($updateDealModalBody.find('input')[2]).val(),
            verified: $($updateDealModalBody.find('input')[3]).is(':checked')
        };
        $.ajax({
            url: '/api/deal',
            method: 'put',
            data: data
        }).success(location.reload());
    }

    return {
        updateDeal : updateDeal,
        toggleUpdateModal : toggleUpdateModal
    }
})();



$(document).ready(updateDeals);

function updateDeals() {
    $.get("/api/deal/list").done(renderDeals);
}

function renderDeals(deals) {
    var template = $('#template').html();
    var rendered = Mustache.render(template, {deals: deals});
    $("#deal-table").find("tbody").html(rendered);
}

function search() {
    var rows = $("#deal-table").find("tbody tr");
    rows.hide();
    var searchText = $("#search").val().toLowerCase();
    rows.each(function () {
        var $this = $(this);
        var name = $this.find("td:first").text().toLowerCase();
        var url = $this.find("td:nth-child(2)").text().toLowerCase();
        if (name.indexOf(searchText) != -1 || url.indexOf(searchText) != -1) {
            $this.show();
        }
    });
}

var $modal = $("#url-extraction-modal");
var $modalLoader = $modal.find(".loader");
var $modalTable = $("#deal-extraction-table");

function extractFromUrl() {
    $modal.modal().toggle(true);
    $modalLoader.toggleClass("hidden", false);
    $modalTable.toggleClass("hidden", true);
    var $input = $("#extraction-url");
    $.ajax({
        url: "/api/deal/extract/url",
        method: "POST",
        data: {url: $input.val()},
        timeout: 900000
    }).done(renderUrlExtractionResults);
}

var extractedDeals;

function renderUrlExtractionResults(deals) {
    $modalLoader.toggleClass("hidden", true);
    $modalTable.toggleClass("hidden", false);

    extractedDeals = deals;
    var auxArray = [];
    for (idx in deals) auxArray.push({'i': idx, 'deal': deals[idx]});

    //Mustache!
    var template = $('#editable-template').html();
    var rendered = Mustache.render(template, {deals: auxArray});

    $modalTable.find("tbody").html(rendered);
}

function saveExtractionResults() {
    $("#deal-list-form").submit();
}

$("#search").keyup(search);

    