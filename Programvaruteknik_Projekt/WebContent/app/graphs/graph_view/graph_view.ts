import {Component} from "angular2/core";
import {DataView} from "../data_view/data_view";
import {CorrView} from "../corr_view/corr_view";
import {PickSourceView} from "../pick_source_view/pick_source_view";
import {DataLoader} from "../../data_loader";

@Component({
    selector: "graph-view",
    templateUrl: "graph_view.html",
    styleUrls: ["graph_view.css"],
    directives: [DataView, CorrView, PickSourceView]
})
export class GraphView 
{
    constructor(private dataLoader: DataLoader) { }

    sourceAJSON = null;
    sourceBJSON = null;

    setSourceA(corr, graph, data)
    {
        this.sourceAJSON = data.json;
        this.dataLoader.getSourceData({}, data.json).subscribe(data => graph.setData(data));
        this.updateCorr(corr);
    }

    setSourceB(corr, graph, data)
    {
        this.sourceBJSON = data.json;
        this.dataLoader.getSourceData({}, data.json).subscribe(data => graph.setData(data));
        this.updateCorr(corr);
    }

    updateCorr(corr)
    {
        corr.clear();
        if (this.sourceAJSON != null && this.sourceBJSON != null)
        {
            this.dataLoader.getCorrData({}, this.sourceAJSON, this.sourceBJSON).subscribe(data => corr.update(data));
        }
    }
}