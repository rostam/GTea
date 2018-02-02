/**
 * Created by rostam on 17.07.17.
 */

function DoActions() {
    var action = $('#action').find('option:selected').text();
    var loadingEle = document.getElementById("loading");
    loadingEle.style.visibility = 'visible';
    var categoryName = getSelectedCategory();
    var databaseName = categoryName + "-" + getSelectedDatabase();

    $('#prop-table').remove();

    if (action == "Save as image") {
        var jpg64 = cy.jpg();
        window.open(jpg64);
        loadingEle.style.visibility = 'hidden';
    } else if (action == "Remove selected node") {
        if (selectedNode != null) {
            cy.elements().unselectify();
            cy.remove(selectedNode);
        }
        loadingEle.style.visibility = 'hidden';
    } else if (action == "Draw graph" || action == "Compute only") {
        $('#parent_canvas').empty();
        $('#parent_canvas').append('<div id="canvas" class="col-12"></div>');
        // cyCanvas(cytoscape); // Register extension
        cy = buildCytoscape();
        $.post(serverAddr + 'graph/' + databaseName
            + "," + getSelectedSampling() + "," + getSamplingThreshold() + "," + getSelectedLayout() + ","
            + getIterationLayout() + "," + getClusterSizes() + "," + getClusterId())
            .done(function (data) {
                useDefaultLabel = true;
                useForceLayout = true;
                $("#infos").empty();
                // update vertex and edge count after sampling
                // Object.keys(data.extra).forEach(function (t) {
                //     $("#infos").append("<tr><td>" + t + "</td><td>" + data.extra[t] + "</td>");
                // });
                if (action == "Draw graph") {
                    drawGraph(data, function () {
                        cy.fit();
                        loadingEle.style.visibility = 'hidden';
                    });
                } else {
                    loadingEle.style.visibility = 'hidden';
                }
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                alert(errorThrown);
            });
    } else if (action == "Compute labels and keys") {
        sendKeyRequest(function () {
            loadingEle.style.visibility = 'hidden';
        });
    } else if (action == "Hierarchichal Clustering") {
        HierarchichalClustering();
        loadingEle.style.visibility = 'hidden';
    } else if (action == "Viva") {
        $.post(serverAddr + 'graph/' + databaseName
            + "," + getSelectedSampling() + "," + getSamplingThreshold() + "," + getSelectedLayout() + ","
            + getIterationLayout() + "," + getClusterSizes() + "," + getClusterId())
            .done(function (data) {
                viva_action(data);
                loadingEle.style.visibility = 'hidden';
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                alert(errorThrown);
            });
    } else if(action == "Botanic Tree") {
        cy.fit();
        cy.nodes().forEach(function (n) {
           n.style('width','20');
           n.style('height','20');
           n.style('opacity','0.1');
        });
        cy.edges().forEach(function (e) {
            e.style('opacity','0.1');
        });
        //toundirected();
        var parent={};
        // cy.add({group:'nodes',data:{id:"extraroot"},position:selectedNode.position()});
        // cy.add({group:'edges',data:{id:"extraedge",source:'extraroot',target:selectedNode.data('id')}});

        // BFSrun(selectedNode,function(v,p){
        //     parent[v.data('id')] = p;
        // });
        selectedNode = cy.$(':selected');
        console.log(selectedNode);
        BFSrun(selectedNode,function(v,p){
            parent[v.data('id')] = p;
        });
        // $('#parent_canvas').empty();
        // $('#parent_canvas').append("<canvas id='myCanvas' class='col-12'></canvas>");
        // cy.fit();
        var layer = cy.cyCanvas({
            zIndex: 1,
            pixelRatio: "auto",
        });
        var canvas = layer.getCanvas();
        var ctx = canvas.getContext('2d');

        cy.on("render cyCanvas.resize", function(evt) {
            layer.resetTransform(ctx);
            layer.clear(ctx);

            // Draw fixed elements
            // ctx.fillRect(0, 0, 100, 100); // Top left corner
            layer.setTransform(ctx);

            // Draw model elements
            // cy.nodes().forEach(function(node) {
            //     var pos = node.position();
            //     ctx.fillRect(pos.x, pos.y, 100, 100); // At node position
            // });
            // var cc = document.getElementsByTagName("canvas")[2];
            // $('<canvas id="myCanvas"></canvas>').insertAfter(cc);
            // var c = document.getElementById("myCanvas");
            // var ctx = c.getContext("2d");
            // ctx.scale(0.1,0.1);
            // ctx.moveTo(-50,-50);
            cy.nodes().forEach(function (v) {
                v1 = parent[v.data('id')];
                if (v1 == undefined) return;
                v2 = parent[v1.data('id')];
                if (v2 == undefined) return;
                console.log(v + " " + v1 + " " + v2);
                p1 = v.position();
                p2 = v1.position();
                p3 = v2.position();

                m1 = getMiddlePoint(p1, p2);
                m2 = getMiddlePoint(p2, p3);
                var cp = p2;

                teta1 = getAngle(p1, p2);
                teta2 = getAngle(p1, p3);
                teta3 = getAngle(p2, p3);

                startWidth = 1;
                middleWidth = 1;
                endWidth = 1;

                pp1 = {x: m1.x - startWidth * Math.sin(teta1), y: m1.y + startWidth * Math.cos(teta1)};
                pp2 = {x: cp.x - middleWidth * Math.sin(teta2), y: cp.y + middleWidth * Math.cos(teta2)};
                pp3 = {x: m2.x - endWidth * Math.sin(teta3), y: m2.y + endWidth * Math.cos(teta3)};

                ctx.beginPath();
                ctx.moveTo(pp1.x, pp1.y);
                ctx.quadraticCurveTo(pp2.x, pp2.y, pp3.x, pp3.y);
                ctx.strokeStyle="green";
                ctx.lineWidth = 4;
                ctx.stroke();

                // ctx.fillRect(20,20,150,100);
            });
        });
    } else if(action == "Degree Distribution") {
        $('#parent_canvas').append('<div id="layer" class="col-4" style="position: absolute;left: 10px;top: 10px;z-index: 100;width: 400px;height: 150px;"></div>');
        $.post(serverAddr + 'degree/' + databaseName)
            .done(function (data) {
                var chart = new CanvasJS.Chart("layer",
                    {
                        animationEnabled: true,
                        theme: "theme2",
                        //exportEnabled: true,
                        title:{
                            text: "Degree Distribution"
                        },
                        data: [
                            {
                                type: "column", //change type to bar, line, area, pie, etc
                                dataPoints: data
                            }
                        ]
                    });
                chart.render()
                loadingEle.style.visibility = 'hidden';
            });
    }
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
    return {x:(p1.x + p2.x) / 2, y:(p1.y + p2.y) / 2};
}

function toundirected() {
    var edges = [];
    cy.edges().forEach(function (e) {
        var src = e.source().data('id');
        var tgt = e.target().data('id');
        edges.push({ group: "edges", data: { id: src+","+tgt, source: tgt, target: src }});
    });
    cy.add(edges);
}


/**
 * runs a BFS on graph, starting the given vertex as the root
 */
function BFSrun(treeRoot, f) {
    console.lgo("test 1");
    var q = [], ret = [];
    q.push(treeRoot);
    ret.push(treeRoot);
    treeRoot.setMark = true;
    treeRoot.obj = 0;
    console.lgo("test 2");
    while (q.length != 0) {
        var v = q.splice(0, 1)[0];
        console.log(v.neighborhood());
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