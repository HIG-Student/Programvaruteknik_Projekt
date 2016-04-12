import {Component, NgZone, AfterView, OnDestroy, Input} from "angular2/core";
import {DataLoader} from "app/data_loader";

class Chart
{ 
	id: String;
	data: Object;
	height: any;
	width: any;
	constructor(config: Object) 
	{
		this.id = config['id'];
		this.data = config['data'];
		this.height = config['height'] || 300;
		this.width = config['width'] || 600;
	}
}

@Component(
{
	selector: 'zingchart',
	inputs: ['chart'],
	template: "<div id='{{chart.id}}'></div>"
})
class ZingChart implements AfterView, OnDestroy
{
	chart: Chart;
	constructor(private zone: NgZone)
	{}

	ngAfterViewInit()
	{
		this.zone.runOutsideAngular(() =>
		{
			zingchart.render(
			{
				id: this.chart['id'],
				data: this.chart['data'],
				width: this.chart['width'],
				height: this.chart['height']
			});
		});
	}
	ngOnDestroy()
	{
		zingchart.exec(this.chart['id'], 'destroy');
	}
}



@Component(
{
	selector: "corr-view",
	directives: [ZingChart],
	templateUrl: "app/graphs/corr_view/corr_view.html"
})
export class CorrView 
{
	@Input("chart-id") id: string;
	
	charts: Chart[];
	
	constructor(private dataLoader: DataLoader) { }
	
	ngOnInit()
	{
		this.name = 'Angular2';

		this.dataLoader.getDataSource().subscribe(data =>
		{
			window.data = data;
			console.log(data);
			
			this.charts = 
			[{
				id: "chart-" + this.id,
				data:
				{
					type: "scatter",
					"scale-x":
					{  
						label:
						{  
							text: data.a_name + " (" + data.a_unit + ")"
						}
					},
					"scale-y":
					{  
						label:
						{  
							text: data.b_name + " (" + data.b_unit + ")"
						}
					},
					plotarea:
					{
						margin: "75px"
					},
					"tooltip":
					{
						"html-mode": true,
						"border-width":2,
						"border-radius":5,
						"border-color":"#000",
						"text": "<table><tr><td>Datum:</td><td>%data-dates</td></tr><tr><td>%data-name-a:</td><td>%v %data-unit-a</td></tr><tr><td>%data-name-b:</td><td>%k %data-unit-b</td></tr></table>" 
					},
					series: 
					[{
						values: Object.keys(data.data).map(k=>[data.data[k].a,data.data[k].b]),
						"data-dates": Object.keys(data.data),
						"data-name-a": data.a_name,
						"data-name-b": data.b_name,
						"data-unit-a": data.a_unit,
						"data-unit-b": data.b_unit
					}],
				},
				height: 400,
				width: 600
			}]
		});
	}
}