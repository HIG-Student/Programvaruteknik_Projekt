import {Component,Output,Input,EventEmitter} from "angular2/core";

@Component({
	selector: "date-picker-view",
	templateUrl: "app/graphs/date_picker_view/date_picker_view.html",
	styleUrls: ["app/graphs/date_picker_view/date_picker_view.css"]
})
export class DatePickerView 
{
	public @Output() onPick: EventEmitter<any> = new EventEmitter();
	public @Input() resolution:string;
	
	public @Input() from:string = "";
	public @Input() to:string = "";
	
	resolutions:object = 
	[
		{
			"name": "År",
			"value": "YEAR"
		},
		{
			"name": "Kvartal",
			"value": "QUARTER"
		},
		{
			"name": "Månad",
			"value": "MONTH"
		},
		{
			"name": "Vecka",
			"value": "WEEK"
		},
		{
			"name": "Dag",
			"value": "DAY"
		}
	];
	
	verifyDate(element)
	{
		if(isNaN(Date.parse(element.value))
			element.value = "";
	}
	
	clicking()
	{
		this.onPick.emit(
		{
			"resolution": this.resolution,
			"from": this.from,
			"to": this.to
		});
	}
}