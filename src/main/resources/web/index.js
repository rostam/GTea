//var serverAddr = "http://127.0.0.1:2342/";
var serverAddr = "http://0.0.0.0:2342/";
// var serverAddr = "http://csc.inf-ra.uni-jena.de:80/";
var nodeId = 0;
var cy; //cytoscape object
var selectedNode;
var uuid = guid();
var directed = 'triangle', undirected = 'none';
//var parallels = [];
var report_results;

var original_data = {};
$.get(serverAddr + 'graphs/')
    .done(function (data) {
        original_data = data;
        var categoriesSelect = $('#categories');
        data.graphs.forEach(function (d) {
            categoriesSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = {desc: d.desc, props: d.properties};
        });

        categoriesSelect.html(categoriesSelect.find('option').sort(function(x, y) {
            return $(x).text() > $(y).text() ? 1 : -1;
        }));

        categoriesSelect.on('change', function () {
            var category = getSelectedCategory();
            var keys = "", vals = "";
            original_data[category].props.forEach(function (d) {
                var propNamesTypes = d.split(":");
                keys += propNamesTypes[0] + ", ";
                vals += propNamesTypes[2] + ", ";
            });
            keys = keys.substr(0, keys.length - 2);
            vals = vals.substr(0, vals.length - 2);
            $('#props_keys').html(keys);
            $('#props_vals').val(vals);

            var desc = original_data[category].desc;
            $('#tooltiptext').html(desc);
            var linkName = category;
            var ind = category.indexOf("Generator");
            if(ind != -1) linkName = category.substr(0,ind);
            ind = category.indexOf("Graph");
            if(ind == -1) linkName += "Graph";
            linkName += ".html";
            $('#gen_link').attr('href','http://mathworld.wolfram.com/'+linkName);
        });

        var reportsSelect = $('#reports');
        data.reports.forEach(function (d) {
            reportsSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = {desc: d.desc, props: d.properties};
        });

        reportsSelect.html(reportsSelect.find('option').sort(function(x, y) {
            return $(x).text() > $(y).text() ? 1 : -1;
        }));

        reportsSelect.on('change', function () {
            var report = getSelectedReport();
            var props = $('#reportProps');
            props.empty();
            var keys="",vals="";
            original_data[report].props.forEach(function (d) {
                var propNamesTypes = d.split(":");
                keys+=propNamesTypes[0]+", ";vals+=propNamesTypes[2]+", ";
            });
            keys=keys.substr(0,keys.length-2);
            vals=vals.substr(0,vals.length-2);
            if(keys=="") props.append("No parameters<br/>");
            else props.append('<span id="reportPropsKeys">' +keys + '</span>: ' +
                '<input id="reportPropsVals"' + 'name="' + keys + '"' + ' value="' + vals + '">');

            var desc = original_data[report].desc;
            $('#tooltiptextReport').html(desc);
        });

        var algorithmsSelect = $('#graphAlgorithms');
        data.algorithms.forEach(function (d) {
            algorithmsSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = {desc: d.desc, props: d.properties};
        });

        algorithmsSelect.html(algorithmsSelect.find('option').sort(function(x, y) {
             return $(x).text() > $(y).text() ? 1 : -1;
        }));

        var actionsSelect = $('#graphActions');
        data.actions.forEach(function (d) {
            actionsSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = {desc: d.desc, props: d.properties};
        });

        actionsSelect.on('change', function () {
            var action = getSelectedGraphAction();
            var props = $('#graphActionsProps');
            props.empty();
            var keys="",vals="";
            original_data[action].props.forEach(function (d) {
                var propNamesTypes = d.split(":");
                keys+=propNamesTypes[0]+", ";vals+=propNamesTypes[1]+", ";
            });
            keys=keys.substr(0,keys.length-2);
            vals=vals.substr(0,vals.length-2);
            if(keys=="") props.append("No parameters<br/>");
            else props.append('<span id="graphActionPropsKeys">' +keys + '</span>: ' +
                '<input id="graphActionPropsVals"' + 'name="' + keys + '"' + ' value="' + vals + '">');

            var desc = original_data[action].desc;
            $('#tooltiptextGraphAction').html(desc);
        });
    })
    .fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });

$.get(serverAddr + 'mats/')
    .done(function (data) {
        data.forEach(function (d) {
            $('#existing_mat').append("<option>" + d + "</option>");
        });
    });

