import {Component, Output, EventEmitter} from "angular2/core";
import {DataLoader} from "../../data_loader";

@Component({
    selector: "pick-source-view",
    templateUrl: "pick_source_view.html",
    styleUrls: ["pick_source_view.css"]
})
export class PickSourceView 
{
    @Output("change-view") changeView: EventEmitter<any> = new EventEmitter();

    constructor(private dataLoader: DataLoader) { }

    json;
    inputs;
    builders;
    builders_keys;

    showDataSource()
    {
        this.dataLoader.getSourceData({}, this.json).subscribe(data =>
        {
            this.changeView.emit(
			{
				"json" : this.json,
				"data": data
			});
        });
    }

    pickSelect(value)
    {
        this.inputs = this.builders[value];

        this.json = {};
        this.json["source-type"] = value;

        for (var input of this.inputs["inputs"])
            this.setValue(input.value, input.values[0].value);
    }

    setValue(type, value)
    {
        this.json[type] = value;
    }

    ngOnInit()
    {
        this.dataLoader.getSources().subscribe(data =>
        {
            this.builders = data;
            this.builders_keys = Object.keys(this.builders);
            this.pickSelect(this.builders_keys[0]);
        });
    }
}