function drawBotanical() {
    var maxBetweennessIndex = 0;
    var res = $('#reportResults').html();
    if (res != "") {
        var arrBetweenness = JSON.parse(res);
        var max = Math.max(...arrBetweenness
    );
        maxBetweennessIndex = arrBetweenness.indexOf(max);
    } else {
        maxBetweennessIndex = 10;
    }
//            cy.fit();
    cy.nodes().forEach(function (n) {
        n.style('width', '20');
        n.style('height', '20');
        n.style('opacity', '0.4');
    });
    cy.edges().forEach(function (e) {
        e.style('opacity', '0.4');
    });
//toundirected();
    var parent = {};
    selectedNode = cy.$('#' + maxBetweennessIndex);//cy.$(':selected');
    BFSrun(selectedNode, function (v, p) {
        parent[v.data('id')] = p;
    });
    var layer = cy.cyCanvas({
        zIndex: 1,
        pixelRatio: "auto"
    });
    var canvas = layer.getCanvas();
    var ctx = canvas.getContext('2d');
    cy.on("render cyCanvas.resize", function (evt) {
        layer.resetTransform(ctx);
        layer.clear(ctx);

        // Draw fixed elements
        // ctx.fillRect(0, 0, 100, 100); // Top left corner
        layer.setTransform(ctx);

        paper.setup(document.getElementById("paperCanvas"));
//                var path = new paper.Path();
//                console.log(path);
//                path.strokeColor = 'black';
//                var start = new paper.Point(100, 100);
//                path.moveTo(start);
//                path.lineTo(start.add([ 200, -50 ]));
//                paper.view.draw();
//                return;
        var paths = [];
        var startEnds = {};
        cy.nodes().forEach(function (v) {
            v1 = parent[v.data('id')];
            if (v1 == undefined) return;
            v2 = parent[v1.data('id')];
            if (v2 == undefined) return;
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

            var path = new paper.Path();
            var start = new paper.Point(pp1.x, pp1.y);
            var handle = new paper.Point(pp2.x, pp2.y);
            var to = new paper.Point(pp3.x, pp3.y);
            path.moveTo(start);
            path.quadraticCurveTo(handle, to);
            var vec = [];
            vec[0] = v.data('id');
            vec[1] = v1.data('id');
            vec[2] = v2.data('id');
            vec.sort();
            paths.push({path: path, vers: vec});
            startEnds[pp1.x + "," + pp1.y] = 0;
            startEnds[pp3.x + "," + pp3.y] = 0;
//                    path.strokeColor = "green";
//                    path.view.draw();

            ctx.beginPath();
            ctx.moveTo(pp1.x, pp1.y);
            ctx.quadraticCurveTo(pp2.x, pp2.y, pp3.x, pp3.y);
            ctx.strokeStyle = "green";
            ctx.lineWidth = 1;
            ctx.stroke();
        });
//                paper.zoom(0.2);
        var cnt = 0;
        var uniqueIntersections = {};
        for (var i = 0; i < paths.length; i++) {
            for (var j = i + 1; j < paths.length; j++) {
//                        paths[i].getTangentAt(paths[i].getOffsetOf())
//                        console.log(intersectionOfArrays(paths[i].vers,paths[j].vers));
//                        if(sizeOfIntersectionOfArrays(paths[i].vers,paths[j].vers) < 2) {
                var intersections = paths[i].path.getIntersections(paths[j].path);
                cnt += intersections.length;
                for (var k = 0; k < intersections.length; k++) {
                    var pp = intersections[k].point;
                    if (pp != undefined) {
                        if (uniqueIntersections[pp.x + "," + pp.y] == undefined)
                            uniqueIntersections[pp.x + "," + pp.y] = 0;
                        uniqueIntersections[pp.x + "," + pp.y]++;
                        //ctx.strokeStyle = "red";
                        //ctx.stroke();
                    }
                }
//                        }
            }
        }
        console.log(cnt);
        var cnt2 = 0;
        Object.keys(uniqueIntersections).forEach(function (t) {
            if (uniqueIntersections[t] == 1) {
                var xy = t.split(",");
                var isStartEnd = false;
                Object.keys(startEnds).forEach(function (t2) {
                    var xy2 = t2.split(",");
                    var x2 = parseFloat(xy2[0]);
                    var y2 = parseFloat(xy2[1]);
                    var x1 = parseFloat(xy[0]);
                    var y1 = parseFloat(xy[1]);
                    var dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                    if (dist < 1) isStartEnd = true;
                });
                if (!isStartEnd) {
                    ctx.beginPath();
                    ctx.arc(parseFloat(xy[0]), parseFloat(xy[1]), 2, 0, 2 * Math.PI);
                    ctx.fill();
                    cnt2++;
                }
            }
        });
        console.log(cnt2);
        first = false;
    });
}