function graphAction(){
    var actionProps = "";
    $('#graphActionsProps').children('input').each(function (i, item) {
        actionProps += item.name + ":" + item.value + "-"
    });
    if (actionProps == "") {
        actionProps = "no";
    }
    $.get(serverAddr + 'action/'
        + $('#graphActions').find('option:selected').text() + "--"
        + ($('#props_keys').html() + ":" + $('#props_vals').val()) + "--"
        + ($('#reportPropsKeys').html() + ":" + $('#reportPropsVals').val())
        + "--" + uuid)
        .done(function (data) {

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}

function graphAlgorithm(status) {
    var type = $('#graphType').find('option:selected').text();
    jQuery.ajax({
        url: 'http://0.0.0.0:2342/add', type: 'POST', contentType: 'application/json',
        data: JSON.stringify({
            "type": "algorithm",
            "name": $('#graphAlgorithms').find('option:selected').text(),
            "graph": $('#categories').find('option:selected').text(),
            "uuid": uuid,
//            "propsKeys": $('#reportPropsKeys').html(),
//            "propsVals": $('#reportPropsVals').val(),
            "directed": type,
            "status": status
        }),
        dataType: 'json'
    })
    //
    // $.get(serverAddr + 'report/'
    //     + $('#categories').find('option:selected').text() + "--"
    //     + $('#reports').find('option:selected').text() + "--"
    //     + ($('#props_keys').html() + ":" + $('#props_vals').val()) + "--"
    //     + ($('#reportPropsKeys').html() + ":" + $('#reportPropsVals').val())
    //     + "--" + uuid)
        .done(function (data) {
            console.log(data);
            var arr = data.steps;
            if(status == 'load_algorithm') {
              for(var stepCounter = 0; stepCounter < arr.length;stepCounter++) {
                stepMessage = arr[stepCounter].split("+++");
                if(stepMessage[0].indexOf('RequestVertex') != -1) {
                    status += "rv";
                }
              }
            } else {
                var id = null;
            function myMove(data) {
              var stepCounter = 0;
              clearInterval(id);
              id = setInterval(frame, 1000);
              function frame() {
                if (stepCounter == arr.length - 1) {
                  clearInterval(id);
                } else {
                  if(arr[stepCounter].indexOf("Step") == -1) return;
                  stepMessage = arr[stepCounter].split("+++");
                  tmp = stepMessage[0].replace("<br/>","").replace("Step==","");
                  if(tmp.length > 1)
                    $("#algResults").html($("#algResults").html()+tmp+"\n\n");
                  var colors = stepMessage[2].split(',');
                  var edge_colors = stepMessage[4].split(',');
                  stepCounter++;
                  console.log(stepCounter);
                  for(i=0;i<colors.length-1;i++) {
                    cy.nodes('[id = "' + i +  '"]').style('background-color', distinctColors[Object.keys(distinctColors)[colors[i]]]);
                  }
                  for(i=0;i<edge_colors.length-1;i++) {
                    cy.edges('[id = "' + i +  '"]').style('color', distinctColors[Object.keys(distinctColors)[edge_colors[i]]]);
                  }
                }
              }
            }
            myMove(data);
            }
//            console.log(arr);
//            for(var i=0;i<arr.length;i++) {
//                var colors = arr[i].split("+++")[2].split(',');
//                console.log(colors);
//                for(i=0;i<colors.length-1;i++) {
//                    cy.nodes('[id = "' + i +  '"]').style('background-color', 'green');
//                }
        //}
        return;
            report_results = data;
            if (data.titles != undefined) {
                $('#reportResults').html(JSON.stringify(data));
                var titles = data.titles.substr(1, data.titles.indexOf("]") - 1);
                var tts = titles.split(",");
                var builtHTML = "<table class='fixed_headers'><thead><tr>";
                tts.forEach(function (t) {
                    builtHTML += "<th>" + t + "</th>";
                });
                var results = JSON.parse(data.results);
                builtHTML += "</tr></thead><tbody>";
                results.forEach(function (row) {
                    builtHTML += "<tr>";
                    row.forEach(function (col) {
                        builtHTML += "<td>" + col + "</td>";
                    })
                    builtHTML += "</tr>";
                });
                builtHTML += "</tr></tbody></table>";
                $('#results-body').html(builtHTML);
            } else {
                var res = "";
                Object.keys(data).forEach(function (t) {
                    res+= t + ":" + JSON.stringify(data[t]) + ",";
                });
                res = res.substr(0,res.length-1);
                $('#reportResults').html(res);
                $('#results-body').html(res);
            }

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}


function Report() {
    var type = $('#graphType').find('option:selected').text();
    $('#reportResults').html('computing...');
    var reportProps = "";
    $('#reportProps').children('input').each(function (i, item) {
        reportProps += item.name + ":" + item.value + "-"
    });
    if (reportProps == "") {
        reportProps = "no";
    }
    jQuery.ajax({
        url: 'http://0.0.0.0:2342/add', type: 'POST', contentType: 'application/json',
        data: JSON.stringify({
            "type": "report",
            "name": $('#reports').find('option:selected').text(),
            "graph": $('#categories').find('option:selected').text(),
            "uuid": uuid,
            "propsKeys": $('#reportPropsKeys').html(),
            "propsVals": $('#reportPropsVals').val(),
            "directed": type
        }),
        dataType: 'json'
    })
    //
    // $.get(serverAddr + 'report/'
    //     + $('#categories').find('option:selected').text() + "--"
    //     + $('#reports').find('option:selected').text() + "--"
    //     + ($('#props_keys').html() + ":" + $('#props_vals').val()) + "--"
    //     + ($('#reportPropsKeys').html() + ":" + $('#reportPropsVals').val())
    //     + "--" + uuid)
        .done(function (data) {
            report_results = data;
            if (data.titles != undefined) {
                $('#reportResults').html(JSON.stringify(data));
                var titles = data.titles.substr(1, data.titles.indexOf("]") - 1);
                var tts = titles.split(",");
                var builtHTML = "<table class='fixed_headers'><thead><tr>";
                tts.forEach(function (t) {
                    builtHTML += "<th>" + t + "</th>";
                });
                var results = JSON.parse(data.results);
                builtHTML += "</tr></thead><tbody>";
                results.forEach(function (row) {
                    builtHTML += "<tr>";
                    row.forEach(function (col) {
                        builtHTML += "<td>" + col + "</td>";
                    })
                    builtHTML += "</tr>";
                });
                builtHTML += "</tr></tbody></table>";
                $('#results-body').html(builtHTML);
            } else {
                var res = "";
                Object.keys(data).forEach(function (t) {
                    res+= t + ":" + JSON.stringify(data[t]) + ",";
                });
                res = res.substr(0,res.length-1);
                $('#reportResults').html(res);
                $('#results-body').html(res);
            }

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}

function load_generator(isDraw,webgl,ended) {
    var lay = $('#layouts').find('option:selected').text();
    var type = $('#graphType').find('option:selected').text();
    if (lay == "Botanical Tree") {
        drawBotanical();
        return;
    }
    jQuery.ajax({
        url: 'http://0.0.0.0:2342/add', type: 'POST', contentType: 'application/json',
        data: JSON.stringify({
            "type": "gen",
            "name": $('#categories').find('option:selected').text(),
            "graph": $('#categories').find('option:selected').text(),
            "uuid": uuid,
            "propsKeys": $('#props_keys').html(),
            "propsVals": $('#props_vals').val(),
            "directed": type
        }),
        dataType: 'json'
    })
        .done(function (data) {
            if (isDraw) {
                if (!webgl) {
                    $('#parent_canvas').empty();
                    $('#parent_canvas').append("<div id='canvas' class='main'>")
                    initCytoscape(undirected, serverAddr, uuid);
                    nodeId = 0; //resets counter for freehand vertices
                    var nodes = data.nodes;
                    var edges = data.edges;
                    cy.elements().remove();
                    cy.add(nodes);
                    cy.add(edges);
                    nodeId += nodes.length; //adds the current amount of nodes, so the next freehand item will be max(ids)+1
                    setVertexIds();
                    applyLayout();
                    ended();
                } else {
                    viva_action(data);
                    ended();
                }
            }
        }).fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });

    // server(serverAddr + 'draw/'
    //   + $('#categories').find('option:selected').text() + "--"
    //   + $('#reports').find('option:selected').text() + "--" +
    //   ($('#props_keys').html() + ":" + $('#props_vals').val())
    //   + "--" + type
    //   + "--" + uuid, function (data) {
    // })
}


function getSelectedCategory() {
    return $('#categories').find('option:selected').text();
}

function getSelectedReport() {
    return $('#reports').find('option:selected').text();
}

function getSelectedGraphAction() {
    return $('#graphActions').find('option:selected').text();
}

/**
 * runs a BFS on graph, starting the given vertex as the root
 */
function BFSrun(treeRoot, f) {
    var q = [], ret = [];
    q.push(treeRoot);
    ret.push(treeRoot);
    treeRoot.setMark = true;
    treeRoot.obj = 0;
    while (q.length != 0) {
        var v = q.splice(0, 1)[0];
        v.neighborhood().forEach(function (vertex) {
            if (vertex.setMark == undefined || vertex.setMark == false) {
                vertex.setMark = true;
                vertex.obj = v.obj + 1;
                q.push(vertex);
                ret.push(vertex);
                f(vertex, v);
            }
        });
    }
    return ret;
}

function getAngle(p1, p2) {
    var angle = Math.atan2(p1.y - p2.y,
        p1.x - p2.x);
    if (angle < 0) {
        // atan2 returns getAngle in phase -pi to pi, which means
        // we have to convert the answer into 0 to 2pi range.
        angle += 2 * Math.PI;
    }
    return angle;
}

function getMiddlePoint(p1, p2) {
    return {x: (p1.x + p2.x) / 2, y: (p1.y + p2.y) / 2};
}

function sizeOfIntersectionOfArrays(arr1, arr2) {
    var cnt = 0;
    if (arr1.indexOf(arr2[0]) == -1) cnt++;
    if (arr1.indexOf(arr2[1]) == -1) cnt++;
    if (arr1.indexOf(arr2[2]) == -1) cnt++;
    return cnt;
}

$('.loaders').hide();
$('#generators').show();

function selectLoader() {
    var loader = $('#loaders').find('option:selected').text();
    $('.loaders').hide();
    switch (loader) {
        case "Generators":
            $('#generators').show();
            $('#act_name').html("gen");
            break;
        case "Edge list":
            $('#elformat').show();
            $('#act_name').html("el");
            break;
        case "From matrix":
            $('#adjMatformat').show();
            $('#act_name').html("adj");
            break;
        case "G6 format":
            $('#g6format').show();
            $('#act_name').html("g6");
            break;
        case "File":
            $('#fileformat').show();
            $('#act_name').html("file");
            break;
        case "Freehand drawing":
            $('#freehandformat').show();
            $('#act_name').html("free");
    }
}

function load_free(ended) {
//if($('#parent_canvas').find("div").length == 1) {
    $('#parent_canvas').empty();
    $('#parent_canvas').append("<div id='canvas' class='main'>")
    initCytoscape(undirected, serverAddr, uuid);
    cy.elements().remove();
    cy.layout({name: 'cose'}).run();
//}
ended();
}

function load_graph(type,isDraw,webgl,ended) {
    var str = $('#' + type + 'string').val().replace(/\n/g, "-");
    var isDirected = $('#graphType').find('option:selected').text();
    var adjMatType = $('#adjmat-type').find('option:selected').val();
    var matName = $('#existing_mat').find('option:selected').text()
    if (type == "g6") {
        var str2 = "";
        for (var i = 0; i < str.length; i++)
            if (str[i] == "?") str2 += "qqq";
            else str2 += str[i];
        str = str2;
    }
    if(type == 'adj') type += adjMatType;
    if(matName!= 'No selection') {
        str = matName;
    }
    server(serverAddr + 'loadGraph' + '/' + type + "--"
        + str + "--" + isDirected + "--" + uuid, function (data) {
        if (isDraw) {
            if(!webgl) {
                // cy = cytoscape({
                //     container: document.getElementById('canvas'),
                //     style: [ // the stylesheet for the graph
                //         {
                //             selector: 'node',
                //             style: {
                //                 'background-color': 'lightgray',
                //                 'label': 'data(id)',
                //                 'text-valign': 'center'
                //             }
                //         }]
                // });
                $('#parent_canvas').empty();
                $('#parent_canvas').append("<div id='canvas' class='main'>")
                initCytoscape(undirected, serverAddr, uuid);
                var nodes = data.nodes;
                var edges = data.edges;
                cy.elements().remove();
                cy.add(nodes);
                cy.add(edges);
                cy.layout({name: 'cose'}).run();
                ended();
            } else {
                viva_action(data);
                ended();
            }
        }
    });
}

function showOnGraph() {
    var act_name = $('#act_name').html();

    if (report_results.colors != undefined) {
        var colors = report_results.colors;
        cy.nodes().forEach(function (n) {
            var color = colors[parseInt(n.id())];
            var actualColor = distinctColors[Object.keys(distinctColors)[color]];
            n.style('background-color', actualColor);
            n.style('background-opacity', 0.8);
        });
        cy.nodes().style('background-opacity', 0.8);
    }
}


// function selectGenerator() {
//     var desc = original_data[$('#categories').find('option:selected').text()].desc;
//     $('#tooltiptext').html(desc);
// }








