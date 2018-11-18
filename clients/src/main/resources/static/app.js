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
            $('.action').append("<button type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#newApp\">Submit New Bond Application</button>")
        }

        if (me == "Exporter"){

            console.log("I'm an exporter. I'm retrieving all bond applications")

            $.ajax({
                type: "POST",
                url: apiBase + "bonds"
            }).then(function(res) {

                console.log("retrieved all bonds")

                var str = ""

                res.bonds.forEach(function(item){
                    str += "<li class=\"list-group-item\" id=\"bond\"" + item.n + ">\n"
                    str += "<p><span> Bond Name: " + item.id  + "</span> </p>\n"
                    str += "<p> \n "
                    str += "<button class=\"btn btn-primary\" type=\"button\" data-toggle=\"collapse\" data-target=\"#bond" + item.n + "Exporter\" aria-expanded=\"false\" aria-controls=\"bond" + item.n + "Exporter\">\n"
                    str += "Exporter data"
                    str += "</button>\n"
                    str += "<button class=\"btn btn-primary\" type=\"button\" data-toggle=\"collapse\" data-target=\"#bond" + item.n + "Bank\" aria-expanded=\"false\" aria-controls=\"bond" + item.n + "Bank\">\n"
                    str += "Bank data"
                    str += "</button>\n"
                    str += "<button class=\"btn btn-primary\" type=\"button\" data-toggle=\"collapse\" data-target=\"#bond" + item.n + "UKEF\" aria-expanded=\"false\" aria-controls=\"bond" + item.n + "UKEF\">\n"
                    str += "UKEF data"
                    str += "</button>\n"
                    str += "</p> \n "
                    str += " <div class=\"row\">\n"
                    str += " <div class=\"col\">\n"
                    str += "<div class=\"collapse multi-collapse\" id=\"bond" + item.n + "Exporter\">\n"
                    str += "<div class=\"card card-body\">\n"
                    str += "<p> Bond value: " + item.value + "  </p>\n"
                    str += "</div>\n"
                    str += "</div>\n"
                    str += "</div>\n"
                    str += " <div class=\"col\">\n"
                    str += "<div class=\"collapse multi-collapse\" id=\"bond" + item.n + "Bank\">\n"
                    str += "<div class=\"card card-body\">"
                    str += "<p> Credit Score: " + item.creditScore + "  </p>\n"
                    str += "</div>\n"
                    str += "</div>\n"
                    str += "</div>\n"
                    str += " <div class=\"col\">\n"
                    str += "<div class=\"collapse multi-collapse\" id=\"bond" + item.n + "UKEF\">\n"
                    str += "<div class=\"card card-body\">\n"
                    str += "<p> UKEF Support: " + item.ukef + "  </p>\n"
                    str += "</div>\n"
                    str += "</div>\n"
                    str += "</div>\n"
                    str += "</div>\n"
                });

                $('#bond').append(str);
            });

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