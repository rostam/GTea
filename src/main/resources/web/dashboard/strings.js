var strings = {
    intro : "The graph can be loaded in different ways. It can be generated from an available" +
    "            list of generators. Also, it can be loaded from a given edge list, adjacency matrix," +
    "            or G6 format:",
    generator : "Here, a graph generator can be selected. The required parameters and their types appeared next to it." +
    "                All parameters should be filled separated with commas.",
    g6format  : "Here, you can paste your G6-formatted string to load that graph.",
    layout : "Now, you can draw the graph in a layout which can be selected here. The preset layout " +
    "here means the computed layout in GraphTea.",
    report: "Here, you can compute the different reports on graph. When a report selected from" +
    "        the list, the parameters, if any, appears near to it. " +
    "        After the computation, the results would appear as strings at the botton. A styled version" +
    "        can be also viewed in a styled way."
};

$('#strings-intro').html(strings.intro);
$('#strings-generator').html(strings.generator);
$('#strings-g6format').html(strings.g6format);
$('#strings-freehand').html(strings.freehand);
$("#strings-layout").html(strings.layout);
$("#strings-report").html(strings.report);
