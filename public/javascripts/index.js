var fundsArray = [];

function updateFunds() {
    $('#funds-loader').removeClass("hidden");
    $.get("/api/fund/list", function (funds) {
        $.each(funds, function (index, fund) {
            fundsArray.push(fund);

        })
    }).done(function () {
        populateFundTable();
        calcStats();
        $('#funds-loader').addClass("hidden");
    })
}

function populateFundTable() {
    $.each(fundsArray, function (index, fund) {
    var $row = $("<tr>");
    $row.append($("<td>").text(fund.name));
    $row.append($("<td>").text(fund.url));
    $('#funds-table').find('tbody').append($row);
    });
}

function calcStats(){
    var verified = 0;
    var unverified = 0;
    $.each(fundsArray, function(fund){
        if(fund.verified) verified += 1;
        else unverified += 1;
    });
    var total = verified + unverified;
    var verPrc = (verified / (verified + unverified) * 100).toString() + "%";
    var unverPrc = (unverified / (verified + unverified) * 100).toString() + "%";

    var $total = $("<h6>").text("Total: " + total);
    var $verified = $("<h6>").text("Verified: " + verPrc + " (" + verified + ")");
    var $unverified = $("<h6>").text("Unverified: " + unverPrc + " (" + unverified + ")");

    var $panelBody = $("#statistics-panel").find(".panel-body");
    $panelBody.append($total);
    $panelBody.append($verified);
    $panelBody.append($unverified);


}