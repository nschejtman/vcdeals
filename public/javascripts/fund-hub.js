var hub = (function(){
    var $createFundForm = $('#createFundForm');
    var $newFundName = $($createFundForm.find('input')[1]);
    var $newFundUrl = $($createFundForm.find('input')[2]);

    var $updateFundModal = $('#updateFundModal');
    var $updateFundModalBody = $updateFundModal.find('.modal-body');
    var $updateFundId = $($updateFundModalBody.find('input')[0]);
    var $updateFundName = $($updateFundModalBody.find('input')[1]);
    var $updateFundUrl = $($updateFundModalBody.find('input')[2]);
    var $updateFundVerified = $($updateFundModalBody.find('input')[3]);
    var $updateTemplate = $('#update-template');


    function createFund(){
        if($newFundName.val() == '' || $newFundUrl.val() == ''){
            alert('Some fields are incomplete!');
        } else {
            $createFundForm.submit();
        }
    }

    function toggleUpdateModal(fund){
        $updateFundModalBody.html(Mustache.render($updateTemplate.html(), fund));
        $updateFundModal.modal('toggle');
    }

    function updateFund(){
        var data = {
            id: $($updateFundModalBody.find('input')[0]).val(),
            name: $($updateFundModalBody.find('input')[1]).val(),
            url: $($updateFundModalBody.find('input')[2]).val(),
            verified: $($updateFundModalBody.find('input')[3]).is(':checked')
        };
        $.ajax({
            url: '/api/fund',
            method: 'put',
            data: data
        }).success(location.reload());
    }

    return {
        createFund : createFund,
        updateFund : updateFund,
        toggleUpdateModal : toggleUpdateModal
    }
})();

$(document).ready(updateFunds);

function updateFunds(){
    $.get("/api/fund/list").done(renderFunds);
}

function renderFunds(funds){
    var template = $('#template').html();
    Mustache.parse(template);   // optional, speeds up future uses
    var rendered = Mustache.render(template, {funds: funds});
    $("#fund-table").find("tbody").html(rendered);
}

function search(){
    var rows = $("#fund-table").find("tbody tr");
    rows.hide();
    var searchText = $("#search").val().toLowerCase();
    rows.each(function(){
        var $this = $(this);
        var name = $this.find("td:first").text().toLowerCase();
        var url = $this.find("td:nth-child(2)").text().toLowerCase();
        if(name.indexOf(searchText) != -1 || url.indexOf(searchText) != -1){
            $this.show();
        }
    });
}

function updateLavca(){
    $.get("/api/fund/lavca").done(renderFunds);
}

function updateEmpea(){
    $.get("/api/fund/empea").done(renderFunds);
}

$("#search").keyup(search);
