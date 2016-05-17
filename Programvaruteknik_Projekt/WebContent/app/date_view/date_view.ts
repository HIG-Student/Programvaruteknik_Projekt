import {Component,Input} from "angular2/core";

@Component({
	selector: "date-view",
	templateUrl: "app/date_view/date_view.html",
	styleUrls: ["app/date_view/date_view.css"]
})
export class DateView 
{
	@Input("date")
	public theDate;
}
