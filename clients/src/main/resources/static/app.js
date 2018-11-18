"use strict";

const apiBase = "/uktf/";

$(document).ready(function() {

    var me = ""

    $.ajax({
        type: "POST",
        url: apiBase + "me"
    }).then(function(data) {
       me = data.me.organisation;
       $('.thisNode').append(data.me.organisation);

        if (me == "Exporter"){
            $('.action').append("<button type=\"button\" class=\"btn btn-outline-success\"> Submit New Application </button>")
        }

        if (me == "Exporter"){
            $('#bond').append("<li class=\"list-group-item\"> ciao exporter </li>");
        }

        if (me == "UKEF"){
                $('.bond').append("<li> ciao ukef </li");
        }

        if (me == "Bank"){
                 $('.bond').append("<li> ciao bank </li");
        }
    });

    $.ajax({
        type: "POST",
        url: apiBase + "peers"
    }).then(function(data) {
       $('.otherNode1').append(data.peers[0].organisation);
       $('.otherNode2').append(data.peers[1].organisation);
    });

});


