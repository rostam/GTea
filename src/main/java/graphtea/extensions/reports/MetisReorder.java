// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphtea.extensions.reports;

import graphtea.extensions.reports.coloring.NDMetis;
import graphtea.graph.graph.GraphModel;
import graphtea.plugins.reports.extension.GraphReportExtension;

/**
 * @author Azin Azadi
 */
public class MetisReorder implements GraphReportExtension {

    public String getName() {
        return "Metis Reorder";
    }

    public String getDescription() {
        return "The reordering by metis";
    }

    public Object calculate(GraphModel g) {
        NDMetis nd = new NDMetis("Test",g);
        return nd.getOrder();
    }

    @Override
    public String getCategory() {
        return "Reordering";
    }

}
